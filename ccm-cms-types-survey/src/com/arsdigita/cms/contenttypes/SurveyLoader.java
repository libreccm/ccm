package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;

/**
 * Loader.
 *
 * @author Sören Bernstein
 * @version $Id: SurveyLoader.java $
 */
public class SurveyLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Survey.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }
}
