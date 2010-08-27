package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ExpertiseLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Expertise.xml"};

    public ExpertiseLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
