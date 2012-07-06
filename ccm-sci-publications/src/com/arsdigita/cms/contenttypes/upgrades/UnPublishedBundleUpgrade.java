package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.UnPublishedBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class UnPublishedBundleUpgrade extends AbstractBundleUpgrade {
  
    @Override
    protected String getBundleTableName() {
        return "ct_unpublished_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_unpublished";
    }

    @Override
    protected String getIdColName() {
        return "unpublished_id";
    }
    
    @Override
    protected String getBundleClassName() {
        return UnPublishedBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_unpubli_bund_bun_id_p_yqn7m";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_unpubli_bund_bun_id_f_ipfu7";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_bundles";
    }
    
}
