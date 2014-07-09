/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.cms.contenttypes.SciAuthor;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Step for editing a {@link SciAuthor}
 *
 * @author Jens Pelzetter
 */
public class SciAuthorPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public SciAuthorPropertiesStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent) {
        super(itemModel, parent);

        BasicPageForm editSheet;
        editSheet = new SciAuthorPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME,
            PublicationGlobalizationUtil.globalize(
                "sciauthor.ui.edit_basic_properties"),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getSciAuthorPropertySheet(itemModel));
    }

    public static Component getSciAuthorPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet;

        sheet = (DomainObjectPropertySheet) GenericPersonPropertiesStep.
                getGenericPersonPropertySheet(itemModel);

        return sheet;
    }
}
