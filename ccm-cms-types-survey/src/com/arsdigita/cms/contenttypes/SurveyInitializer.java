package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein;
 */
public class SurveyInitializer extends ContentTypeInitializer {

    public final static String versionId =
            "$Id: SurveyInitializer.java $" +
            "$Author: quasi $" +
            "$DateTime: 2010/02/18 $";
    private static final Logger s_log = Logger.getLogger(SurveyInitializer.class);

    public SurveyInitializer() {
        super("ccm-cms-types-survey.pdl.mf",
                Survey.BASE_DATA_OBJECT_TYPE);
    }

    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/Survey.xsl"
                };
    }

    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Survey.xml";
    }
}
