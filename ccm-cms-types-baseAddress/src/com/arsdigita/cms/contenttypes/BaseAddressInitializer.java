package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 * The CMS initializer
 *
 */
public class BaseAddressInitializer extends ContentTypeInitializer {
    public final static String versionId =
        "$Id: BaseAddressInitializer.java $" +
        "$Author: quasi $" +
        "$DateTime: 2009/03/15 $";
    private static final Logger s_log = Logger.getLogger(BaseAddressInitializer.class);

    public BaseAddressInitializer() {
        super("ccm-cms-types-baseAddress.pdl.mf",
              BaseAddress.BASE_DATA_OBJECT_TYPE);
    }

    public String[] getStylesheets() {
        return new String[] {
            "/static/content-types/com/arsdigita/cms/contenttypes/BaseAddress.xsl"
        };
    }

}
