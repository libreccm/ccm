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

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.domain.DataObjectNotFoundException;
import org.apache.log4j.Logger;

/**
 * Form for adding members to a SciDepartment. The form extends 
 * {@link GenericOrganizationalUnitPersonAddForm} and overwrites only the methods provided by
 * the superclass for customising the form. The logic of the form is completely provided by the 
 * super class.
 * 
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentMemberAddForm extends GenericOrganizationalUnitPersonAddForm {

    private final Logger logger = Logger.getLogger(
            SciDepartmentMemberAddForm.class);

    public SciDepartmentMemberAddForm(
            final ItemSelectionModel itemModel,
            final GenericOrganizationalUnitPersonSelector personSelector) {
        super(itemModel, personSelector);
    }

    @Override
    protected String getPersonType() {
        String personType = SciDepartment.getConfig().getPermittedPersonType();

        try {
            ContentType.findByAssociatedObjectType(personType);
        } catch (DataObjectNotFoundException ex) {
            logger.error(String.format("No content type for object type '%s'. "
                                       + "Falling back to '%s'.",
                                       personType,
                                       GenericPerson.class.getName()),
                         ex);
            personType = GenericPerson.class.getName();
        }

        return personType;
    }

    @Override
    protected String getRoleAttributeName() {
        return SciDepartment.ROLE_ENUM_NAME;
    }
}
