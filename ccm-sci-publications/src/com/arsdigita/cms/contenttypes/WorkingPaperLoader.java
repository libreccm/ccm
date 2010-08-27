package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class WorkingPaperLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/WorkingPaper.xml"};

    public WorkingPaperLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
