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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Image;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Form to edit the basic properties of an article. This form can be
 * extended to create forms for Image subclasses.
 */
public class ImagePropertyForm
        extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(ImagePropertyForm.class);
    private ImagePropertiesStep m_step;
//    private final StringParameter m_imageComponentKey;
//    private final MapComponentSelectionModel m_imageComponent;
    private final String UPLOAD = "upload";

    /**
     * Creates a new form to edit the Image object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Image to work on
     */
    public ImagePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Creates a new form to edit the Image object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Image to work on
     * @param step The ImagePropertiesStep which controls this form.
     */
    public ImagePropertyForm(ItemSelectionModel itemModel, ImagePropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
//        m_imageComponentKey = new StringParameter("imageComponent");
//        ParameterSingleSelectionModel componentModel = new ParameterSingleSelectionModel(m_imageComponentKey);
//        m_imageComponent = new MapComponentSelectionModel(componentModel, new HashMap());
        addSubmissionListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

//        Map selectors = m_imageComponent.getComponentsMap();
//        ImageUploadComponent upload = new ImageUploadComponent();
//        upload.getForm().addInitListener(this);
//        upload.getForm().addProcessListener(this);
//        selectors.put(UPLOAD, upload);
//        add(upload);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.caption")));
        ParameterModel captionParam = new StringParameter(Image.CAPTION);
        captionParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField caption = new TextField(captionParam);
        add(caption);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.description")));
        ParameterModel descriptionParam = new StringParameter(Image.DESCRIPTION);
        descriptionParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField description = new TextField(descriptionParam);
        add(description);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.artist")));
        ParameterModel artistParam = new StringParameter(Image.ARTIST);
        artistParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField artist = new TextField(artistParam);
        add(artist);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.publish_date")));
        ParameterModel publishDateParam = new DateParameter(Image.PUBLISHDATE);
        Date publishDate = new Date(publishDateParam);
        add(publishDate);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.source")));
        ParameterModel sourceParam = new StringParameter(Image.SOURCE);
        sourceParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField source = new TextField(sourceParam);
        add(source);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.media")));
        ParameterModel mediaParam = new StringParameter(Image.MEDIA);
        mediaParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField media = new TextField(mediaParam);
        add(media);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.copyright")));
        ParameterModel copyrightParam = new StringParameter(Image.COPYRIGHT);
        copyrightParam.addParameterListener(new StringInRangeValidationListener(0, 200));
        TextField copyright = new TextField(copyrightParam);
        add(copyright);

    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        FormData d = e.getFormData();
    }

//    @Override
//    public void register(Page p) {
//        super.register(p);
//
//        Map componentsMap = m_imageComponent.getComponentsMap();
//        Iterator i = componentsMap.keySet().iterator();
//        while (i.hasNext()) {
//            Object key = i.next();
//            Component component = (Component) componentsMap.get(key);
//
//            p.setVisibleDefault(component, UPLOAD.equals(key));
//        }
//
//        p.addComponentStateParam(this, m_imageComponentKey);
//    }
//
//    Iterator getImageComponents() {
//        return m_imageComponent.getComponentsMap().values().iterator();
//    }
//
//    private ImageComponent getImageComponent(PageState ps) {
//        if (!m_imageComponent.isSelected(ps)) {
//            if (s_log.isDebugEnabled()) {
//                s_log.debug("No component selected");
//                s_log.debug("Selected: " + m_imageComponent.getComponent(ps));
//            }
//
//            m_imageComponent.setSelectedKey(ps, UPLOAD);
//        }
//
//        return (ImageComponent) m_imageComponent.getComponent(ps);
//
//    }
//
//    private void setImageComponent(PageState ps, final String activeKey) {
//        m_imageComponent.setSelectedKey(ps, activeKey);
//
//        if (s_log.isDebugEnabled()) {
//            s_log.debug("Selected component: " + activeKey);
//        }
//
//        Map componentsMap = m_imageComponent.getComponentsMap();
//        Iterator i = componentsMap.keySet().iterator();
//        while (i.hasNext()) {
//            Object key = i.next();
//            Component component = (Component) componentsMap.get(key);
//
//            boolean isVisible = activeKey.equals(key);
//
//            if (s_log.isDebugEnabled()) {
//                s_log.debug("Key: " + key + "; Visibility: " + isVisible);
//            }
//
//            ps.setVisible(component, isVisible);
//        }
//    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        PageState ps = fse.getPageState();
        Image image = (Image) super.initBasicWidgets(fse);

