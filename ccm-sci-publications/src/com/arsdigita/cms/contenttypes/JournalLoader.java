package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class JournalLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Journal.xml"};

    public JournalLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
