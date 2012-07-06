package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.SciInstituteBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciInstituteBundleUpgrade extends AbstractBundleUpgrade {
  
    @Override
    protected String getBundleTableName() {
        return "ct_sci_institute_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_sci_institutes";
    }

    @Override
    protected String getIdColName() {
        return "institute_id";
    }

    @Override
    protected String getBundleClassName() {
        return SciInstituteBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_sci_inst_bun_bun_id_p_vbt1k";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_sci_inst_bun_bun_id_f_b30ys";
    }

    @Override
    public String getSuperBundleTable() {
        return "cms_orgaunit_bundles";
    }
}
