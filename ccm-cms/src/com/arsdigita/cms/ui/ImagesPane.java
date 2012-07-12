/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.toolbox.ui.LayoutPanel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A LayoutPanel to insert into ContentSectionPage or ImageSelectPage
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImagesPane extends LayoutPanel implements Resettable {

    public static final Logger s_log = Logger.getLogger(BrowsePane.class);
    //private ImageChooser imageChooser;
    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;
    private final String UPLOAD = "upload";
    private final String LIBRARY = "library";

    public ImagesPane() {
        // Left column is empty, this is only to provide the same layout for all
        // tabs in ContentSectionPage
        setLeft(new SimpleComponent());

        SegmentedPanel body = new SegmentedPanel();
        body.addSegment(
                new Label(GlobalizationUtil.globalize("cms.ui.image_browser")),
                new ImageChooser(ContentItem.DRAFT, ImageBrowser.ADMIN_IMAGES));

        setBody(body);
        
        m_imageComponentKey = new StringParameter("imageComponent");

        ParameterSingleSelectionModel componentModel = new ParameterSingleSelectionModel(m_imageComponentKey);
        m_imageComponent = new MapComponentSelectionModel(componentModel, new HashMap());

        Map selectors = m_imageComponent.getComponentsMap();

//        ImageUploadComponent upload = new ImageUploadComponent();
//        upload.getForm().addInitListener(this);
//        upload.getForm().addProcessListener(this);
//        selectors.put(UPLOAD, upload);
//        add(upload);
//
//        ImageLibraryComponent library = new ImageLibraryComponent();
//        library.getForm().addInitListener(this);
//        library.getForm().addProcessListener(this);
//        selectors.put(LIBRARY,
//                library);
//        add(library);
    }

    public final void register(Page page) {
        super.register(page);
    }

    public final void reset(PageState state) {
        super.reset(state);
    }

    /*
     * // Private classes and methods private final class ProcessListener
     * implements FormProcessListener {
     *
     * public void process(FormSectionEvent event) throws FormProcessException {
     * PageState ps = event.getPageState(); ImageComponent component =
     * getImageComponent(ps);
     *
     * if (!component.getSaveCancelSection().getSaveButton().isSelected(ps)) {
     * return; }
     *
     * ContentItem item = m_imageStep.getItem(ps); if (null == item) {
     * s_log.error("No item selected in ImageStepEdit", new RuntimeException());
     * return; }
     *
     * ReusableImageAsset image = component.getImage(event);
     *
     * ItemImageAttachment attachment = m_imageStep.getAttachment(ps); if (null
     * == attachment) { attachment = new ItemImageAttachment(item, image); }
     * attachment.setCaption(component.getCaption(event));
     *
     * // We only set the description and title based on the UI in // the case
     * where getIsImageStepDescriptionAndTitleShown is true. // Otherwise, we
     * leave this as the default value. This means // existing values are not
     * overwritten if the image is edited when //
     * isImageStepDescriptionAndTitleShown is false. if
     * (ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown())
     * { attachment.setDescription(component.getDescription(event));
     * attachment.setTitle(component.getTitle(event)); }
     * attachment.setUseContext(component.getUseContext(event)); } }
     *
     * private final class SubmissionListener implements FormSubmissionListener
     * {
     *
     * public final void submitted(final FormSectionEvent e) { final PageState s
     * = e.getPageState();
     *
     * }
     * }
     */
    public void init(FormSectionEvent event)
            throws FormProcessException {
        PageState ps = event.getPageState();

//        ItemImageAttachment attachment = m_imageStep.getAttachment(ps);
//        if (null == attachment) {
        // XXX: Do something
//        }
    }
}
