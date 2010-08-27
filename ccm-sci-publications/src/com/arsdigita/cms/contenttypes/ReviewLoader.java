package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ReviewLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Review.xml"};

    public ReviewLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
