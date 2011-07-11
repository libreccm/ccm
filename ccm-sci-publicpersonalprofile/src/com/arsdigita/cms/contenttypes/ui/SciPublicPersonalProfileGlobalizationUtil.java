package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicPersonalProfileGlobalizationUtil {
    
      public static final String BUNDLE_NAME =
                               "com.arsdigita.cms.contenttypes.ui.SciPublicPersonalProfileResources";

    public static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static GlobalizedMessage globalize(String key, Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
    
}
