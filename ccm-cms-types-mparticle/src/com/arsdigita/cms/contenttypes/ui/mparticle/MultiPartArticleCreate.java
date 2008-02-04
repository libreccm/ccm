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
package com.arsdigita.cms.contenttypes.ui.mparticle;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.ui.authoring.CreationComponent;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.util.Assert;

import java.util.Date;

/**
 * A form which will create a MultiPartArticle or one of its
 * subclasses.
 *
 * @author <a href="mailto:dturner@arsidigita.com">Dave Turner</a>
 * @version $Id: MultiPartArticleCreate.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class MultiPartArticleCreate extends MultiPartArticleForm
        implements FormInitListener, FormProcessListener,
                   FormSubmissionListener, 
                   FormValidationListener, 
                   CreationComponent {
    private CreationSelector m_parent;
    private ApplyWorkflowFormSection m_workflowSection;

    public MultiPartArticleCreate(ItemSelectionModel itemModel,
                                  CreationSelector parent) {
        super("MultiPartArticleCreate", itemModel);
        m_parent = parent;
        m_workflowSection.setCreationSelector(m_parent);
        m_workflowSection.setContentType(m_itemModel.getContentType());
        addSubmissionListener(this);
        getSaveCancelSection().getSaveButton().setButtonLabel("Create");
    }

    protected void addWidgets() {
        m_workflowSection = new ApplyWorkflowFormSection();
        add(m_workflowSection, ColumnPanel.INSERT);
        add(new Label(
                MPArticleGlobalizationUtil
                .globalize("cms.ui.language.field")));
        add(new LanguageWidget(LANGUAGE));
        super.addWidgets();
    }

    /**
     * Return the ApplyWorkflowFormSection associated with this CreationComponent.
     *
     * @return the ApplyWorkflowFormSection associated with this CreationComponent.
     */
    public ApplyWorkflowFormSection getWorkflowSection() {
        return m_workflowSection;
    }

    public void init(FormSectionEvent event) throws FormProcessException {
        // this is currently a no-op
    }

    public void submitted(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();

        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_parent.redirectBack(state);
            throw new FormProcessException(
                (String)MPArticleGlobalizationUtil
                .globalize("cms.contenttypes.ui.mparticle.submission_cancelled")
                .localize());
        }
    }

    public void validate(FormSectionEvent event) throws FormProcessException {
        Folder f = m_parent.getFolder(event.getPageState());
        Assert.exists(f, Folder.class);
        if (!validateNameUniqueness(f, event)) {
            throw new FormProcessException(
                (String)MPArticleGlobalizationUtil
                .globalize("cms.contenttypes.ui.mparticle." + 
                           "an_item_with_this_name_already_exists")
                .localize());
        }
    }

    public void process(final FormSectionEvent e) throws FormProcessException {
        final FormData data = e.getFormData();
        final PageState state = e.getPageState();
        final ContentSection section = m_parent.getContentSection(state);

        final MultiPartArticle article = createArticle(state);
        article.setLanguage((String) data.get(LANGUAGE));
        article.setName((String) data.get(NAME));
        article.setTitle((String) data.get(TITLE));
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            article.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }
        article.setSummary((String) data.get(SUMMARY));

        final ContentBundle bundle = new ContentBundle(article);
        bundle.setParent(m_parent.getFolder(state));
        bundle.setContentSection(section);

        m_workflowSection.applyWorkflow(state, article);

        m_parent.editItem(state, article);
    }

}
