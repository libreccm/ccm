/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui.authoring;

import com.arsdigita.cms.contenttypes.FileStorageItem;
import com.arsdigita.cms.contenttypes.ui.FileStorageItemPropertyForm;
import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.util.GlobalizationUtil;
import org.apache.log4j.Logger;

/**
 * Authoring step to edit the simple attributes of the FileStorageItem content 
 * type (and its subclasses). 
 */
public class FileStorageItemPropertiesStep
    extends SimpleEditStep {

    private static final Logger s_log =
        Logger.getLogger(FileStorageItemPropertiesStep.class);

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    public FileStorageItemPropertiesStep( ItemSelectionModel itemModel,
                                          AuthoringKitWizard parent ) {
        super( itemModel, parent );

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new FileStorageItemPropertyForm( itemModel, this);
        add( EDIT_SHEET_NAME, "Edit",
             new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getFileStorageDomainObjectPropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the 
     * FileStorageItem specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getFileStorageDomainObjectPropertySheet( ItemSelectionModel
                                                             itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet( itemModel );

        sheet.add( GlobalizationUtil
                   .globalize("cms.ui.authoring.title"), FileStorageItem.TITLE );
        sheet.add(GlobalizationUtil
                  .globalize("cms.ui.authoring.name"), FileStorageItem.NAME );
        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.summary"), FileStorageItem.DESCRIPTION );
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }

        return sheet;
    }
}
