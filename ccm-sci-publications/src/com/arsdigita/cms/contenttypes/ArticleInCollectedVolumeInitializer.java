package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolumeInitializer extends ContentTypeInitializer {

    public ArticleInCollectedVolumeInitializer() {
        super("ccm-sci-publications.pdl.mf", ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/ArticleInCollectedVolume.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/ArticleInCollectedVolume.xml";
    }

}
