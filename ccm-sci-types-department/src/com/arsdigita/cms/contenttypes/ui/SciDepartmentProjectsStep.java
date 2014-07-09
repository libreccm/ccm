/*
 * Copyright (c) 2013 Jens Pelzetter
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
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Authoring step for editing the associations between a SciDepartment and SciProject items.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentProjectsStep extends SimpleEditStep {

    private String ADD_PROJECT_SHEET_NAME = "SciDepartmentAddProject";
    public final static String ASSOC_TYPE = "ProjectOf";

    public SciDepartmentProjectsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentProjectsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent,
                                     final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addProjectSheet =
                new GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
                    itemModel,
                    new GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer() {

                    @Override
                    public GlobalizedMessage getSelectSubordinateOrgaUnitLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.select");
                    }

                    @Override
                    public String getSubordinateOrgaUnitType() {
                        return "com.arsdigita.cms.contenttypes.SciProject";
                    }

                    @Override
                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    @Override
                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.select.nothing");
                    }

                    @Override
                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.no_suitable_language_variant");
                    }

                    @Override
                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.adding_to_itself");
                    }

                    @Override
                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.already_added");
                    }
                });
        add(ADD_PROJECT_SHEET_NAME,
            SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.projects.add"),
            new WorkflowLockedComponentAccess(addProjectSheet, itemModel),
            addProjectSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSubordinateOrgaUnitsTable projectsTable =
                      new GenericOrganizationalUnitSubordinateOrgaUnitsTable(
                          itemModel,
                          new GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return  SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.delete.confirm");
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }
                    
                    public String getContentType() {
                        return "com.arsdigita.cms.contenttypes.SciProjectBundle";
                    }
                });

        setDisplayComponent(projectsTable);
    }
}
