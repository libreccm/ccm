package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciDepartment.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }

}
