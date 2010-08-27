package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedingsLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/InProceedings.xml"};

    public InProceedingsLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
