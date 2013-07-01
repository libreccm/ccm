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
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.formbuilder.FormSectionItem;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;


public class FormSectionProperties extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public FormSectionProperties(ItemSelectionModel model,
                                 AuthoringKitWizard parent) {
        super(model, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm edit = new FormSectionPropertyEditForm(model, this);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(edit, model),
            edit.getSaveCancelSection().getCancelButton());
        setDisplayComponent(buildDisplayComponent(model));
    }


    protected class FormSectionPropertyEditForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

        private FormSectionProperties m_step;
        private TextArea m_desc;

        public FormSectionPropertyEditForm(ItemSelectionModel itemModel) {
            this(itemModel, null);
        }

        /**
         *  @param itemModel the ItemSelectionModel that controls which form
         *                   to work on
         *  @param sectionProperties The properties step that controls this form.
         */
        public FormSectionPropertyEditForm(ItemSelectionModel itemModel,
                                           FormSectionProperties sectionProperties) {
            super("edit_properties", itemModel);
            m_step = sectionProperties;
            addSubmissionListener(this);
        }

        protected void addWidgets() {
            super.addWidgets();

            m_desc = new TextArea(new StringParameter("description"));
            m_desc.setRows(5);
            m_desc.setCols(50);
            add(new Label(GlobalizationUtil
                          .globalize("cms.contenttypes.ui.description")));
            add(m_desc);
        }

        public void init(FormSectionEvent e)
            throws FormProcessException {

            FormSectionItem item = (FormSectionItem)initBasicWidgets(e);

            PersistentFormSection form = item.getFormSection();
            m_desc.setValue(e.getPageState(), form.getDescription());
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            FormData data = e.getFormData();
            FormSectionItem item = (FormSectionItem)processBasicWidgets(e);

            PersistentFormSection form = item.getFormSection();

            item.save();
            form.setAdminName(item.getName());
            form.setDescription((String)data.get("description"));
            form.save();

            if (m_step != null) {
                m_step.maybeForwardToNextStep(e.getPageState());
            }
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

    private Component buildDisplayComponent(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"),  
                  ContentPage.TITLE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"),  
                  ContentPage.NAME);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.description"),
                  "form", new FormSectionFormatter());

        return sheet;
    }

    private class FormSectionFormatter 
                  implements DomainObjectPropertySheet.AttributeFormatter {

        ItemSelectionModel m_item;

        public String format(DomainObject item, String attribute, PageState state) {
            if (attribute.equals("form")) {
                FormSectionItem section = (FormSectionItem)item;

                PersistentFormSection s = section.getFormSection();
                return s.getDescription();
            }
            return null;
        }

    }
}
