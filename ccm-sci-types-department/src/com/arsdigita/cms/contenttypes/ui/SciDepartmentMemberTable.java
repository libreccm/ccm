package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentMemberTable 
extends GenericOrganizationalUnitPersonsTable {
    
    public SciDepartmentMemberTable(final ItemSelectionModel itemModel,
                                    final GenericOrganizationalUnitPersonSelector personSelector) {
        super(itemModel, personSelector);
    }
    
    @Override
    public String getRoleAttributeName() {
        return SciDepartment.ROLE_ENUM_NAME;
    }
    
}
