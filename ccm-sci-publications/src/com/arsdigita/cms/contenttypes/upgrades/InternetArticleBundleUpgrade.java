package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.InternetArticleBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class InternetArticleBundleUpgrade extends AbstractBundleUpgrade {

    public InternetArticleBundleUpgrade() {
        super("InternetArticleBundleUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new InternetArticleBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_internet_article_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_internet_article";
    }

    @Override
    protected String getIdColName() {
        return "internet_article_id";
    }

    @Override
    protected String getBundleClassName() {
       return InternetArticleBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_inte_art_bun_bun_id_p_vjjsh";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_inte_art_bun_bun_id_f_dretd";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_bundles";
    }
    
}
