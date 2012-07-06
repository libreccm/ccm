package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.JournalBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class JournalBundleUpgrade extends AbstractBundleUpgrade {
    
    @Override
    protected String getBundleTableName() {
        return "ct_journal_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_journal";
    }

    @Override
    protected String getIdColName() {
        return "journal_id";
    }

    @Override
    protected String getBundleClassName() {
        return JournalBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_journ_bundl_bund_id_p_ecg0j";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_journ_bundl_bund_id_f_0jtmz";
    }
}
