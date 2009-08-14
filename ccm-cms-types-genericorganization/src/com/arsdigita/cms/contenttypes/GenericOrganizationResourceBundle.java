package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.CMSGlobalized;
import com.arsdigita.globalization.ChainedResourceBundle;
import java.util.PropertyResourceBundle;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class GenericOrganizationResourceBundle extends ChainedResourceBundle implements CMSGlobalized {

    public final static String GENERIC_ORGANIZATION_BUNDLE_NAME =
        "com.arsdigita.cms.contenttypes.GenericOrganizationResources";

    public GenericOrganizationResourceBundle() {
        super();
        addBundle((PropertyResourceBundle)getBundle(GENERIC_ORGANIZATION_BUNDLE_NAME));
        addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }

}
