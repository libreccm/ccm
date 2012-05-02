package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.PublisherBundle;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublisherBundleUpgrade extends AbstractBundleUpgrade {
    
    public PublisherBundleUpgrade() {
        super("PublisherBundleUpgrade", "1.0.0", "");
    }
    
    public static void main(final String[] args) {
         new PublicationBundleUpgrade().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "ct_publisher_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "ct_publisher";
    }

    @Override
    protected String getIdColName() {
        return "publisher_id";
    }

    @Override
    protected String getBundleClassName() {
        return PublisherBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "ct_publis_bund_bund_id_p_tu1nf";
    }

    @Override
    protected String getBundleContraintName() {
        return "ct_publis_bund_bund_id_f_321fl";
    }
    
    @Override
    protected String getSuperBundleTable() {
        return "cms_orgaunit_bundles";
    }
    
}
