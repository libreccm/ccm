/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.london.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.london.contenttypes.ESDService;
import com.arsdigita.london.contenttypes.util.ESDServiceGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Authoring kit step to edit the basic properties of
 * <code>ESDService</code> content type object.
 *
 * @author Shashin Shinde <a
 * href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ESDServicePropertiesStep.java 287 2005-02-22 00:29:02Z sskracic
 * $
 *
 */
public class ESDServicePropertiesStep extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    private static String EDIT_SHEET_NAME = "edit";

    /**
     * @param itemModel
     * @param parent
     */
    public ESDServicePropertiesStep(ItemSelectionModel itemModel,
                                    AuthoringKitWizard parent) {
        super(itemModel, parent);

        ESDServicePropertiesForm editSheet;

        editSheet = new ESDServicePropertiesForm(itemModel);
        add(EDIT_SHEET_NAME,
            GlobalizationUtil.globalize("cms.ui.edit"),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getESDServicePropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the ESDService
     * specified by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the
   *
     */
    public static Component getESDServicePropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), 
                  ESDService.TITLE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"),
                  ESDService.NAME);

        sheet.add(ESDServiceGlobalizationUtil.globalize(
                  "london.contenttypes.ui.esdservice.servicetimes"),
                  ESDService.SERVICE_TIMES);

        return sheet;

    }
}
