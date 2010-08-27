package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Series.xml"};

    public SeriesLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
