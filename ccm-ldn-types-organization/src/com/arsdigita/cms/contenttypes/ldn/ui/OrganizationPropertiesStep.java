/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ldn.ui;

import com.arsdigita.cms.contenttypes.ldn.Organization;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ldn.util.OrganizationGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;


/**
 * Authoring step to edit the simple attributes of the Organization content 
 * type (and its subclasses). 
 *
 * @version $Id: OrganizationPropertiesStep.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class OrganizationPropertiesStep
    extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    public OrganizationPropertiesStep( ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent ) {
        super( itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new OrganizationPropertyForm( itemModel, this);
        add( EDIT_SHEET_NAME, 
             GlobalizationUtil.globalize("cms.ui.edit"), 
             new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getOrganizationPropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the 
     * Organization specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getOrganizationPropertySheet( ItemSelectionModel
                                                          itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet( itemModel );

        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.title"), 
                  Organization.TITLE);
        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.name"), 
                  Organization.NAME);

        sheet.add(OrganizationGlobalizationUtil
                  .globalize("cms.contenttypes.ui.organization.link"), 
                  Organization.LINK);
        sheet.add(OrganizationGlobalizationUtil
                  .globalize("cms.contenttypes.ui.organization.contact"), 
                  Organization.CONTACT);

	return sheet;
    }
}
