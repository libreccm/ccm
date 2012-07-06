package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class JournalArticlesAssocUpgrade extends AbstractAssocUpgrade {
  
    @Override
    protected String getTableName() {
        return "ct_journal_article_map";
    }

    @Override
    protected String getOwnerIdCol() {
        return "journal_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "article_in_journal_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("article_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_jou_art_map_art_in__p_w1c45";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_jou_art_map_jour_id_f_anw93";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_jou_art_map_art_in__f_gx_9q";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_journal_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "ct_article_in_journal_bundles";
    }

}
