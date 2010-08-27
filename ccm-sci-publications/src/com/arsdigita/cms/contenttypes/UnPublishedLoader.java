package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/UnPublished.xml"};

    public UnPublishedLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
