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
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Authoring step for editing the associations between a department and its projects.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciProjectDepartmentsStep extends SimpleEditStep {

    private final static String ADD_DEPARTMENT_SHEET_NAME = "SciProjectAddDepartment";
    public final static String ASSOC_TYPE = "ProjectOf";

    public SciProjectDepartmentsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectDepartmentsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent,
                                     final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addDepartmentSheet
                            = new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public GlobalizedMessage getSelectSuperiorOrgaUnitLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.department.select");
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciDepartment.BASE_DATA_OBJECT_TYPE;
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.department.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.department.no_suitable_language_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.department.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.department.already_added");
                    }

                });

        add(ADD_DEPARTMENT_SHEET_NAME,
            SciDepartmentGlobalizationUtil.globalize(
                "sciproject.ui.department.add"),
            new WorkflowLockedComponentAccess(addDepartmentSheet, itemModel),
            addDepartmentSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable departmentsTable
                                                              = new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.columns.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return SciDepartmentGlobalizationUtil.globalize(
                            "sciproject.ui.departments.delete.confirm");
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getContentType() {
                        return "com.arsdigita.cms.contenttypes.SciDepartmentBundle";
                    }

                });

        setDisplayComponent(departmentsTable);
    }

}
