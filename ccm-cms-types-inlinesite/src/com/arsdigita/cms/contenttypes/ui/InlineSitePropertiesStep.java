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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.InlineSite;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;


/**
 * Authoring step to edit the simple attributes of the InlineSite content 
 * type (and its subclasses). 
 */
public class InlineSitePropertiesStep
    extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    public InlineSitePropertiesStep( ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent ) {
        super( itemModel, parent );

        BasicPageForm editSheet;

        editSheet = new InlineSitePropertyForm( itemModel );
        add( EDIT_SHEET_NAME, "Edit", 
             new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getInlineSitePropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the 
     * InlineSite specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getInlineSitePropertySheet( ItemSelectionModel
                                                          itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet( itemModel );

        sheet.add( "Name:", InlineSite.NAME );
        sheet.add( "Title:", InlineSite.TITLE );
        sheet.add( "Description:", InlineSite.DESCRIPTION);
        sheet.add( "URL:", InlineSite.URL );

        return sheet;
    }
}

