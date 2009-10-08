package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.CMSGlobalized;
import com.arsdigita.globalization.ChainedResourceBundle;
import java.util.PropertyResourceBundle;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
@SuppressWarnings("unchecked")
public class ProjectResourceBundle extends ChainedResourceBundle implements CMSGlobalized {

    public final static String PROJECT_BUNDLE_NAME = 
            "com.arsdigita.cms.contenttypes.ProjectResources";

    public ProjectResourceBundle() {
        super();
        addBundle((PropertyResourceBundle)getBundle(PROJECT_BUNDLE_NAME));
        addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }
}
