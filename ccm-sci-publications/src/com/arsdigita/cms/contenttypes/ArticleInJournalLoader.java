package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInJournalLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/ArticleInJournal.xml"};

    public ArticleInJournalLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
