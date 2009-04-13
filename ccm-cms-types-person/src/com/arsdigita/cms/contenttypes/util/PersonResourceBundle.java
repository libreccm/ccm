package com.arsdigita.cms.contenttypes.util;

import java.util.PropertyResourceBundle;
import com.arsdigita.globalization.ChainedResourceBundle;
import com.arsdigita.cms.CMSGlobalized;

public class PersonResourceBundle extends ChainedResourceBundle implements CMSGlobalized {
    public static final String PERSON_BUNDLE_NAME =
	"com.arsdigita.cms.contenttypes.PersonResources";

    public PersonResourceBundle() {
	super();
	addBundle((PropertyResourceBundle)getBundle(PERSON_BUNDLE_NAME));
	addBundle((PropertyResourceBundle)getBundle(BUNDLE_NAME));
    }
}