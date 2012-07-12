/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ReusableImageAsset;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public interface ImageComponent {

    ReusableImageAsset getImage(FormSectionEvent event) throws FormProcessException;

    String getCaption(FormSectionEvent event);

    String getDescription(FormSectionEvent event);

    String getTitle(FormSectionEvent event);

    String getUseContext(FormSectionEvent event);

    SaveCancelSection getSaveCancelSection();

    Form getForm();
    
}
