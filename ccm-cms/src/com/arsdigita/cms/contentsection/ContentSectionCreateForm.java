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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;

/**
 * Form for creating a new ContentSection. Used by the {@link ContentSectionAppManager}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ContentSectionCreateForm extends Form {

    public final static String FORM_NAME = "ContentSectionCreateForm";
    private final static String NEW_SECTION_NAME = "newSectionName";
    private final static String NEW_SECTION_ROOT_CAT = "newSectionRootCategory";
    private final SaveCancelSection saveCancelSection;

    public ContentSectionCreateForm() {
        super(FORM_NAME);

        add(new Label(GlobalizationUtil.globalize("cms.ui.section.new_section_name")));
        final TextField sectionNameField = new TextField(NEW_SECTION_NAME);
        sectionNameField.setMaxLength(256);
        sectionNameField.addValidationListener(new NotNullValidationListener());
        sectionNameField.addValidationListener(new NotEmptyValidationListener());
        add(sectionNameField);

        add(new Label(GlobalizationUtil.globalize("cms.ui.section.new_section_root_category")));
        final SingleSelect rootCategorySelect = new SingleSelect(NEW_SECTION_ROOT_CAT);
        final DataCollection categories = SessionManager.getSession().retrieve(
                Category.BASE_DATA_OBJECT_TYPE);
        rootCategorySelect.addOption(new Option(""));
        Category current;
        while (categories.next()) {
            current = (Category) DomainObjectFactory.newInstance(categories.getDataObject());
            if (current.isRoot()) {
                rootCategorySelect.addOption(new Option(current.getID().toString(),
                                                        current.getDisplayName()));
            }
        }
        rootCategorySelect.addValidationListener(new NotNullValidationListener());
        rootCategorySelect.addValidationListener(new NotEmptyValidationListener());
        add(rootCategorySelect);

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

            final Category rootCategory = new Category(new BigDecimal(data.getString(
                    NEW_SECTION_ROOT_CAT)));
            if (!rootCategory.isRoot()) {
                throw new IllegalArgumentException("The category given is not a root category.");
            }

            ContentSectionSetup.setupContentSectionAppInstance(
                    newSectionName,
                    rootCategory,
                    config.getDefaultRoles(),
                    config.getDefaultWorkflows(),
                    config.isPubliclyViewable(),
                    config.getItemResolverClass(),
                    config.getTemplateResolverClass(),
                    config.getContentSectionsContentTypes());

            data.put(NEW_SECTION_NAME, "");
            data.put(NEW_SECTION_ROOT_CAT, "");
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
