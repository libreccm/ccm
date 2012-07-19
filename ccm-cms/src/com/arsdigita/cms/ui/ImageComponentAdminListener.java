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

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
class ImageComponentAdminListener extends ImageComponentAbstractListener implements ActionListener {

    public ImageComponentAdminListener(MapComponentSelectionModel imageComponent) {
        super(imageComponent);
    }

    @Override
    protected void processImage(FormSectionEvent event, PageState ps, ImageComponent component, ReusableImageAsset image) {
    }

    public void actionPerformed(ActionEvent ev) {
        setImageComponent(ev.getPageState(), ImageComponent.UPLOAD);
    }
}
