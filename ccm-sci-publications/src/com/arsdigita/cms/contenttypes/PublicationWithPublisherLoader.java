package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisherLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/PublicationWithPublisher.xml"};

    public PublicationWithPublisherLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
