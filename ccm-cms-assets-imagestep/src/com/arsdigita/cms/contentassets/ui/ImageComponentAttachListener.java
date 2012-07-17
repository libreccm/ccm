/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.ui.ImageComponent;
import com.arsdigita.cms.ui.ImageComponentAbstractListener;
import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageComponentAttachListener extends ImageComponentAbstractListener {

    private final ImageStep m_imageStep;
    private static final Logger s_log = Logger.getLogger(ImageComponentAttachListener.class);

    public ImageComponentAttachListener(MapComponentSelectionModel imageComponent, ImageStep imageStep) {
        super(imageComponent);
        m_imageStep = imageStep;
    }

    @Override
    protected void processImage(FormSectionEvent event, PageState ps, ImageComponent component, ReusableImageAsset image) {
        ContentItem item = m_imageStep.getItem(ps);
        if (null == item) {
            s_log.error("No item selected in ImageStepEdit",
                    new RuntimeException());
            return;
        }
        ItemImageAttachment attachment = m_imageStep.getAttachment(ps);
        if (null == attachment) {
            attachment = new ItemImageAttachment(item, image);
        }

        attachment.setCaption(component.getCaption(event));
        
//        setImageComponent(ps, ImageComponent.LIBRARY);
    }
}
