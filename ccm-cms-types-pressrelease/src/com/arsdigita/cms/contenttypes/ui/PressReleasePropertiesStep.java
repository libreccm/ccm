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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PressRelease;
import com.arsdigita.cms.contenttypes.util.PressReleaseGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import java.text.DateFormat;

/**
 * Authoring step to edit the simple attributes of the PressRelease content type
 * (and its subclasses). The attributes edited are 'name', 'title', 'release
 * date' and 'reference code'. This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public class PressReleasePropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor.
     * 
     * @param itemModel
     * @param parent 
     */
    public PressReleasePropertiesStep( ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent ) {
        super( itemModel, parent );

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new PressReleasePropertyForm( itemModel, this);
        add( EDIT_SHEET_NAME, 
             GlobalizationUtil.globalize("cms.ui.edit"), 
             new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getPressReleasePropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the PressRelease
     * specified by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     **/
    public static Component getPressReleasePropertySheet( ItemSelectionModel
                                                          itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet( itemModel );

        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.title"),  
                  PressRelease.TITLE);
        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.name"),  
                   PressRelease.NAME);
        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.summary"), 
                  PressRelease.SUMMARY );
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }
        sheet.add(PressReleaseGlobalizationUtil
                  .globalize("cms.contenttypes.ui.pressrelease.contact_info"), 
                  PressRelease.CONTACT_INFO );
        sheet.add(PressReleaseGlobalizationUtil
                  .globalize("cms.contenttypes.ui.pressrelease.ref_code"), 
                  PressRelease.REFERENCE_CODE );

        return sheet;
    }
}
