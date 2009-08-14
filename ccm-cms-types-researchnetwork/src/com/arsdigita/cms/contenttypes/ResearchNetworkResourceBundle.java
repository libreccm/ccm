package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.CMSGlobalized;
import com.arsdigita.globalization.ChainedResourceBundle;
import java.util.PropertyResourceBundle;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkResourceBundle extends ChainedResourceBundle implements CMSGlobalized {

    public final static String RESEARCH_NETWORK_BUNDLE_NAME =
        "com.arsdigita.cms.contenttypes.ResearchNetworkResources";

    public ResearchNetworkResourceBundle() {
        super();
        addBundle((PropertyResourceBundle)getBundle(RESEARCH_NETWORK_BUNDLE_NAME));
        addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }

}
