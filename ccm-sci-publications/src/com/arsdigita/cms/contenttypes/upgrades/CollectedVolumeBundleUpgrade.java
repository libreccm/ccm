package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class CollectedVolumeBundleUpgrade extends AbstractBundleUpgrade {
    
    public CollectedVolumeBundleUpgrade() {
        super("CollectedVolumeBundleUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
        new CollectedVolumeBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_collected_volume_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_collected_volume";
    }

    @Override
    protected String getIdColName() {
        return "collected_volume_id";
    }

    @Override
    protected String getBundleClassName() {
        return CollectedVolumeBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_coll_vol_bun_bun_id_p_2epsd";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_coll_vol_bun_bun_id_f__jijf";
    }
    
    @Override
    public String getSuperBundleTable() {
        return "ct_publication_with_publisher_bundles";
    }
    
}
