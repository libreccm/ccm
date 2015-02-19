/*
 * Copyright (C) 2005 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.camden.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.camden.cms.contenttypes.EForm;
import com.arsdigita.camden.cms.contenttypes.util.EFormGlobalizedMsg;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring step to view/edit the simple attributes of the EForm content type (and its subclasses).
 */
public class EFormPropertiesStep extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    public static final String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor.
     *
     * @param itemModel
     * @param parent
     */
    public EFormPropertiesStep(final ItemSelectionModel itemModel,
                               final AuthoringKitWizard parent) {
        super(itemModel, parent);

        final BasicPageForm editSheet = new EFormPropertyForm(itemModel);
        add(EDIT_SHEET_NAME,
            GlobalizationUtil.globalize("cms.ui.edit"),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getEFormPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the EForm content item specified by the
     * ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     *
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the release
     *
     * Method add deprecated, use add(GlobalizedMessage label, String attribute) instead (but
     * probably Camden doesn't use globalized strings).
     */
    public static Component getEFormPropertySheet(final ItemSelectionModel itemModel) {

        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(EFormGlobalizedMsg.getName(), EForm.NAME);
        sheet.add(EFormGlobalizedMsg.getTitle(), EForm.TITLE);
        sheet.add(EFormGlobalizedMsg.getLocation(), EForm.URL);
        sheet.add(EFormGlobalizedMsg.getDescription(), EForm.DESCRIPTION);

        return sheet;
    }

}
