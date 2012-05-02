package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.ArticleInJournalBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ArticleInJournalBundleUpgrade extends AbstractBundleUpgrade {
    
    public ArticleInJournalBundleUpgrade() {
        super("ArticleInJournalBundleUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
        new ArticleInJournalBundleUpgrade().run(args);
    }
    
    
    @Override
    protected String getBundleTableName() {
        return "ct_article_in_journal_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_article_in_journal";
    }

    @Override
    protected String getIdColName() {
        return "article_in_journal_id";
    }

    @Override
    protected String getBundleClassName() {
        return ArticleInJournalBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_art_in_jou_bun_bun__p__c88r";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_art_in_jou_bun_bun__f_3_p3l";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_bundles";
    }
}
