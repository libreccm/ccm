package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class MonographLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Monograph.xml"};

    public MonographLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
