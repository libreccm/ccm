/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * @author Sören Bernstein
 */
public class GenericContactTypePropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    private static String ADD_CONTACT_SHEET_NAME = "addContactType";

    public GenericContactTypePropertiesStep(ItemSelectionModel itemModel, 
                                            AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericContactTypePropertiesStep(ItemSelectionModel itemModel, 
                                            AuthoringKitWizard parent, 
                                            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addContactTypeSheet = new GenericContactTypeAddForm(itemModel);
        add(ADD_CONTACT_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.add_contacttype").localize(),
            new WorkflowLockedComponentAccess(addContactTypeSheet, itemModel),
            addContactTypeSheet.getSaveCancelSection().getCancelButton());

        GenericContactTypeTable contacttypesTable = new 
                                GenericContactTypeTable(itemModel);
        setDisplayComponent(contacttypesTable);

    }
}
