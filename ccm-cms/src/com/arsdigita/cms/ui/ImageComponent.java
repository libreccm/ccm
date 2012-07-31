/*
 * 
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ReusableImageAsset;

/**
 * Interface for ImageCompnents.
 * 
 * All components for image handling (like {@link ImageLibraryComponent} or
 * {@link ImageUploadComponent}) should implement this interface.
 * 
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public interface ImageComponent {

    /**
     * The modes
     */
    public static final int DISPLAY_ONLY = 0;
    public static final int SELECT_IMAGE = 1;
    public static final int ATTACH_IMAGE = 2;
    public static final int ADMIN_IMAGES = 3;

    public static final String UPLOAD = "upload";
    public static final String LIBRARY = "library";

    ReusableImageAsset getImage(FormSectionEvent event) throws FormProcessException;

    String getCaption(FormSectionEvent event);

    String getDescription(FormSectionEvent event);

    String getTitle(FormSectionEvent event);

    String getUseContext(FormSectionEvent event);

    SaveCancelSection getSaveCancelSection();

    Form getForm();
    
}
