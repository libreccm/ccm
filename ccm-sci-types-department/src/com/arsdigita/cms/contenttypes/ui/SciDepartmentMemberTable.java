/*
 * Copyright (c) 2013 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;

/**
 * Table displaying the members of an SciDepartment. Based on the 
 * {@link GenericOrganizationalUnitPersonsTable} it overwrites only the methods 
 * provided by the superclass for customising the form.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentMemberTable extends GenericOrganizationalUnitPersonsTable {
    
    public SciDepartmentMemberTable(final ItemSelectionModel itemModel,
                                    final GenericOrganizationalUnitPersonSelector personSelector) {
        super(itemModel, personSelector);
    }
    
    @Override
    public String getRoleAttributeName() {
        return SciDepartment.ROLE_ENUM_NAME;
    }
    
}
