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

public class FormProperties extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public FormProperties(ItemSelectionModel model,
                          AuthoringKitWizard parent) {
        super(model, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editForm = buildEditForm(model);

        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editForm, model),
            editForm.getSaveCancelSection().getCancelButton());
        setDisplayComponent(buildDisplayComponent(model));
    }

    protected BasicPageForm buildEditForm(ItemSelectionModel model) {
        return new FormPropertyEditForm(model, this);
    }

    protected Component buildDisplayComponent(ItemSelectionModel model) {
        return new FormPropertySheet(model);
    }

    protected class FormPropertyEditForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, 
                   FormSubmissionListener {

        private TextArea m_desc;
        private TextField m_css;
        private FormProperties m_step;

        public FormPropertyEditForm(ItemSelectionModel itemModel) {
            this(itemModel, null);
        }

        /**
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

        protected void addWidgets() {
            super.addWidgets();

            m_desc = new TextArea(new StringParameter("description"));
            m_desc.setRows(5);
            m_desc.setCols(50);
            add(new Label(GlobalizationUtil
                          .globalize("cms.ui.formbuilder.description")));
            add(m_desc);
		//Css control hidden            
            /*add(new Label(GlobalizationUtil.globalize("cms.formbuilder.css")));
            m_css = new TextField(new StringParameter("css"));
            add(m_css);*/
        }

        public void init(FormSectionEvent e)
            throws FormProcessException {

            FormItem item = (FormItem)initBasicWidgets(e);
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            FormItem item = (FormItem)processBasicWidgets(e);
            item.save();
            if (m_step != null) {
                m_step.maybeForwardToNextStep(e.getPageState());
            }
        }

        public ContentPage initBasicWidgets(FormSectionEvent e) {
            FormItem item = (FormItem)super.initBasicWidgets(e);

            PersistentForm form = item.getForm();
            m_desc.setValue(e.getPageState(), form.getDescription());
            //Css hidden
            //m_css.setValue(e.getPageState(), item.getCSS());
            
            return item;
        }

        
        public ContentPage processBasicWidgets(FormSectionEvent e) {
            FormItem item = (FormItem)super.processBasicWidgets(e);

            PersistentForm form = item.getForm();
						
            //Css hidden 
            //item.setCSS((String)m_css.getValue(e.getPageState()));
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

    protected class FormPropertySheet extends DomainObjectPropertySheet {
        public FormPropertySheet(ItemSelectionModel model) {
            super(model);

            add(GlobalizationUtil.globalize("cms.ui.formbuilder.name"),  
                ContentPage.NAME);
            add(GlobalizationUtil.globalize("cms.ui.formbuilder.title"),  
                ContentPage.TITLE);
            add(GlobalizationUtil.globalize("cms.ui.formbuilder.description"),
                "form", new FormFormatter());
            //Css hidden temporarily

            //add(GlobalizationUtil.globalize("cms.formbuilder.css"),  
            //    FormItem.CSS);
        }
    }

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
