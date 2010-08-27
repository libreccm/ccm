package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class PublisherLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Publisher.xml"};

    public PublisherLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
