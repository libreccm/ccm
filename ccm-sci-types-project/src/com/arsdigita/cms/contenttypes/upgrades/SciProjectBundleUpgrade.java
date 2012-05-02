package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.SciProjectBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciProjectBundleUpgrade extends AbstractBundleUpgrade {
    
    public SciProjectBundleUpgrade() {
        super("SciProjectBundleUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
        new SciProjectBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_sci_project_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_sci_projects";
    }

    @Override
    protected String getIdColName() {
        return "project_id";
    }

    @Override
    protected String getBundleClassName() {
        return SciProjectBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_sci_pro_bund_bun_id_p_tynqh";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_sci_pro_bund_bun_id_f_n6se2";
    }
    
    @Override
    public String getSuperBundleTable() {
        return "cms_orgaunit_bundles";
    }
    
}
