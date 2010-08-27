package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class GreyLiteratureLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/GreyLiterature.xml"};

    public GreyLiteratureLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
