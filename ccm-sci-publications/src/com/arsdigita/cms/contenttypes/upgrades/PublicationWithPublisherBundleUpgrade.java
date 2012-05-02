package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.PublicationWithPublisherBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationWithPublisherBundleUpgrade extends AbstractBundleUpgrade {

    public PublicationWithPublisherBundleUpgrade() {
        super("PublicationWithPublisherBundleUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new PublicationWithPublisherBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_publication_with_publisher_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_publication_with_publisher";
    }

    @Override
    protected String getIdColName() {
        return "publication_with_publisher_id";
    }

    @Override
    protected String getBundleClassName() {
        return PublicationWithPublisherBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_pub_wit_pub_bun_bun_p_qj70a";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_proceed_bund_bun_id_f_loele";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "ct_publication_bundles";
    }
}
