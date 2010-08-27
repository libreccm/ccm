package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class GreyLiteratureInitializer extends ContentTypeInitializer {

    public GreyLiteratureInitializer() {
        super("ccm-sci-publications.pdl.mf", GreyLiterature.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/GreyLiterature.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/GreyLiterature.xml";
    }

}
