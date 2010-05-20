package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;


/**
 * Loader
 *
 */
public class BaseAddressLoader extends AbstractContentTypeLoader {
    public final static String versionId =
        "$Id: BaseAddressLoader.java$" +
        "$Author: quasi $" +
        "$DateTime: 2009/03/15 $";


    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/BaseAddress.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
