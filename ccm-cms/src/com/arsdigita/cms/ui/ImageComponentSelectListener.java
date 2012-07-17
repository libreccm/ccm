/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ReusableImageAsset;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageComponentSelectListener extends ImageComponentAbstractListener {

    private static final Logger s_log = Logger.getLogger(ImageComponentSelectListener.class);

    public ImageComponentSelectListener(MapComponentSelectionModel imageComponent) {
        super(imageComponent);
    }

    protected void processImage(FormSectionEvent event, PageState ps, ImageComponent component, ReusableImageAsset image) {
        // SELECT {
        String name = image.getDisplayName();
        BigDecimal id = image.getID();
        BigDecimal width = image.getWidth();
        BigDecimal height = image.getHeight();
        // SELECT }
    }
}
