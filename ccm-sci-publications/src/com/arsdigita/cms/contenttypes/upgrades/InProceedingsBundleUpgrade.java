package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.InProceedingsBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class InProceedingsBundleUpgrade extends AbstractBundleUpgrade {
   
    @Override
    protected String getBundleTableName() {
        return "ct_inproceedings_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_inproceedings";
    }

    @Override
    protected String getIdColName() {
        return "inproceedings_id";
    }

    @Override
    protected String getBundleClassName() {
        return InProceedingsBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_inprocee_bun_bun_id_p_v2f4k";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_inprocee_bun_bun_id_f_cam1l";
    }

    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_bundles";
    }
}