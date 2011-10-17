package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectMemberTable
        extends GenericOrganizationalUnitPersonsTable {
    
    public SciProjectMemberTable(final ItemSelectionModel itemModel,
                                 final GenericOrganizationalUnitPersonSelector personSelector) {
        super(itemModel, personSelector);
    }
    
    @Override
    public String getRoleAttributeName() {
        return SciProject.ROLE_ENUM_NAME;
    }
}
