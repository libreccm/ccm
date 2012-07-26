/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ReusableImageAsset;
import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class ImageComponentSelectListener extends ImageComponentAbstractListener {

    private static final Logger S_LOG = Logger.getLogger(ImageComponentSelectListener.class);
    private final ImageSelectResultPane m_resultPane;

    public ImageComponentSelectListener(MapComponentSelectionModel imageComponent, ImageSelectResultPane resultPane) {
        super(imageComponent);
        m_resultPane = resultPane;
    }

    @Override
    protected void cancelled(PageState ps) {
        super.cancelled(ps);
        m_resultPane.reset(ps);
    }
    
    protected void processImage(FormSectionEvent event, PageState ps, ImageComponent component, ReusableImageAsset image) {
        m_imageComponent.setSelectedKey(ps, ImageSelectPage.RESULT);
        m_resultPane.reset(ps);
    }
}
