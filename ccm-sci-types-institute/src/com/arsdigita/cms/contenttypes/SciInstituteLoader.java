package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteLoader extends AbstractContentTypeLoader {

    public SciInstituteLoader() {
        super();
    }

    @Override
    public String[] getTypes() {
        return new String[]{
                    "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciInstitute.xml"};
    }
}
