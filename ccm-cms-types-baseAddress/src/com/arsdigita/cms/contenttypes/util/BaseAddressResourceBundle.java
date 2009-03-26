package com.arsdigita.cms.contenttypes.util;

import java.util.PropertyResourceBundle;
import com.arsdigita.globalization.ChainedResourceBundle;
import com.arsdigita.cms.CMSGlobalized;

public class BaseAddressResourceBundle extends ChainedResourceBundle implements CMSGlobalized {
    
    public final static String BASE_ADDRESS_BUNDLE_NAME = 
        "com.arsdigita.cms.contenttypes.BaseAddressResources";

    public BaseAddressResourceBundle() {
        super();
        addBundle((PropertyResourceBundle)getBundle(BASE_ADDRESS_BUNDLE_NAME));
        addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }
}
