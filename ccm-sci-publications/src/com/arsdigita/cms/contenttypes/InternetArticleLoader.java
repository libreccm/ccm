package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class InternetArticleLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/InternetArticle.xml"};

    public InternetArticleLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
