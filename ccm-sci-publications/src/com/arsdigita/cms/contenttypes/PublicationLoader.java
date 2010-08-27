package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Publication.xml"};

    public PublicationLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
