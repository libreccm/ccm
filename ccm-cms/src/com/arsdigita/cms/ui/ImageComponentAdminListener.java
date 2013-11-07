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

    public ImageComponentAdminListener(MapComponentSelectionModel imageComponent, ComponentMap pane) {
        super(imageComponent);
        m_pane = pane;
    }

    @Override
    protected void cancelled(PageState ps) {
        m_pane.reset(ps);
    }

    @Override
    protected void processImage(FormSectionEvent event, PageState ps, ImageComponent component, ReusableImageAsset image) {
        m_pane.reset(ps);
    }

    public void actionPerformed(ActionEvent ev) {
        setImageComponent(ev.getPageState(), ImageComponent.UPLOAD);
    }
}
