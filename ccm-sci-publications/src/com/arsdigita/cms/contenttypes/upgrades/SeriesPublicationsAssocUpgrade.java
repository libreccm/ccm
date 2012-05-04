package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SeriesPublicationsAssocUpgrade extends AbstractAssocUpgrade {

    public SeriesPublicationsAssocUpgrade() {
        super("SeriesPublicationsAssocUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new SeriesPublicationsAssocUpgrade().run(args);
    }

    @Override
    protected String getTableName() {
        return "ct_publications_volume_in_series";
    }

    @Override
    protected String getOwnerIdCol() {
        return "series_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "publication_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("volumeOfSeries", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pub_vol_in_ser_pub__p_qsbkh";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_pub_vol_in_ser_ser__f_q3jjk";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_pub_vol_in_ser_pub__f_gmhj2";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_series_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "ct_publication_bundles";
    }

}
