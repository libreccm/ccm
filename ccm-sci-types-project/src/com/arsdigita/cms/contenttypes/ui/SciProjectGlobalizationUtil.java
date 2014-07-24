package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectGlobalizationUtil {
    
    public static final String BUNDLE_NAME = "com.arsdigita.cms.contenttypes.SciProjectResources";
    
    public static GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }
    
    public static GlobalizedMessage globalize(final String key, 
                                              final Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
    
}
