package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class DepartmentLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Department.xml"
    };
    
    public String[] getTypes() {
        return TYPES;
    }

}
