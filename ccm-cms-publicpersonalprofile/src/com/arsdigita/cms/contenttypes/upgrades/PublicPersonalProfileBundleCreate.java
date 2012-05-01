package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.PublicPersonalProfileBundle;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileBundleCreate extends AbstractBundleUpgrade {

    public PublicPersonalProfileBundleCreate() {
        super("PublicPersonalProfileBundleCreate", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new PublicPersonalProfileBundleCreate().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_public_personal_profile_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_public_personal_profiles";
    }

    @Override
    protected String getIdColName() {
        return "profile_id";
    }

    @Override
    protected String getBundleClassName() {
        return PublicPersonalProfileBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pub_per_pro_bun_bun_p_zhc9i";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_pub_per_pro_bun_bun_f__jr2_";
    }

}
