/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * CMS authoring step for the Camden Decision Tree content type.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreePropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME	= "SHEET_PROPERTIES";
    public final static String CANCEL_URL	= "cancelURL"; 

    public DecisionTreePropertiesStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {

        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);

        BasicPageForm editSheet;

        editSheet = new DecisionTreePropertiesForm( itemModel, this );
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent(getPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the DecisionTree
     * specified by the ItemSelectionModel passed in.
     * 
     * @param itemModel   The ItemSelectionModel to use
     * @pre itemModel     != null
     * @return            A component to display the state of the basic
     *                    properties  of the release
     */
    public static Component getPropertySheet( ItemSelectionModel itemModel ) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), 
                  ContentPage.TITLE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"), 
                  ContentItem.NAME);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.description"), 
                  ContentPage.DESCRIPTION);
        sheet.add(DecisionTreeGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.decisiontree.properties.cancel_url"), 
                  CANCEL_URL);

        return sheet;
    }
}
