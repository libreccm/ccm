package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SeriesEditorsAssocUpgrade extends AbstractAssocUpgrade {

    public SeriesEditorsAssocUpgrade() {
        super("SeriesEditorsAssocUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new SeriesEditorsAssocUpgrade().run(args);
    }

    @Override
    protected String getTableName() {
        return "ct_series_editship";
    }

    @Override
    protected String getOwnerIdCol() {
        return "series_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "person_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("date_from", "DATE");
        attributes.put("date_from_skip_month", "boolean");
        attributes.put("date_from_skip_day", "boolean");
        attributes.put("date_to", "DATE");
        attributes.put("date_to_skip_month", "boolean");
        attributes.put("date_to_skip_day", "boolean");
        attributes.put("editship_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_ser_edi_per_id_ser__p_i1gdy";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_seri_editsh_seri_id_f_6vmir";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_seri_editsh_pers_id_f_eje11";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_series_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_person_bundles";
    }

}
