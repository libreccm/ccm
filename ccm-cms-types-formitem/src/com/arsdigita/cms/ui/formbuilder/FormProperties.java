/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.formbuilder;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.formbuilder.FormItem;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring step to edit the simple attributes of the FormItem content type
 * (and its subclasses). The attributes edited are 'title', 'name', optionally
 * 'launchDate' (if configured as active), and 'desciption' / summary.
 * 
 */
public class FormProperties extends SimpleEditStep {

    /** The name of the editing sheet for this step */
    public static String EDIT_SHEET_NAME = "edit";

    /**
     * FormProperties Constructor, creates an empty property step sheet.
     * 
     * @param model
     * @param parent 
     */
    public FormProperties(ItemSelectionModel itemModel,
                          AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editForm = buildEditForm(itemModel);

        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(editForm, itemModel),
            editForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(buildDisplayComponent(itemModel));
    }

    /**
     * 
     * @param model
     * @return 
     */
    protected BasicPageForm buildEditForm(ItemSelectionModel model) {
        return new FormPropertyEditForm(model, this);
    }

    /**
     * 
     * @param model
     * @return 
     */
    protected Component buildDisplayComponent(ItemSelectionModel model) {
        return new FormPropertySheet(model);
    }

    /**
     * Internal class to implement an edit form for properties. For most other
     * content types this is implemented as a separate (external) public class.
     * FormItem just uses the standard properties and relays complete on
     * classes of the AP (i.e. specifically CMS and CORE).
     */
    protected class FormPropertyEditForm extends BasicPageForm
                                         implements FormProcessListener, 
                                                    FormInitListener, 
                                                    FormSubmissionListener {

        private TextArea m_desc;
        private TextField m_css;
        private FormProperties m_step;

        /**
         * Internal class constructor, just creates an empty form.
         * @param itemModel 
         */
        public FormPropertyEditForm(ItemSelectionModel itemModel) {
            this(itemModel, null);
        }

        /**
         * Internal class constructor, just creates a form using passed in
         * form properties.
         * 
         *  @param itemModel the ItemSelectionModel that controls which form
         *                   to work on
         *  @param formProperties The properties step that controls this form.
         */
        public FormPropertyEditForm(ItemSelectionModel itemModel,
                                    FormProperties formProperties) {
            super("edit_properties", itemModel);
            m_step = formProperties;
            addSubmissionListener(this);
        }

        /**
         * Fills the (empty) form as created by the constructor with widgets.
         */
        @Override
        protected void addWidgets() {
            super.addWidgets();  // adds standard properties title,name,launchdate

            // add editing the description property
            m_desc = new TextArea(new StringParameter("description"));
            m_desc.setRows(5);
            m_desc.setCols(50);
            add(new Label(GlobalizationUtil
                          .globalize("cms.contenttypes.ui.description")));
            add(m_desc);
        }

        /**
         * 
         * @param e
         * @throws FormProcessException 
         */
        public void init(FormSectionEvent e)
            throws FormProcessException {

            FormItem item = (FormItem)initBasicWidgets(e);
        }

        /**
         * Process the FORM after submit. 
         * 
         * @param e
         * @throws FormProcessException 
         */
        public void process(FormSectionEvent e)
                    throws FormProcessException {

            FormItem item = (FormItem)processBasicWidgets(e);
            item.save();
            if (m_step != null) {
                m_step.maybeForwardToNextStep(e.getPageState());
            }
        }

        /**
         * 
         * @param e
         * @return 
         */
        @Override
        public ContentPage initBasicWidgets(FormSectionEvent e) {
            FormItem item = (FormItem)super.initBasicWidgets(e);

            PersistentForm form = item.getForm();
            m_desc.setValue(e.getPageState(), form.getDescription());
            
            return item;
        }

        
        /**
         * 
         * @param e
         * @return 
         */
        @Override
        public ContentPage processBasicWidgets(FormSectionEvent e) {

            FormItem item = (FormItem)super.processBasicWidgets(e);

            PersistentForm form = item.getForm();
						
            item.save();
            form.setAdminName(item.getName());
            form.setHTMLName(item.getName());
            form.setDescription((String)m_desc.getValue(e.getPageState()));
            form.save();

            return item;
        }

        /** Cancels streamlined editing. */
        public void submitted( FormSectionEvent fse ) {
            if (m_step != null &&
                getSaveCancelSection().getCancelButton()
                .isSelected( fse.getPageState())) {
                m_step.cancelStreamlinedCreation(fse.getPageState());
            }
        }
    }

    /**
     * Internal class implents a DomainObjectProertySheet containent the
     * required widgets for the editin FORM (i.e. title, name, description)
     */
    protected class FormPropertySheet extends DomainObjectPropertySheet {

        public FormPropertySheet(ItemSelectionModel model) {
            super(model);

            add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"),  
                ContentPage.TITLE);
            add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"),  
                ContentPage.NAME);
            add(GlobalizationUtil.globalize("cms.contenttypes.ui.description"),
                "form", 
                new FormFormatter());
        }
    }

    /**
     * Provides an AttributeFormatter for the 'description' property to be
     * displayed in the property step.
     */
    private class FormFormatter implements DomainObjectPropertySheet.AttributeFormatter {

        ItemSelectionModel m_item;

        public String format(DomainObject item, String attribute, PageState state) {
            if (attribute.equals("form")) {
                FormItem formitem = (FormItem)item;

                PersistentForm f = formitem.getForm();
                return f.getDescription();
            }
            return null;
        }

    }
}
