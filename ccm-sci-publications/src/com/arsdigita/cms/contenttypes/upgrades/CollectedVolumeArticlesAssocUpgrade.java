package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class CollectedVolumeArticlesAssocUpgrade extends AbstractAssocUpgrade {
    
    @Override
    protected String getTableName() {
        return "ct_collected_volume_article_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "collected_volume_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "article_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("article_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_col_vol_art_map_art_p___0v4";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_col_vol_art_map_col_f_x2knn";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_col_vol_art_map_art_f_j7m8p";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_collected_volume_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "ct_article_in_collected_volume_bundles";
    }

}
