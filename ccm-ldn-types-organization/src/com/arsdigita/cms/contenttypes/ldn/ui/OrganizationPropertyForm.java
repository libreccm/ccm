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
package com.arsdigita.cms.contenttypes.ldn.ui;

import com.arsdigita.cms.contenttypes.ldn.ui.OrganizationPropertiesStep;
import com.arsdigita.cms.contenttypes.ldn.Organization;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
// import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.contenttypes.ldn.util.OrganizationGlobalizationUtil;


/**
 * Form to edit the basic properties of an Organization. This form can be
 * extended to create forms for Organization subclasses.
 *
 * @version $Id: OrganizationPropertyForm.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class OrganizationPropertyForm
    extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private OrganizationPropertiesStep m_step;

    /** parameter names */
    public static final String LINK = "link";
    public static final String CONTACT = "contact";

    /** Name of this form */
    public static final String ID = "Organization_edit";

    /**
     * Creates a new form to edit the Organization object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Organization to work on
     */
    public OrganizationPropertyForm( ItemSelectionModel itemModel ) {
        this( itemModel, null );
    }

    /**
     * Creates a new form to edit the Organization object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Organization to work on
     * @param step The OrganizationPropertiesStep which controls this form.
     */
    public OrganizationPropertyForm( ItemSelectionModel itemModel, 
                                     OrganizationPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

        TextField link = new TextField(LINK);
        //add(new Label(OrganizationGlobalizationUtil.globalize(
        //             "cms.contenttypes.ui.organization.link")));
        link.setLabel(OrganizationGlobalizationUtil.globalize(
                     "cms.contenttypes.ui.organization.link"));
        add(link);

        TextField contact = new TextField(CONTACT);
        //add(new Label(OrganizationGlobalizationUtil.globalize(
        //              "cms.contenttypes.ui.organization.contact")));
        contact.setLabel(OrganizationGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.organization.contact"));
        add(contact);

    }

    /** 
     * Form initialisation hook. Fills widgets with data. 
     * @param fse
     */
    @Override
    public void init( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        Organization glossary_item
            = (Organization) super.initBasicWidgets( fse );

        data.put( LINK, glossary_item.getLink() );
        data.put( CONTACT, glossary_item.getContact() );
    }

    /** 
     * Cancels streamlined editing. 
     * @param fse
     */
    @Override
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** 
     * Form processing hook. Saves Organization object. 
     * @param fse
     */
    @Override
    public void process( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        
        Organization glossary_item
            = (Organization) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if( glossary_item != null
            && getSaveCancelSection().getSaveButton()
            .isSelected( fse.getPageState() ) ) {
            glossary_item.setLink( (String) data.get( LINK ) );
            glossary_item.setContact( (String) data.get( CONTACT ) );
            glossary_item.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
