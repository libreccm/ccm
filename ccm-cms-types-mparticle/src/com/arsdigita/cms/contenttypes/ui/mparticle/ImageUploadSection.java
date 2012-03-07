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
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.ui.ImageDisplay;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import org.apache.log4j.Logger;
import java.io.File;

/**
 * A form section with one widget to upload an image.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 */
public class ImageUploadSection extends FormSection
{
    private static final Logger log = Logger.getLogger(ImageUploadSection.class);

    public static final String IMAGE    = "Image";
    public static final String CAPTION  = "Caption";

    private String m_name;
    private ItemSelectionModel m_selImage;
    private ImageDisplay m_imageDisplay;
    private Label m_currentImage;
    private Label m_spacer;
    private Submit m_deleteImage;

    /**
     * Constructor.
     *
     * @param panel the panel used to lay out the components
     */
    public ImageUploadSection(String name,
                              ItemSelectionModel selImage,
                              Container panel) {
        super(panel);
        m_selImage = selImage;
        m_name = name;

        m_imageDisplay = new ImageDisplay(m_selImage);
        m_imageDisplay.setClassAttr("MPAImageDisplay");
        m_currentImage = new Label
            (MPArticleGlobalizationUtil
             .globalize("cms.contenttypes.ui.mparticle.current_image"));
        add(m_currentImage);
        add(m_imageDisplay);
        m_spacer = new Label("");
	add(m_spacer);
        m_deleteImage = new Submit("Delete Image");
        add(m_deleteImage);
        add(new Label(
                MPArticleGlobalizationUtil
                .globalize("cms.contenttypes.ui.mparticle.select_image")));
        add(new FileUpload(m_name + IMAGE));
        add(new Label(
                MPArticleGlobalizationUtil
                .globalize("cms.contenttypes.ui.mparticle.caption")));
        TextField caption = new TextField(m_name + CAPTION);
        caption.setSize(40);
        add(caption);
    }

    public ImageUploadSection(String name,
                              ItemSelectionModel selImage) {
        this(name, selImage, new ColumnPanel(2, true));
        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
    }

    public Submit getDeleteImageButton() {
    	return m_deleteImage;
    }

    /**
     * Initialise the caption text field.  Should be called from the
     * form init listener.
     */
    public void initImageUpload(FormSectionEvent event) {
        FormData data = event.getFormData();
        PageState state = event.getPageState();

        ReusableImageAsset image = (ReusableImageAsset)m_selImage.getSelectedObject(state);

        m_currentImage.setVisible(state, false);
        m_imageDisplay.setVisible(state, false);
	m_spacer.setVisible(state, false);
	m_deleteImage.setVisible(state, false);
        if ( image != null ) {
            data.put(m_name + CAPTION, image.getDescription());
            m_currentImage.setVisible(state, true);
            m_imageDisplay.setVisible(state, true);
	    m_spacer.setVisible(state, true);
	    m_deleteImage.setVisible(state, true);

        }
    }


    /**
     * Return the filname from the upload widget.  Should be called
     * from the form process listener.
     */
    public String getImageFilename(FormSectionEvent event) {
        FormData data = event.getFormData();
        return (String)data.get(m_name + IMAGE);
    }

    /**
     * Return a File object from the file upload widget.  Should be
     * called from the form process listener.
     */
    public File getImage(FormSectionEvent event) {
        FormData data = event.getFormData();
        String filename = getImageFilename(event);
        File image = null;


        if ( filename != null && filename.length() > 0) {
            image = ((MultipartHttpServletRequest)event.getPageState().getRequest())
                .getFile(m_name + IMAGE);
        }

        return image;
    }


    /**
     * Process the image upload.  Should be called form the form
     * process listener.
     */
    public ReusableImageAsset processImageUpload(FormSectionEvent event) {
        ReusableImageAsset a = null;
        FormData data = event.getFormData();

        File image = getImage(event);

        if ( image != null ) {
            try {
                a = new ReusableImageAsset();
                a.loadFromFile(getImageFilename(event), image, ReusableImageAsset.MIME_JPEG);
                a.setDescription((String)data.get(m_name + CAPTION));
            } catch ( Exception ex ) {
                log.error("Could not load " + getImageFilename(event));
                return null;
            }
        }

        return a;
    }
}
