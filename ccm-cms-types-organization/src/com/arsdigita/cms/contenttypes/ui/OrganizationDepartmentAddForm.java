package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Department;

/**
 *
 * @author Jens Pelzetter
 */
public class OrganizationDepartmentAddForm
        extends GenericOrganizationalUnitChildAddForm {

    public OrganizationDepartmentAddForm(ItemSelectionModel itemModel) {
        super("DepartmentAddForm", itemModel);
    }

    @Override
    protected String getChildDataObjectType() {
    	return Department.BASE_DATA_OBJECT_TYPE;
    }
}