package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.ProceedingsBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ProceedingsBundleUpgrade extends AbstractBundleUpgrade {
    
    public ProceedingsBundleUpgrade() {
        super("ProceedingsBundleUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
        new ProceedingsBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_proceedings_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_proceedings";
    }

    @Override
    protected String getIdColName() {
        return "proceedings_id";
    }

    @Override
    protected String getBundleClassName() {
        return ProceedingsBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_proceed_bund_bun_id_p_sh1xo";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_proceed_bund_bun_id_f_loele";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_with_publisher_bundles";
    }
    
}
