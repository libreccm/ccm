/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ReusableImageAsset;
import org.apache.log4j.Logger;

/**
 * A listener to select an image and save it for later use.
 * 
 * This listerner is used by {@link ImageSelectPage}.
 * 
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageComponentSelectListener extends ImageComponentAbstractListener {

    private static final Logger S_LOG = Logger.getLogger(
                                        ImageComponentSelectListener.class);
    private final ImageSelectResultComponent m_resultPane;

    public ImageComponentSelectListener(MapComponentSelectionModel imageComponent, 
                                        ImageSelectResultComponent resultPane) {
        super(imageComponent);
        m_resultPane = resultPane;
    }

    @Override
    protected void cancelled(PageState ps) {
        super.cancelled(ps);
        m_resultPane.reset(ps);
    }
    
    /**
     * 
     * @param event
     * @param ps
     * @param component
     * @param image 
     */
    protected void processImage(FormSectionEvent event, 
                                PageState ps, 
                                ImageComponent component, 
                                ReusableImageAsset image) {
        m_resultPane.setResult(image, (String) m_imageComponent.getSelectedKey(ps));
        m_imageComponent.setSelectedKey(ps, ImageSelectPage.RESULT);
    }
}
