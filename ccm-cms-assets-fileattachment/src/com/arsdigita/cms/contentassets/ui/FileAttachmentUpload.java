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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.contentassets.FileAttachmentGlobalize;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.mimetypes.ImageMimeType;
import java.io.File;
import org.apache.log4j.Logger;


/**
 * A form for uploading file attachments. Displays a mime-type selection
 * box.
 *
 * @author Scott Seago (sseago@redhat.com)
 * @version $Revision: #2 $ $DateTime: 2004/03/30 18:21:14 $
 * @version $Id: FileAttachmentUpload.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FileAttachmentUpload extends Form
    implements FormInitListener, FormProcessListener {

    private static final Logger s_log = Logger.getLogger(FileAttachmentUpload.class);

    private FileUploadSection m_fileUploadSection;
    private TextArea m_description;
    private ItemSelectionModel m_itemModel;
    private SaveCancelSection m_saveCancelSection;

    /**
     * Construct a new FileAttachmentUpload
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     */
    public FileAttachmentUpload(ItemSelectionModel itemModel) {
        super("FileAttachmentUpload", new ColumnPanel(2));
        m_itemModel = itemModel;

        ColumnPanel panel = (ColumnPanel)getPanel();
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
     * @return the currently selected item
     */
    public ContentItem getContentItem(PageState state) {
        return (ContentItem)m_itemModel.getSelectedObject(state);
    }

    /**
     * Set the image asset. This will probably be done in the process
     * listener
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
        m_fileUploadSection =
            new FileUploadSection(FileAttachmentGlobalize.FileTypeLabel(),
                                  "file",
                                  ImageMimeType.MIME_IMAGE_JPEG);
        m_fileUploadSection.getFileUploadWidget()
            .addValidationListener(new NotNullValidationListener());
        add(m_fileUploadSection, ColumnPanel.INSERT);

        m_description = new TextArea("description");
        m_description.setRows(5);
        m_description.setCols(60);
        add(new Label(GlobalizationUtil.globalize("cms.contentassets.ui.description")));
        add(m_description);
    }

    public void init(FormSectionEvent event) throws FormProcessException {
        // Do nothing.
    }

    // process: update the mime type and content
    public void process(FormSectionEvent event) throws FormProcessException {
        s_log.debug("Uploading File");

        FormData data = event.getFormData();
        PageState state = event.getPageState();
        ContentItem item = getContentItem(state);
        FileAttachment a;

        try {
            // Get the text asset or create a new one
            String fileName = m_fileUploadSection.getFileName(event);

            File file = m_fileUploadSection.getFile(event);

            a = new FileAttachment();

            // Load the asset from file
            a.setFileOwner(item);
            a.loadFromFile(fileName, file, "application/octet-stream");
            a.setDescription((String)m_description.getValue(state));
            a.save();

            item.save();

            // Save everything

            this.setFileAttachment(state);
            // Utilities.disableBrowserCache(state.getResponse());
            s_log.debug("File Uploaded");

        } catch (Exception e) {
            e.printStackTrace();
            throw new FormProcessException(e);
        }
    }

    /**
     * @return the save/cancel section for this form
     */
    public FileUploadSection getFileUploadSection() {
        return m_fileUploadSection;
    }

}
