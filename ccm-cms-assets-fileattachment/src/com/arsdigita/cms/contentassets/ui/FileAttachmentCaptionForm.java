/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.contentassets.FileAttachmentConfig;
import com.arsdigita.cms.contentassets.FileAttachmentGlobalize;
import com.arsdigita.cms.contentassets.util.FileAttachmentGlobalizationUtil;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.dispatcher.DispatcherHelper;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * A form for uploading captions. Based on FileAttachmentUpload
 *
 * @author konerman
 */
public class FileAttachmentCaptionForm extends Form
        implements FormInitListener, FormProcessListener, FormValidationListener {

    private static final Logger s_log = Logger.getLogger(FileAttachmentCaptionForm.class);

    private FileUploadSection m_fileUploadSection;
    private ItemSelectionModel m_itemModel;
    private SaveCancelSection m_saveCancelSection;
    //private DHTMLEditor m_title;
    private TextField m_title;
    private DHTMLEditor m_captionText;

    private static final FileAttachmentConfig s_config = FileAttachmentConfig
            .instanceOf();

    /**
     * Construct a new FileCaptionForm
     *
     * @param itemModel The {@link ItemSelectionModel} which will be responsible
     * for loading the current item
     *
     */
    public FileAttachmentCaptionForm(ItemSelectionModel itemModel) {
        super("CaptionAttachmentUpload", new ColumnPanel(2));
        m_itemModel = itemModel;

        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("2%");

        addWidgets();

        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
        setMethod(Form.POST);
        setEncType("multipart/form-data");
    }

    /**
     * @return the item selection model used in this form
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * @param state The page state
     *
     * @return the currently selected item
     */
    public ContentItem getContentItem(PageState state) {
        return (ContentItem) m_itemModel.getSelectedObject(state);
    }

    /**
     * Set the image asset. This will probably be done in the process listener
     *
     * @param state The page state
     * @param asset The image asset
     */
    // this may not be needed at all -- it was in the medqic code for some reason.
    // we should remove if possible
    public void setFileAttachment(PageState state) {
        ContentItem item = getContentItem(state);
        // Force the item to reload, since the assets query is cached ?
        // ?
    }

    // Add the widgets
    public void addWidgets() {
        //m_title = new DHTMLEditor("captiontitle");
        m_title = new TextField("captiontitle");
        m_title.setSize(32);
        m_title.lock();

        add(new Label(FileAttachmentGlobalizationUtil.globalize(
                "cms.contentassets.file_attachment.title")));
        add(m_title);

        m_captionText = new DHTMLEditor("caption");
        m_captionText.addValidationListener(new StringInRangeValidationListener(
                0,
                s_config.getFileAttachmentDescriptionMaxLength(),
                FileAttachmentGlobalize.DescriptionTooLong(s_config.getFileAttachmentDescriptionMaxLength())));
        m_captionText.lock();
        add(new Label(FileAttachmentGlobalizationUtil.globalize(
                "cms.contentassets.file_attachment.caption_or_description")));
        add(m_captionText);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {

        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        s_log.debug("Init");
        m_captionText.setValue(state, null);
        m_title.setValue(state, null);
    }

    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {
        // test if the user made an input
        PageState state = event.getPageState();
        String title = (String) m_title.getValue(state);
        String desc = (String) m_captionText.getValue(state);
        if ((title.length() + desc.length()) <= 0) {
            throw new FormProcessException(FileAttachmentGlobalizationUtil
                    .globalize(
                            "cms.contentassets.file_attachment.input_mandatory"));
        }
    }

    // process: update the mime type and content
    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        s_log.debug("Uploading File");

        final PageState state = event.getPageState();
        final ContentItem item = getContentItem(state);
        final FileAttachment attachment = new FileAttachment();

        try {
            attachment.setCaption();
        } catch (IOException ex) {
            throw new FormProcessException(ex);
        }
        attachment.setDescription((String) m_captionText.getValue(state));
        String title = (String) m_title.getValue(state);
        if (title.isEmpty()) {
            attachment.setName("iscaption");
        } else {
            attachment.setName((String) m_title.getValue(state));
        }
        attachment.setFileOwner(item);
        attachment.save();
        item.save();

        // Save everything
        this.setFileAttachment(state);
        DispatcherHelper.cacheDisable(state.getResponse());
        s_log.debug("File Uploaded");
    }

    /**
     * @return the save/cancel section for this form
     */
    public FileUploadSection getFileUploadSection() {
        return m_fileUploadSection;
    }

}
