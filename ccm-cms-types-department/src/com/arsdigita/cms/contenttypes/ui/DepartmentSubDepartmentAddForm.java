package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Department;

/**
 *
 * @author Jens Pelzetter
 */
public class DepartmentSubDepartmentAddForm
        extends GenericOrganizationalUnitChildAddForm {

    public DepartmentSubDepartmentAddForm(ItemSelectionModel itemModel) {
        super("SubDepartmentAddForm", itemModel);
    }

    @Override
    protected String getChildDataObjectType() {
        return Department.BASE_DATA_OBJECT_TYPE;
    }
}
