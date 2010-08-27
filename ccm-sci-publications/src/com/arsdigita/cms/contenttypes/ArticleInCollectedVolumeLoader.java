package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolumeLoader extends AbstractContentTypeLoader {

    private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/ArticleInCollectedVolume.xml"};

    public ArticleInCollectedVolumeLoader() {
        super();
    }

    public String[] getTypes() {
        return TYPES;
    }

}
