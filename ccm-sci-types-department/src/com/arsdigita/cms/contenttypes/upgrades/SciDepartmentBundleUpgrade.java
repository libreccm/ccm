package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.SciDepartmentBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentBundleUpgrade extends AbstractBundleUpgrade {
   
    @Override
    protected String getBundleTableName() {
        return "ct_sci_department_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_sci_departments";
    }

    @Override
    protected String getIdColName() {
        return "department_id";
    }

    @Override
    protected String getBundleClassName() {
        return SciDepartmentBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_sci_depa_bun_bun_id_p_fq3eo";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_sci_depa_bun_bun_id_f_m74xq";
    }

    @Override
    protected String getSuperBundleTable() {
        return "cms_orgaunit_bundles";
    }
}
