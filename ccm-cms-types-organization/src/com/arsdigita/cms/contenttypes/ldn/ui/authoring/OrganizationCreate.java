/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ldn.ui.authoring;

import com.arsdigita.cms.contenttypes.ldn.Organization;
import com.arsdigita.cms.contenttypes.ldn.util.OrganizationGlobalizationUtil;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;



/**
 *  The create step for Organization content items.
 *
 *  @author <a href="mailto:dturner@redhat.com">David Turner</a>
 *  @version $Id: OrganizationCreate.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class OrganizationCreate extends PageCreate {

    public static final String LINK = "link";
    public static final String CONTACT = "contact";

    private CreationSelector m_parent;


    public OrganizationCreate ( ItemSelectionModel itemModel,
                                CreationSelector parent ) {

        super(itemModel, parent);
        m_parent = parent;
    }


    protected void addWidgets () {

        super.addWidgets();

        TextField link = new TextField(LINK);
        add(new Label(OrganizationGlobalizationUtil
                      .globalize("cms.contenttypes.ui.organization.link")));
        add(link);

        TextField contact = new TextField(CONTACT);
        add(new Label(OrganizationGlobalizationUtil
                      .globalize("cms.contenttypes.ui.organization.contact")));
        add(contact);
    }


    @Override
    public void process ( FormSectionEvent e ) throws FormProcessException {

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        ItemSelectionModel m = getItemSelectionModel();

        // try to get the contact section from the state parameter
        Folder f = m_parent.getFolder(state);
        ContentSection sec = m_parent.getContentSection(state);
        Organization org = (Organization) createContentPage(state);
        org.setLanguage((String) data.get(LANGUAGE));                
        org.setName((String)data.get(NAME));
        org.setTitle((String)data.get(TITLE));
        org.setLink((String)data.get(LINK));
        org.setContact((String)data.get(CONTACT));
        org.save();

        final ContentBundle bundle = new ContentBundle(org);
        bundle.setParent(f);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();

        // aplaws default workflow
        getWorkflowSection().applyWorkflow(state, org);


        m_parent.editItem(state, org);
    }

}
