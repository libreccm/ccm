package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectSubprojectAddForm
        extends GenericOrganizationalUnitChildAddForm {

    public SciProjectSubprojectAddForm(ItemSelectionModel itemModel) {
        super("SubprojectAddForm", itemModel);
    }

    @Override
    protected String getChildDataObjectType() {
        return SciProject.BASE_DATA_OBJECT_TYPE;
    }
}
