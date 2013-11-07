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

/**
 * Step for editing the association the the superior organisational unit.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentSuperDepartmentsStep extends SimpleEditStep {

    private final static String ADD_SUPER_DEPARTMENT_SHEET_NAME =
                                "SciDepartmentAddSuperDepartment";
    public final static String ASSOC_TYPE = "DepartmentOf";

    public SciDepartmentSuperDepartmentsStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentSuperDepartmentsStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent,
                                             final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addSuperDepartmentSheet =
                            new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public String getSelectSuperiorOrgaUnitLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartment.select").
                                localize();
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciDepartment.BASE_DATA_OBJECT_TYPE;
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartment.select.nothing").
                                localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartment.no_suitable_language_variant").
                                localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartment.adding_to_itself").
                                localize();
                    }

                    public String getAlreadyAddedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartment.already_added").
                                localize();
                    }
                });
        add(ADD_SUPER_DEPARTMENT_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.superdepartment.add").localize(),
            new WorkflowLockedComponentAccess(addSuperDepartmentSheet, itemModel),
            addSuperDepartmentSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable superDeparmentsTable =
                                                              new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public String getEmptyViewLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.empty_view").
                                localize();
                    }

                    public String getNameColumnLabel() {
                      return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.columns.name").
                                localize();
                    }

                    public String getDeleteColumnLabel() {
                       return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.columns.delete").
                                localize();
                    }

                    public String getUpColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.columns.up").
                                localize();
                    }

                    public String getDownColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.columns.down").
                                localize();
                    }

                    public String getDeleteLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.delete").
                                localize();
                    }

                    public String getUpLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.up").
                                localize();
                    }

                    public String getDownLabel() {
                       return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.down").
                                localize();
                    }

                    public String getConfirmRemoveLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.superdepartments.delete.confirm").
                                localize();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }
                    
                    public String getContentType() {
                        return "com.arsdigita.cms.contenttypes.SciDepartmentBundle";
                    }
                });
        
        setDisplayComponent(superDeparmentsTable);
    }
}
