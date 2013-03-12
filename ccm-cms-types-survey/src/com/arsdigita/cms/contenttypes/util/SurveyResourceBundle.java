package com.arsdigita.cms.contenttypes.util;

import java.util.PropertyResourceBundle;
import com.arsdigita.globalization.ChainedResourceBundle;
import com.arsdigita.cms.util.CMSGlobalized;

public class SurveyResourceBundle extends ChainedResourceBundle implements CMSGlobalized {

    public final static String SURVEY_BUNDLE_NAME =
            "com.arsdigita.cms.contenttypes.SurveyResources";

    public SurveyResourceBundle() {
        super();
        addBundle((PropertyResourceBundle) getBundle(SURVEY_BUNDLE_NAME));
        addBundle((PropertyResourceBundle) getBundle(BUNDLE_NAME));
    }
}
