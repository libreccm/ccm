/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * A form which will create a new ContentPage or one of its
 * subclasses.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Revision: #21 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class PageCreate extends BasicPageForm
        implements FormSubmissionListener, CreationComponent {
    public static final String versionId =
        "$Id: PageCreate.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger(PageCreate.class);

    private final CreationSelector m_parent;
    private ApplyWorkflowFormSection m_workflowSection;

    /**
     * The state parameter which specifies the content section
     */
    public static final String SECTION_ID = "sid";

    /**
     * Construct a new PageCreationForm
     *
     * @param itemModel The {@link ItemSelectionModel} which will be
     * responsible for loading the current item
     *
     * @param parent The {@link CreationSelector} parent. This class
     * should call either the {@link
     * CreationSelector#redirectBack(PageState)} or {@link
     * CreationSelector#editItem(PageState, ContentItem)} methods on
     * the parent eventually
     */
    public PageCreate(final ItemSelectionModel itemModel,
                      final CreationSelector parent) {
        super("PageCreate", itemModel);
        
        m_parent = parent;
        m_workflowSection.setCreationSelector(m_parent);
        addSubmissionListener(this);

        getSaveCancelSection().getSaveButton().setButtonLabel("Create");
    }

    /**
     * Add various widgets to the form. Child classes should override
     * this method to perform all their widget-adding needs.
     */
    protected void addWidgets() {
        ContentType type = getItemSelectionModel().getContentType();
        m_workflowSection = new ApplyWorkflowFormSection(type);
        add(m_workflowSection, ColumnPanel.INSERT);
        add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.content_type")));
        add(new Label(type.getLabel()));
        add(new Label(GlobalizationUtil.globalize("cms.ui.language.field")));
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

    // Init: create a new item id
    public void init(FormSectionEvent e) throws FormProcessException {
        // this is currently a no-op
    }

    // Submission: If the Cancel button was pressed, hide self and
    // show the display component
    public void submitted(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        if(getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_parent.redirectBack(state);

            throw new FormProcessException
                ((String) GlobalizationUtil.globalize
                     ("cms.ui.authoring.submission_cancelled").localize());
        }
    }

    // Validate: ensure name uniqueness
    public void validate(FormSectionEvent e) throws FormProcessException {
        Folder f = m_parent.getFolder(e.getPageState());
        Assert.exists(f);
        validateNameUniqueness(f, e);
    }

    // Process: save fields to the database
    public void process(final FormSectionEvent e) throws FormProcessException {
        final FormData data = e.getFormData();
        final PageState state = e.getPageState();
        final ContentSection section = m_parent.getContentSection(state);
        Folder folder = m_parent.getFolder(state);

        Assert.exists(section, ContentSection.class);

        final ContentPage item = createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));
        item.setName((String) data.get(NAME));
        item.setTitle((String) data.get(TITLE));
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }

        final ContentBundle bundle = new ContentBundle(item);
        bundle.setParent(folder);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();

        m_workflowSection.applyWorkflow(state, item);

        m_parent.editItem(state, item);
    }

}
