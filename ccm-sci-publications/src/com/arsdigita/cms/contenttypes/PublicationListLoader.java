package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationListLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/PublicationList.xml"};

    public PublicationListLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
