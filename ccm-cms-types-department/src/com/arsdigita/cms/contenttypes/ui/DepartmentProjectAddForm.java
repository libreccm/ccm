/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Project;

/**
 *
 * @author Jens Pelzetter
 */
public class DepartmentProjectAddForm 
        extends GenericOrganizationalUnitChildAddForm {

    public DepartmentProjectAddForm(ItemSelectionModel itemModel) {
        super("ProjectAddForm", itemModel);
    }

    @Override
    protected String getChildDataObjectType() {
        return Project.BASE_DATA_OBJECT_TYPE;
    }
}