//        ItemImageAttachment attachment = m_imageStep.getAttachment(ps);

        data.put(Image.CAPTION, image.getCaption());
        data.put(Image.DESCRIPTION, image.getDescription());
        data.put(Image.ARTIST, image.getArtist());
        data.put(Image.PUBLISHDATE, image.getPublishDate());
        data.put(Image.SOURCE, image.getSource());
        data.put(Image.MEDIA, image.getMedia());
        data.put(Image.COPYRIGHT, image.getCopyright());
//        data.put( LEAD, image.getLead() );
    }

    /** Cancels streamlined editing. */
    public void submitted(FormSectionEvent fse) {
        if (m_step != null
                && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves Event object. */
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState ps = fse.getPageState();
        Image image = (Image) super.processBasicWidgets(fse);

//        ImageComponent component = getImageComponent(ps);

//        try {
//            ImageAsset imageAsset = component.getImage(fse);

//            ItemImageAttachment attachment = m_imageStep.getAttachment(ps);
//            if (null == attachment) {
//                attachment = new ItemImageAttachment(item, imageAsset);
//            }
//        attachment.setCaption( component.getCaption( event ) );
//
//        // We only set the description and title based on the UI in
//        // the case where getIsImageStepDescriptionAndTitleShown is true.
//        // Otherwise, we leave this as the default value.  This means
//        // existing values are not overwritten if the image is edited when
//        // isImageStepDescriptionAndTitleShown is false.
//		if(ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown()) {
//	        attachment.setDescription(component.getDescription( event ));
//			attachment.setTitle(component.getTitle( event ));
//		}
//        attachment.setUseContext( component.getUseContext( event ) );

//        } catch (FormProcessException ex) {
//        }

        // save only if save button was pressed
        if (image != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            image.setCaption((String) data.get(Image.CAPTION));
            image.setDescription((String) data.get(Image.DESCRIPTION));
            image.setArtist((String) data.get(Image.ARTIST));
            image.setPublishDate((java.util.Date) data.get(Image.PUBLISHDATE));
            image.setSource((String) data.get(Image.SOURCE));
            image.setMedia((String) data.get(Image.MEDIA));
            image.setCopyright((String) data.get(Image.COPYRIGHT));
//            image.setLead( (String) data.get( LEAD ) );

            image.save();
        }
//        if (m_step != null) {
//            m_step.maybeForwardToNextStep(fse.getPageState());
//        }
    }

    interface ImageComponent {

        ImageAsset getImage(FormSectionEvent event)
                throws FormProcessException;

        SaveCancelSection getSaveCancelSection();

        Form getForm();
    }

    private class ImageUploadComponent extends Form
            implements ImageComponent {

        private final FileUploadSection m_imageFile;
        private final SaveCancelSection m_saveCancel;

        public ImageUploadComponent() {
            super("imageStepEditUpload", new ColumnPanel(2));

            setEncType("multipart/form-data");

            // Ignoring deprecated constructor.
            m_imageFile = new FileUploadSection("Image Type", "image", ImageAsset.MIME_JPEG);
            m_imageFile.getFileUploadWidget().addValidationListener(new NotNullValidationListener());

            add(m_imageFile, ColumnPanel.FULL_WIDTH);

            m_saveCancel = new SaveCancelSection();
            add(m_saveCancel);

        }

        public SaveCancelSection getSaveCancelSection() {
            return m_saveCancel;
        }

        public ImageAsset getImage(FormSectionEvent event)
                throws FormProcessException {
            PageState ps = event.getPageState();

            String filename = (String) m_imageFile.getFileName(event);
            File imageFile = m_imageFile.getFile(event);

            try {
                ImageAsset image = new ImageAsset();
                image.loadFromFile(filename, imageFile, ImageAsset.MIME_JPEG);
//                image.setDescription((String) m_caption.getValue(ps));

                return image;
            } catch (IOException ex) {
                s_log.error("Error loading image from file", ex);
                throw new FormProcessException(ex.getMessage());
            }
        }

        public Form getForm() {
            return this;
        }
    }
}
