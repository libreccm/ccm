package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ArticleInCollectedVolumeBundleUpgrade extends AbstractBundleUpgrade {
       
    @Override
    protected String getBundleTableName() {
        return "ct_article_in_collected_volume_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_article_in_collected_volume";
    }

    @Override
    protected String getIdColName() {
        return "article_id";
    }

    @Override
    protected String getBundleClassName() {
        return ArticleInCollectedVolumeBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_art_in_col_vol_bun__p_1khsq";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_art_in_col_vol_bun__f_u4b17";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_bundles";
    }
    
    
}
