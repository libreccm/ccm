package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteMembersTable
        extends GenericOrganizationalUnitPersonsTable {
    
    public SciInstituteMembersTable(final ItemSelectionModel itemModel,
                                    final GenericOrganizationalUnitPersonSelector personSelector) {
        super(itemModel, personSelector);
    }
    
    @Override
    public String getRoleAttributeName() {
        return SciInstitute.ROLE_ENUM_NAME;
    }
        
}
