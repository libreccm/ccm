package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedInitializer extends ContentTypeInitializer {

    public UnPublishedInitializer() {
        super("ccm-sci-publications.pdl.mf", UnPublished.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/UnPublished.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/UnPublished.xml";
    }

}
