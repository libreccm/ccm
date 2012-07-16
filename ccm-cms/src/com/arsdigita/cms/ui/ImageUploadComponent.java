/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ReusableImageAsset;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageUploadComponent extends Form implements ImageComponent {

    private final FileUploadSection m_imageFile;
    private final TextField m_caption;
    private final TextField m_title;
    private final TextArea m_description;
    private final TextField m_useContext;
    private final SaveCancelSection m_saveCancel;
    private int m_mode;

    public ImageUploadComponent() {
        this(ImageComponent.ATTACH_IMAGE);
    }

    public ImageUploadComponent(int mode) {
        super("imageUploadComponent", new ColumnPanel(2));
        m_mode = mode;
        setEncType("multipart/form-data");
        // Ignoring deprecated constructor.
        m_imageFile = new FileUploadSection("Image Type", "image", ImageAsset.MIME_JPEG);
        m_imageFile.getFileUploadWidget().addValidationListener(new NotNullValidationListener());
        add(m_imageFile, ColumnPanel.FULL_WIDTH);

        // Initialize all widgets
        m_caption = new TextField("caption");
        m_title = new TextField("title");
        m_description = new TextArea("description");
        m_useContext = new TextField("useContext");

        // add widget only if we are in attach mode
        if (m_mode == ImageComponent.ATTACH_IMAGE) {
            add(new Label("Caption"));
            m_caption.addValidationListener(new NotNullValidationListener());
            m_caption.addValidationListener(new StringLengthValidationListener(40));
            m_caption.setSize(40);
            add(m_caption);

            // We only show the title and description fields in the case where
            // getIsImageStepDescriptionAndTitleShown is false.

//        if (ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown()) {
//            add(new Label("Title"));
//            m_title.addValidationListener(new NotNullValidationListener());
//            m_title.setSize(40);
//            m_title.addValidationListener(new StringLengthValidationListener(40));
//            add(m_title);
//
//            add(new Label("Description"));
//            m_description.addValidationListener(new NotNullValidationListener());
//            m_description.addValidationListener(new StringLengthValidationListener(600));
//            m_description.setCols(30);
//            m_description.setRows(5);
//            add(m_description);
//
//        }

            add(new Label("Use Context"));
            m_useContext.setSize(40);
            add(m_useContext);
        }
        m_saveCancel = new SaveCancelSection();
        add(m_saveCancel);

        /*
         * Removed by Quasimodo: Changed editing workflow, so that library comes
         * first Also, library mode has now a link to upload images which will
         * link to this form. Consequently, this link will create a loop, which
         * isn't fatal but confusing. ActionLink library = new ActionLink(
         * "Select an existing image" ); library.addActionListener( new
         * ActionListener() { public void actionPerformed( ActionEvent ev ) {
         * setImageComponent( ev.getPageState(), LIBRARY ); } } ); add( library,
         * ColumnPanel.FULL_WIDTH );
         */
    }

    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancel;
    }

    public ReusableImageAsset getImage(FormSectionEvent event) throws FormProcessException {
        PageState ps = event.getPageState();
        String filename = (String) m_imageFile.getFileName(event);
        File imageFile = m_imageFile.getFile(event);
        try {
            ReusableImageAsset image = new ReusableImageAsset();
            image.loadFromFile(filename, imageFile, ImageAsset.MIME_JPEG);
//            image.setDescription((String) m_caption.getValue(ps));
            return image;
        } catch (IOException ex) {
            ImagesPane.s_log.error("Error loading image from file", ex);
            throw new FormProcessException(ex.getMessage());
        }
    }

    public String getCaption(FormSectionEvent event) {
        PageState ps = event.getPageState();
        return (String) m_caption.getValue(ps);
    }

    public String getDescription(FormSectionEvent event) {
        PageState ps = event.getPageState();
        return (String) m_description.getValue(ps);
    }

    public String getTitle(FormSectionEvent event) {
        PageState ps = event.getPageState();
        return (String) m_title.getValue(ps);
    }

    public String getUseContext(FormSectionEvent event) {
        PageState ps = event.getPageState();
        return (String) m_useContext.getValue(ps);
    }

    public Form getForm() {
        return this;
    }

}
