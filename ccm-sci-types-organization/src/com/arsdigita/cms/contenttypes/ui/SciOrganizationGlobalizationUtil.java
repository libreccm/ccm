package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationGlobalizationUtil {

    public static final String BUNDLE_NAME = "com.arsdigita.cms.contenttypes.ui.OrganizationResources";

    public static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, BUNDLE_NAME);
    }

    public static GlobalizedMessage globalize(String key, Object[] args) {
        return new GlobalizedMessage(key, BUNDLE_NAME, args);
    }

}
