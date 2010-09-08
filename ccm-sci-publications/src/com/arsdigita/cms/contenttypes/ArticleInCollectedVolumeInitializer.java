package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolumeInitializer extends ContentTypeInitializer {

    /**
     * The pdl.mf file used here is empty, since the
     * {@link PublicationInitializer} loads all things using the pdl.mf file
     * of the module. Also, it may causes on silly errors in the load-bundle
     * step if the same pdl.mf file is used in more than one initializer.
     */
    public ArticleInCollectedVolumeInitializer() {
        super("empty.pdl.mf", ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE);
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
