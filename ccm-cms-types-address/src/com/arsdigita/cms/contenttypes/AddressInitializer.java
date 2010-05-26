package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 * The CMS initializer
 *
 */
public class AddressInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(AddressInitializer.class);

    public AddressInitializer() {
        super("ccm-cms-types-address.pdl.mf",
                Address.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/Address.xsl"
                };
    }
}
