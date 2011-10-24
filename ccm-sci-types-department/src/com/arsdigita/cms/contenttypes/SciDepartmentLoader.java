package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentLoader extends AbstractContentTypeLoader {

    public SciDepartmentLoader() {
        super();
    }

    @Override
    public String[] getTypes() {
        return new String[]{
                    "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciDepartment.xml"};
    }
}
