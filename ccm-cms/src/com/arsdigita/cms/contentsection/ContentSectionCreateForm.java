/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contentsection;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

/**
 * Form for creating a new ContentSection. Used by the {@link ContentSectionAppManager}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ContentSectionCreateForm extends Form {

    public final static String FORM_NAME = "ContentSectionCreateForm";
    private final static String NEW_SECTION_NAME = "newSectionName";
    private final SaveCancelSection saveCancelSection;

    public ContentSectionCreateForm() {
        super(FORM_NAME);

        add(new Label(GlobalizationUtil.globalize("cms.ui.section.new_section_name")));
        final TextField sectionNameField = new TextField(NEW_SECTION_NAME);
        sectionNameField.setMaxLength(256);
        sectionNameField.addValidationListener(new NotNullValidationListener());
        sectionNameField.addValidationListener(new NotEmptyValidationListener());
        add(sectionNameField);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addProcessListener(new ContentSectionCreateProcessListener());
        addSubmissionListener(new ContentSectionSubmissionListener());
    }

    private class ContentSectionCreateProcessListener implements FormProcessListener {

        private final ContentSectionConfig config = ContentSectionConfig.getInstance();

        public ContentSectionCreateProcessListener() {
            //Nothing for now
        }

        public void process(final FormSectionEvent event) throws FormProcessException {
            final FormData data = event.getFormData();

            final String newSectionName = data.getString(NEW_SECTION_NAME);

//            final TransactionContext tctx = SessionManager.getSession().getTransactionContext();
//            tctx.beginTxn();
            ContentSectionSetup.setupContentSectionAppInstance(newSectionName,
                                                               config.getDefaultRoles(),
                                                               config.getDefaultWorkflows(),
                                                               config.isPubliclyViewable(),
                                                               config.getItemResolverClass(),
                                                               config.getTemplateResolverClass(),
                                                               config.getContentSectionsContentTypes(),
                                                               config.getUseSectionCategories(),
                                                               config.getCategoryFileList());
//            tctx.commitTxn();
            
            data.put(NEW_SECTION_NAME, "");
        }

    }

    private class ContentSectionSubmissionListener implements FormSubmissionListener {

        public ContentSectionSubmissionListener() {
            //Nothing for now
        }

        public void submitted(final FormSectionEvent event) throws FormProcessException {
            if (saveCancelSection.getCancelButton().isSelected(event.getPageState())) {
                event.getFormData().put(NEW_SECTION_NAME, "");
                
                throw new FormProcessException("Canceled");
            }
        }

    }
}
