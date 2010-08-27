package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class WorkingPaperInitializer extends ContentTypeInitializer {

    public WorkingPaperInitializer() {
        super("ccm-sci-publications.pdl.mf", WorkingPaper.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/WorkingPaper.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/WorkingPaper.xml";
    }

}
