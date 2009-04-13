package com.arsdigita.cms.contenttypes.util;

import com.arsdigita.globalization.GlobalizedMessage;

public class PersonGlobalizationUtil {
    final public static String BUNDLE_NAME = 
	"com.arsdigita.cms.contenttypes.util.PersonResourceBundle";

    public static GlobalizedMessage globalize (String key) {
	return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static GlobalizedMessage globalize (String key, Object[] args) {
	return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }
}