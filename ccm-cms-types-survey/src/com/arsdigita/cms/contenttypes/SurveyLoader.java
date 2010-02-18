package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;

/**
 * Loader.
 *
 * @author Sören Bernstein
 */
public class SurveyLoader extends AbstractContentTypeLoader {

    public final static String versionId =
            "$Id: SurveyLoader.java $" +
            "$Author: quasi $" +
            "$DateTime: 2010/02/18 $";
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Survey.xml"
    };

    public String[] getTypes() {
        return TYPES;
    }
}
