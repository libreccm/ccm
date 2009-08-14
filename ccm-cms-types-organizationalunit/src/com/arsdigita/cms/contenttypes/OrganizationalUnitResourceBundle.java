package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.CMSGlobalized;
import com.arsdigita.globalization.ChainedResourceBundle;
import java.util.PropertyResourceBundle;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnitResourceBundle extends ChainedResourceBundle implements CMSGlobalized {

     public final static String ORGANIZATIONAL_UNIT_BUNDLE_NAME =
        "com.arsdigita.cms.contenttypes.OrganizationalUnitResources";

    public OrganizationalUnitResourceBundle() {
        super();
        addBundle((PropertyResourceBundle)getBundle(ORGANIZATIONAL_UNIT_BUNDLE_NAME));
        addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }

}
