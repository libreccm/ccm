package com.arsdigita.cms.contenttypes.upgrades;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationAuthorsAssocUpgrade extends AbstractAssocUpgrade {
  
    @Override
    protected String getTableName() {
        return "ct_publications_authorship";
    }

    @Override
    protected String getOwnerIdCol() {
        return "publication_id";
    }

    @Override
    protected String getMemberIdCol() {
        return "author_id";
    }

    @Override
    protected Map<String, String> getAttributes() {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("editor", "BIT");
        attributes.put("authorship_order", "integer");
        return attributes;
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pub_aut_per_id_pub__p_adskp";
    }

    @Override
    protected String getOwnerConstraintName() {
        return "ct_publi_auth_publi_id_f_6aw9g";
    }

    @Override
    protected String getMemberConstraintName() {
        return "ct_public_autho_per_id_f_ot1p6";
    }

    @Override
    protected String getOwnerTableName() {
        return "ct_publication_bundles";
    }

    @Override
    protected String getMemberTableName() {
        return "cms_person_bundles";
    }

}
