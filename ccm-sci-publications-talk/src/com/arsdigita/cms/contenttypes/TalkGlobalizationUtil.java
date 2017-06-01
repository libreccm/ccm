package com.arsdigita.cms.contenttypes;

import com.arsdigita.globalization.GlobalizedMessage;

public class TalkGlobalizationUtil {

    public static final String BUNDLE_NAME
                                   = "com.arsdigita.cms.contenttypes.TalkResources";

    public static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static GlobalizedMessage globalize(String key, Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }

}
