package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.GenericPersonBundle;

/**
 * Creates new table {@code cms_person_bundles}. Part of upgrade from 6.6.4 to 6.6.5
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class CreatePersonBundles extends AbstractBundleUpgrade {

    public CreatePersonBundles() {
        super("CreatePersonBundles", "1.0.0", "");
    }
    
    public static void main(final String args[]) {
        new CreatePersonBundles().run(args);
    }

    @Override
    protected String getBundleTableName() {
        return "cms_person_bundles";
    }

    @Override
    protected String getContentItemTableName() {
        return "cms_persons";
    }

    @Override
    protected String getIdColName() {
        return "person_id";
    }

    @Override
    protected String getBundleClassName() {
        return GenericPersonBundle.class.getName();
    }

    @Override
    protected String getPrimaryKeyConstraintName() {
        return "cms_pers_bundl_bund_id_p_7xuzi";
    }

    @Override
    protected String getBundleContraintName() {
        return "cms_pers_bundl_bund_id_f__rzge";
    }

}
