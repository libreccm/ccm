/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciOrganization;

/**
 * Form for linking members with a {@link SciOrganization}. The form overwrites
 * the {@link GenericOrganizationalUnitPersonAddForm#getPersonType()} to limit
 * the selectable person type to {@link SciMember}.
 *
 * @author Jens Pelzetter
 * @see SciOrganization
 * @see SciMember
 * @see GenericPerson
 * @see GenericOrganizationalUnitPersonAddForm
 */
public class SciOrganizationMemberAddForm 
        extends GenericOrganizationalUnitPersonAddForm {

    public SciOrganizationMemberAddForm(ItemSelectionModel itemModel) {
        super(itemModel);
    }

    @Override
    protected String getPersonType() {
        return SciMember.class.getName();
    }

    @Override
    protected String getRoleAttributeName() {
        return "SciOrganizationRole";
    }
}
