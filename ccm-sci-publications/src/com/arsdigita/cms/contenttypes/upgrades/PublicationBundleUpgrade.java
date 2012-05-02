package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.PublicationBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationBundleUpgrade extends AbstractBundleUpgrade {
    
    public PublicationBundleUpgrade() {
        super("PublicationBundleUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
        new PublicationBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_publication_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_publications";
    }

    @Override
    protected String getIdColName() {
        return "publication_id";
    }

    @Override
    protected String getBundleClassName() {
        return PublicationBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_publica_bund_bun_id_p_ivy3p";
    }

    @Override
    protected String getBundleContraintName() {
        return "cms_org_pub_map_pub_id_f_6udi3";
    }
    
    
    
}
