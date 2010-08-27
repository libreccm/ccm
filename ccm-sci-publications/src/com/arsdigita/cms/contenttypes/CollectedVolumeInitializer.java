package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class CollectedVolumeInitializer extends ContentTypeInitializer {

    public CollectedVolumeInitializer() {
        super("ccm-sci-publications.pdl.mf", CollectedVolume.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/CollectedVolume.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/CollectedVolume.xml";
    }

}
