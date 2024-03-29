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
import com.arsdigita.cms.contenttypes.Service;
import com.arsdigita.cms.contenttypes.util.ServiceGlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Authoring step to edit the simple attributes of the Service content type (and
 * its subclasses). The attributes edited are 'name', 'title', 'summary',
 * 'services provided', 'opening times', 'address', and 'contacts'.  This
 * authoring step replaces the <code>com.arsdigita.ui.authoring.PageEdit</code>
 * step for this type.
 **/
public class ServicePropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public ServicePropertiesStep(ItemSelectionModel itemModel,
                                 AuthoringKitWizard parent) {

        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new ServicePropertyForm(itemModel,this);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getServicePropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the Service specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getServicePropertySheet(ItemSelectionModel
                                                    itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.title"),  
                   Service.TITLE);
        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.name"),  
                   Service.NAME);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }
        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.summary"),  
                   Service.SUMMARY);
        sheet.add( ServiceGlobalizationUtil
                   .globalize("cms.contenttypes.ui.service.services_provided"),  
                   Service.SERVICES_PROVIDED);
        sheet.add( ServiceGlobalizationUtil
                   .globalize("cms.contenttypes.ui.service.opening_times"),  
                   Service.OPENING_TIMES);
        sheet.add( ServiceGlobalizationUtil
                   .globalize("cms.contenttypes.ui.service.address"),  
                   Service.ADDRESS);
        sheet.add( ServiceGlobalizationUtil
                   .globalize("cms.contenttypes.ui.service.contacts"),  
                   Service.CONTACTS);

        return sheet;
    }
}
