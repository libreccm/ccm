package com.arsdigita.cms.contenttypes;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkGlobalizationUtil {

    final public static String BUNDLE_NAME =
            "com.arsdigita.cms.contenttypes.ResearchNetworkResourceBundle";

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
