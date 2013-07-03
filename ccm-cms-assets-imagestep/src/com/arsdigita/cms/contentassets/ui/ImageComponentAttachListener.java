/*
 * Copyright (C) 2004-2012 Sören Bernstein, Universität Bremen. All Rights Reserved.
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
 * A listener to attach an image to a content item.
 * 
 * This listerner is used by {@link ImageStepEdit}.
 * 
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageComponentAttachListener extends ImageComponentAbstractListener {

    private final ImageStep m_imageStep;
    private static final Logger s_log = Logger.getLogger(
                                        ImageComponentAttachListener.class);

    /**
     * Constructor.
     * @param imageComponent
     * @param imageStep 
     */
    public ImageComponentAttachListener(MapComponentSelectionModel imageComponent, 
                                        ImageStep imageStep) {
        super(imageComponent);
        m_imageStep = imageStep;
    }

    /**
     * 
     * @param event
     * @param ps
     * @param component
     * @param image 
     */
    @Override
    protected void processImage(FormSectionEvent event, 
                                PageState ps, 
                                ImageComponent component, 
                                ReusableImageAsset image) {
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
        
    }
}
