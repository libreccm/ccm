package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedingsInitializer extends ContentTypeInitializer {

    public InProceedingsInitializer() {
        super("ccm-sci-publications.pdl.mf", InProceedings.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/InProceedings.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/InProceedings.xml";
    }

}
