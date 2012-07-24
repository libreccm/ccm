/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.math.BigDecimal;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageLibraryComponent extends SimpleContainer implements ImageComponent, Resettable {

    private final ImageChooser m_chooser;
    private final ItemSelectionModel m_imageModel;
    private final BigDecimalParameter m_imageID;
    private final Form m_form;
    private final TextField m_caption;
    private final TextField m_description;
    private final TextField m_title;
    private final TextField m_useContext;
    private final SaveCancelSection m_saveCancel;
    private int m_mode;

    public ImageLibraryComponent() {
        this(ImageComponent.ATTACH_IMAGE);
    }

    public ImageLibraryComponent(final int mode) {
        m_mode = mode;
        m_imageID = new BigDecimalParameter("imageID");
        m_imageModel = new ItemSelectionModel(m_imageID);
        m_chooser = new ImageChooser(ContentItem.DRAFT, m_mode);
        m_chooser.addImageActionListener(new ImageBrowser.LinkActionListener() {

            public void deleteClicked(final PageState state, final BigDecimal imageID) {
                ImagesPane.S_LOG.debug("Clicked delete");
                final ReusableImageAsset image = new ReusableImageAsset(imageID);
                image.delete();
            }

            public void linkClicked(final PageState state, final BigDecimal imageID) {
                ImagesPane.S_LOG.debug("Clicked select");
                try {
                    final ReusableImageAsset image = new ReusableImageAsset(imageID);
                    m_imageModel.setSelectedObject(state, image);
                } catch (DataObjectNotFoundException ex) {
                    ImagesPane.S_LOG.error("Selected non-existant image: " + imageID, ex);
                }
            }
        });
        add(m_chooser);

        // Form for additional fields and submit
        m_form = new Form("imageLibraryComponent", new ColumnPanel(2));
        add(m_form);

        // Initialize all wisgets
        m_caption = new TextField("caption");
        m_description = new TextField("description");
        m_title = new TextField("title");
        m_useContext = new TextField("useContext");

        // Show additional fields only in default mode a.k.a. ATTACH_IMAGE like
        // in image-step
        if (m_mode == ImageComponent.ATTACH_IMAGE) {
            m_form.add(new Label("Caption"));
            m_caption.addValidationListener(new NotNullValidationListener());
            m_caption.setSize(40);
            m_form.add(m_caption);
            m_description.addValidationListener(new NotNullValidationListener());
            m_description.setSize(40);
            m_title.addValidationListener(new NotNullValidationListener());
            m_title.setSize(40);
            // Only show the title and description fields where these have
            // been explicitly requested.
        /*
             * if
             * (ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown())
             * { m_form.add(new Label("Description"));
             * m_form.add(m_description); m_form.add(new Label("Title"));
             * m_form.add(m_title); }
             */
            m_form.add(new Label("Use Context"));
            m_useContext.setSize(40);
            m_form.add(m_useContext);
        }

        // save and cancel buttons
        m_saveCancel = new SaveCancelSection();
        if (m_mode == ImageComponent.SELECT_IMAGE || m_mode == ImageComponent.ATTACH_IMAGE) {
            m_form.add(m_saveCancel);
        }
    }

    public ReusableImageAsset getImage(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        return (ReusableImageAsset) m_imageModel.getSelectedItem(state);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, m_imageID);
    }

    public String getCaption(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        return (String) m_caption.getValue(state);
    }

    public String getDescription(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        return (String) m_description.getValue(state);
    }

    public String getTitle(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        return (String) m_title.getValue(state);
    }

    public String getUseContext(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        return (String) m_useContext.getValue(state);
    }

    public Form getForm() {
        return m_form;
    }

    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancel;
    }

    public void addUploadLink(final ActionListener actionListener) {
        // Add action link to image upload component
        if (m_mode != ImageComponent.DISPLAY_ONLY) {
            final ActionLink upload = new ActionLink("Upload new image");
            upload.addActionListener(actionListener);
            add(upload, ColumnPanel.FULL_WIDTH);
        }
    }

    // Reset this component
    public void reset(final PageState state) {
        // clear selection
        m_imageModel.clearSelection(state);
        m_chooser.clearSelection(state);
        m_chooser.clearKeyword(state);
    }
}
