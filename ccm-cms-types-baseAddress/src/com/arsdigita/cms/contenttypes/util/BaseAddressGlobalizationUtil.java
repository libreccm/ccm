package com.arsdigita.cms.contenttypes.util;

import com.arsdigita.globalization.GlobalizedMessage;

public class BaseAddressGlobalizationUtil {
    
    final public static String BUNDLE_NAME = 
        "com.arsdigita.cms.contenttypes.util.BaseAddressResourceBundle";

    /**
     *  This returns a globalized message using the type specific bundle,
     *  BUNDLE_NAME
     */
    public static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    /**
     *  This returns a globalized message using the type specific bundle,
     *  BUNDLE_NAME
     */
    public static GlobalizedMessage globalize(String key, Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
}
