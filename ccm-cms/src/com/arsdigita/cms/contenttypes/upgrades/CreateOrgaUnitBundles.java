package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitBundle;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class CreateOrgaUnitBundles extends AbstractBundleUpgrade {
  
    @Override
    protected String getBundleTableName() {
        return "cms_orgaunit_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "cms_orgaunits";
    }

    @Override
    protected String getIdColName() {
        return "orgaunit_id";
    }

    @Override
    protected String getBundleClassName() {
        return GenericOrganizationalUnitBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_orgau_bund_bund_id_p_cfjhf";
    }

    @Override
    protected String getBundleContraintName() {
        return "cms_org_con_map_org_id_f_vdrnx";
    }

}
