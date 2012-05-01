package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.GenericContactBundle;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class CreateContactBundles extends AbstractBundleUpgrade {

    public CreateContactBundles() {
        super("CreateContactBundles", "1.0.0", "");
    }

    public static void main(final String args[]) {
        new CreateContactBundles().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "cms_contact_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "cms_contacts";
    }

    @Override
    protected String getIdColName() {
        return "contact_id";
    }

    @Override
    protected String getBundleClassName() {
        return GenericContactBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_cont_bundl_bund_id_p_2p6vp";
    }

    @Override
    protected String getBundleContraintName() {
        return "cms_cont_bundl_bund_id_f_m8aga";
    }

}
