/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.toolbox.ui.ComponentMap;

/**
 * A listener to administer images.
 * 
 * This listerner is used by {@link ImagesPane}.
 * 
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
class ImageComponentAdminListener extends ImageComponentAbstractListener implements ActionListener {

    private final ComponentMap m_pane;

    public ImageComponentAdminListener(final MapComponentSelectionModel imageComponent, 
                                       final ComponentMap pane) {
        super(imageComponent);
        m_pane = pane;
    }

    @Override
    protected void cancelled(final PageState state) {
        m_pane.reset(state);
    }

    @Override
    protected void processImage(final FormSectionEvent event, 
                                final PageState state, 
                                final ImageComponent component, 
                                final ReusableImageAsset image) {
        //m_pane.reset(state);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        setImageComponent(event.getPageState(), ImageComponent.UPLOAD);
    }
}
