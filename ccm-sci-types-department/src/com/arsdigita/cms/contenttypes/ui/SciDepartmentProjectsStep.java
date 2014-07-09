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
                    public String getSelectSubordinateOrgaUnitLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.select").localize();
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
                    public String getNothingSelectedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.select.nothing").
                                localize();
                    }

                    @Override
                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.no_suitable_language_variant").
                                localize();
                    }

                    @Override
                    public String getAddingToItselfMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.adding_to_itself").
                                localize();
                    }

                    @Override
                    public String getAlreadyAddedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.already_added").
                                localize();
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

                    public String getEmptyViewLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.empty_view").localize();
                    }

                    public String getNameColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.name").
                                localize();
                    }

                    public String getDeleteColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.delete").
                                localize();
                    }

                    public String getUpColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.up").localize();
                    }

                    public String getDownColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.columns.down").
                                localize();
                    }

                    public String getDeleteLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.delete").localize();
                    }

                    public String getUpLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.up").localize();
                    }

                    public String getDownLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.down").localize();
                    }

                    public String getConfirmRemoveLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.delete.confirm").
                                localize();
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
