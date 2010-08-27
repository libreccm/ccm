package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInJournalInitializer extends ContentTypeInitializer {

    public ArticleInJournalInitializer() {
        super("ccm-sci-publications.pdl.mf", ArticleInJournal.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/ArticleInJournal.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/ArticleInJournal.xml";
    }

}
