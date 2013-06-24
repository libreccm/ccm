/*
 * Copyright (C) 2010 Jens Pelzetter, 
 * for the Center of Social Policy Resarch of the University of Bremen
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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;

public class PersonPropertiesStep extends GenericPersonPropertiesStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public PersonPropertiesStep(ItemSelectionModel itemModel,
                                AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    protected void createEditSheet(ItemSelectionModel itemModel) {
        BasicPageForm editSheet;
        editSheet = new PersonPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(editSheet, itemModel), editSheet.
                getSaveCancelSection().getCancelButton());
    }

    public static Component getPersonPropertySheet(ItemSelectionModel itemModel) {
        Component sheet = GenericPersonPropertiesStep.getGenericPersonPropertySheet(
                itemModel);

        return sheet;
    }
}
