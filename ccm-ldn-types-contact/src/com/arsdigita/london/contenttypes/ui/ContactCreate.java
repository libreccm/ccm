/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.london.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.london.contenttypes.Contact;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.PageCreate;

/**
 * Authoring kit create component to create objects of <code>Contact</code> 
 * ContentType objects. Replaces the default PageCreate class usually used
 * for most content items.
 *
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ContactCreate.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContactCreate extends PageCreate {

    private CreationSelector m_parent;

    public ContactCreate(ItemSelectionModel itemModel,
            CreationSelector parent) {

        super(itemModel, parent);
        m_parent = parent;
    }

    @Override
    protected void addWidgets() {

        /* Add the standard widgets title, name, and optional launchdate     */
        super.addWidgets();

        TextField givenName = new TextField(Contact.GIVEN_NAME);
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.givenname")));
        givenName.addValidationListener(new NotNullValidationListener());
        add(givenName);

        TextField familyName = new TextField(Contact.FAMILY_NAME);
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.familyname")));
        familyName.addValidationListener(new NotNullValidationListener());
        add(familyName);
    }

    /**
     * 
     * @param e
     * @throws FormProcessException 
     */
    @Override
    public void process(FormSectionEvent e) throws FormProcessException {

        FormData data = e.getFormData();
        PageState state = e.getPageState();

        // try to get the contact section from the state parameter
        Folder f = m_parent.getFolder(state);
        ContentSection sec = m_parent.getContentSection(state);
        Contact contact = (Contact) createContentPage(state);
        contact.setLanguage((String) data.get(LANGUAGE));
        contact.setName((String) data.get(NAME));
        contact.setTitle((String) data.get(TITLE));
        contact.setGivenName((String) data.get(Contact.GIVEN_NAME));
        contact.setFamilyName((String) data.get(Contact.FAMILY_NAME));
        contact.save();

        final ContentBundle bundle = new ContentBundle(contact);
        bundle.setParent(f);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();

        // aplaws default workflow
        getWorkflowSection().applyWorkflow(state, contact);

        m_parent.editItem(state, contact);
    }
}
