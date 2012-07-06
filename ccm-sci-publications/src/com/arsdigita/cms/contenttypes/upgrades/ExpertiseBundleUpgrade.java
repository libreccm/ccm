package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.ExpertiseBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ExpertiseBundleUpgrade extends AbstractBundleUpgrade {
       
    @Override
    protected String getBundleTableName() {
        return "ct_expertise_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_expertise";
    }

    @Override
    protected String getIdColName() {
        return "expertise_id";
    }

    @Override
    protected String getBundleClassName() {
        return ExpertiseBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_expert_bund_bund_id_p_5e0fp";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_expert_bund_bund_id_f_ieiya";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_bundles";
    }
    
}
