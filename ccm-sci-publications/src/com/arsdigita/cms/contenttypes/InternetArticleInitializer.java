package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class InternetArticleInitializer extends ContentTypeInitializer {

    public InternetArticleInitializer() {
        super("ccm-sci-publications.pdl.mf", InternetArticle.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/InternetArticle.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/InternetArticle.xml";
    }

}
