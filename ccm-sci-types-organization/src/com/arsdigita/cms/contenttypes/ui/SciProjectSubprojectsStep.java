/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import org.apache.log4j.Logger;

/**
 * Step for adding and removing subprojects to and from a {@link SciProject}.
 *
 * @author Jens Pelzetter
 */
public class SciProjectSubprojectsStep extends SimpleEditStep {

    private final static Logger s_log = Logger.getLogger(
            SciProjectSubprojectsStep.class);
    private String ADD_CHILD_SHEET_NAME = "addChild";

    public SciProjectSubprojectsStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectSubprojectsStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addSubProjectSheet =
                      new SciProjectSubprojectAddForm(itemModel);
        add(ADD_CHILD_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.addSubProject").localize(),
            new WorkflowLockedComponentAccess(addSubProjectSheet, itemModel),
            addSubProjectSheet.getSaveCancelSection().getCancelButton());

        SciProjectSubProjectsTable subProjectsTable = new SciProjectSubProjectsTable(
                itemModel);
        setDisplayComponent(subProjectsTable);
    }
}
