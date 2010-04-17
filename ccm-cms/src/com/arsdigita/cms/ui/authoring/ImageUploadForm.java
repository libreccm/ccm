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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.Article;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.mimetypes.ImageMimeType;
import java.io.File;
import org.apache.log4j.Logger;


/**
 * A form for uploading images. Displays a mime-type selection
 * box and a Caption field.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #16 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: ImageUploadForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ImageUploadForm extends BasicImageForm implements FormSubmissionListener {

    private static Logger s_log = Logger.getLogger(ImageUploadForm.class);

    private FileUploadSection m_fileUploadSection;
    private ArticleImage m_step;

    /**
     * Construct a new ImageUploadForm
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param assetModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current image asset
     */
    public ImageUploadForm(ItemSelectionModel itemModel,
                           ItemSelectionModel assetModel) {
        this(itemModel, assetModel, null);
    }

    /**
     * Construct a new ImageUploadForm
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param assetModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current image asset
     *
     * @param articleImage The {@link ArticleImage} step which this form belongs to
     */
    public ImageUploadForm(ItemSelectionModel itemModel,
                           ItemSelectionModel assetModel,
                           ArticleImage articleImage) {
        super("ImageUploadForm", itemModel, assetModel);

        setMethod(Form.POST);
        setEncType("multipart/form-data");
        m_step = articleImage;
    }

    // Add the widgets
    public void addWidgets() {
        m_fileUploadSection =
            new FileUploadSection("Image&nbsp;Type:", "image",
                                  ImageMimeType.MIME_IMAGE_JPEG);
        m_fileUploadSection.getFileUploadWidget()
            .addValidationListener(new NotNullValidationListener());
        add(m_fileUploadSection, ColumnPanel.INSERT);

        super.addWidgets();
    }

    public void init(FormSectionEvent event) throws FormProcessException {
        // Do nothing.
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
    // process: update the mime type and content
    public void process(FormSectionEvent event) throws FormProcessException {
        s_log.debug("Uploading Image");

        FormData data = event.getFormData();
        PageState state = event.getPageState();
        Article item = this.getArticle(state);
        ImageAsset a = null;

        try {
            // Upload a new image asset
            String fileName = m_fileUploadSection.getFileName(event);
            String caption = (String)data.get(CAPTION);
            File file = m_fileUploadSection.getFile(event);
            
            // Even though Articles are allowed to have more than one Image in the
            // object model, this UI only allows them to have one at a time...
            // so first off we get rid of all the existing images.
            item.clearImages();
            item.save();
            
            a = new ReusableImageAsset();
            // Load the asset from file
            a.loadFromFile(fileName, file, ImageAsset.MIME_JPEG);
            
            // Save everything
            a.save();
            
            // Update caption
            item.addImage(a, caption);
            item.save();
            this.setImageAsset(state, a);
            s_log.debug("Image Uploaded");
        } catch (Exception e) {
            s_log.error("Uploading error", e);
            throw new FormProcessException(e);
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(event.getPageState());
        }

    }

    /**
     * @return the save/cancel section for this form
     */

    public FileUploadSection getFileUploadSection() {
        return m_fileUploadSection;
    }
}
