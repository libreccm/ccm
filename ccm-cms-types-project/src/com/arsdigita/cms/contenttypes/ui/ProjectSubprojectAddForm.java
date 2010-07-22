package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Project;

/**
 *
 * @author Jens Pelzetter
 */
public class ProjectSubprojectAddForm
        extends GenericOrganizationalUnitChildAddForm {

    public ProjectSubprojectAddForm(ItemSelectionModel itemModel) {
        super("SubprojectAddForm", itemModel);
    }

    @Override
    protected String getChildDataObjectType() {
        return Project.BASE_DATA_OBJECT_TYPE;
    }
}
