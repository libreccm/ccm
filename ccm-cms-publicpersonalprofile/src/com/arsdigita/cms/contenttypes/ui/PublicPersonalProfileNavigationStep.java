/*
 * Copyright (c) 2011 Jens Pelzetter
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
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavigationStep extends SimpleEditStep {

    public static final String EDIT_NAV_ITEM_SHEET_NAME = "editNavItem";
    public static final String EDIT_NAV_GENERATED_ITEM_SHEET_NAME =
                               "editGeneratedNavItem";

    public PublicPersonalProfileNavigationStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicPersonalProfileNavigationStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm editNavItemSheet =
                      new PublicPersonalProfileNavigationAddForm(itemModel,
                                                                 this);
        add(EDIT_NAV_ITEM_SHEET_NAME,
            PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.profile.content.add"),
            new WorkflowLockedComponentAccess(editNavItemSheet, itemModel),
            editNavItemSheet.getSaveCancelSection().getCancelButton());

        /*BasicItemForm editGeneratedNavItemSheet =
                      new PublicPersonalProfileNavigationGeneratedAddForm(
                itemModel,
                this);
        add(EDIT_NAV_GENERATED_ITEM_SHEET_NAME,
            (String) PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.profile.generated_content.add").
                localize(),
            new WorkflowLockedComponentAccess(editGeneratedNavItemSheet,
                                              itemModel),
            editGeneratedNavItemSheet.getSaveCancelSection().getCancelButton());*/

        PublicPersonalProfileNavigationTable navTable =
                                             new PublicPersonalProfileNavigationTable(
                itemModel, this);
        setDisplayComponent(navTable);
    }
}
