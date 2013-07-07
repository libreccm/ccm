/*
 * Copyright (C) 2009-2013 Sören Bernstein, University of Bremen. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.cms.contenttypes.GenericContact;

import org.apache.log4j.Logger;

/**
 * The editing step for contact's basic properties. 
 * It is part of the central entry point of contact's authoring step
 * {@link GenericContactPropertiesStep}
 * 
 * The Form enables editing the basic properties of a basic contact as 
 * required by parent class ContentPage (i.e. title / name).
 */
public class GenericContactPropertyForm extends BasicPageForm 
                                        implements FormProcessListener, 
                                                   FormInitListener, 
                                                   FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(
                                         GenericContactPropertyForm.class);

    private GenericContactPropertiesStep m_step;

    public static final String PERSON = GenericContact.PERSON;
    public static final String ADRESS = GenericContact.ADDRESS;
    public static final String CONTACT_ENTRIES= GenericContact.CONTACT_ENTRIES;

    /** ID of the form     */
    public static final String ID = "Contact_edit";

    /**
     * Constructor taking an ItemSelectionModel.
     *
     * @param itemModel
     */
    public GenericContactPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constructor taking an ItemSelectionModel and an instance of 
     * BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public GenericContactPropertyForm(ItemSelectionModel itemModel, 
                                      GenericContactPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * 
     */
    @Override
    public void addWidgets() {
        super.addWidgets();

/*
        add(new Label(BaseContactGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.contact.basic_properties.description")
                      )));
        TextArea description = new TextArea(DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);
*/
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        GenericContact contact = (GenericContact)super.initBasicWidgets(e);

//        data.put(DESCRIPTION, contact.getDescription());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        GenericContact contact = (GenericContact)super.processBasicWidgets(e);

        if((contact != null) 
           && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
//            contact.setDescription((String)data.get(DESCRIPTION));
            contact.save();
        }

        if(m_step != null) {
            m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if((m_step != null) 
           && (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}