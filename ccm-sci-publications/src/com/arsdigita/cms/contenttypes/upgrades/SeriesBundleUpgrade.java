package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.SeriesBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SeriesBundleUpgrade extends AbstractBundleUpgrade {

    public SeriesBundleUpgrade() {
        super("SeriesBundleUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new SeriesBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_series_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_series";
    }

    @Override
    protected String getIdColName() {
        return "series_id";
    }

    @Override
    protected String getBundleClassName() {
        return SeriesBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_seri_bundl_bundl_id_p_tuvfn";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_seri_bundl_bundl_id_f_8wj0h";
    }
}
