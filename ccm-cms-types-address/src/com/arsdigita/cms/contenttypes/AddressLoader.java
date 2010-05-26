package com.arsdigita.cms.contenttypes;

/**
 * Loader
 *
 */
public class AddressLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Address.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }
}
