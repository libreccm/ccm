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
import com.arsdigita.cms.contenttypes.GlossaryItem;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.GlossaryGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Authoring step to edit the simple attributes of the GlossaryItem content type (and its subclasses).
 */
public class GlossaryItemPropertiesStep
        extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    public static final String EDIT_SHEET_NAME = "edit";

    public GlossaryItemPropertiesStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new GlossaryItemPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME,
            GlobalizationUtil.globalize("cms.ui.edit"),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getGlossaryDomainObjectPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the GlossaryItem specified by the ItemSelectionModel passed
     * in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the release
     */
    public static Component getGlossaryDomainObjectPropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlossaryGlobalizationUtil
                .globalize("cms.contenttypes.ui.glossary.term"),
                  GlossaryItem.TITLE);
        sheet.add(GlossaryGlobalizationUtil
                .globalize("cms.contenttypes.ui.name"),
                  GlossaryItem.NAME);
        sheet.add(GlossaryGlobalizationUtil
                .globalize("cms.contenttypes.ui.glossary.definition"),
                  GlossaryItem.DEFINITION);

        return sheet;
    }
}
