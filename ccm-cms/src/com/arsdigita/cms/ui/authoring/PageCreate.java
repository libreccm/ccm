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
 * A form which will create a new documnent (that is subclasses of class
 * ContentPage).
 * 
 * Used to create a new document / content item. Creates widgets to select the
 * workflow, and language. It displays the type of document as well. Super class
 * adds additional widgets (title and name/url) to complete the form.
 * 
 * It's a pane which is part of a more complex page, additionally containing
 * folder structure, content items in the folder, permissions, etc.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Revision: #21 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: PageCreate.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class PageCreate extends BasicPageForm
                        implements FormSubmissionListener, CreationComponent {
  
    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.cms.ui.authoring.PageCreate=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private final static org.apache.log4j.Logger s_log =
                         org.apache.log4j.Logger.getLogger(PageCreate.class);
    protected final CreationSelector m_parent;
    protected ApplyWorkflowFormSection m_workflowSection;

    /** The state parameter which specifies the content section               */
    public static final String SECTION_ID = "sid";

    /**
     * Construct a new PageCreationForm
     *
     * @param itemModel The {@link ItemSelectionModel} which will be
     *                  responsible for loading the current item
     * @param parent    The {@link CreationSelector} parent. This class
     *                  should call either the {@link
     *                  CreationSelector#redirectBack(PageState)} or {@link
     *                  CreationSelector#editItem(PageState, ContentItem)} 
     *                  methods on the parent eventually
     */
    public PageCreate(final ItemSelectionModel itemModel,
                      final CreationSelector parent) {
        super("PageCreate", itemModel);
        
        m_parent = parent;
        m_workflowSection.setCreationSelector(m_parent);
        addSubmissionListener(this);

        getSaveCancelSection().getSaveButton()
                              .setButtonLabel(GlobalizationUtil.globalize(
                                              "cms.ui.create"));
    }

    /**
     * Add various widgets to the form. Child classes should override
     * this method to perform all their widget-adding needs.
     */
    @Override
    protected void addWidgets() {

        /* Retrieve Content Type  */
        ContentType type = getItemSelectionModel().getContentType();
        /* Add workflow selection based on configured Content Type            */
        m_workflowSection = new ApplyWorkflowFormSection(type);
        add(m_workflowSection, ColumnPanel.INSERT);
        /* content type */
        add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.content_type")));
        add(new Label(type.getName()));
        /* language selection   */
        add(new Label(GlobalizationUtil.globalize("cms.ui.language.field")));
        add(new LanguageWidget(LANGUAGE));

        /* Additional widgets from super type: title and name (url)   */
        super.addWidgets();
    }

    /**
     * Return the ApplyWorkflowFormSection associated with this CreationComponent.
     *
     * @return the ApplyWorkflowFormSection associated with this CreationComponent.
     */
    @Override
    public ApplyWorkflowFormSection getWorkflowSection() {
        return m_workflowSection;
    }

    /** 
     * Create a new item id.
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        // this is currently a no-op
    }

    /**
     * If the Cancel button was pressed, hide self and show the display 
     * component.
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */ 
    @Override
    public void submitted(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        if(getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_parent.redirectBack(state);

            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.authoring.submission_cancelled"));
        }
    }

    /**
     * Validate inputs to ensure name uniqueness. Note: We can't call 
     * {@code super.validate(FormSectionEvent)} here because the  super method
     * {@link BasicPageForm#validate(com.arsdigita.bebop.event.FormSectionEvent)} 
     * tries to access things which on existing yet.
     * 
     * @param event 
     * @throws com.arsdigita.bebop.FormProcessException 
     */
    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {       
        final Folder folder = m_parent.getFolder(event.getPageState());
        Assert.exists(folder);
        validateNameUniqueness(folder, event);
    }

    /**
     * Class specific implementation if FormProcessListener (as inherited from
     * BasicItemForm), saves fields to the database.
     * 
     * @param e
     * @throws FormProcessException 
     */
    @Override
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
