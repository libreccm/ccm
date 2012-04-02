package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectDepartmentsStep extends SimpleEditStep {

    private final static String ADD_DEPARTMENT_SHEET_NAME =
                                "SciProjectAddDepartment";
    public final static String ASSOC_TYPE = "ProjectOf";

    public SciProjectDepartmentsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectDepartmentsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent,
                                     final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addDepartmentSheet =
                            new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public String getSelectSuperiorOrgaUnitLabel() {
                       return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.department.select").localize();
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciDepartment.BASE_DATA_OBJECT_TYPE;
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.department.select.nothing").localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                         return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.department.no_suitable_language_variant").localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.department.adding_to_itself").localize();
                    }

                    public String getAlreadyAddedMessage() {
                         return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.department.already_added").localize();
                    }
                });

        add(ADD_DEPARTMENT_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "sciproject.ui.department.add").localize(),
            new WorkflowLockedComponentAccess(addDepartmentSheet, itemModel),
            addDepartmentSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable departmentsTable =
                                                              new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public String getEmptyViewLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.empty_view").localize();
                    }

                    public String getNameColumnLabel() {
                       return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.columns.name").localize();
                    }

                    public String getDeleteColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.columns.delete").localize();
                    }

                    public String getUpColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.columns.up").localize();
                    }

                    public String getDownColumnLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.columns.down").localize();
                    }

                    public String getDeleteLabel() {
                      return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.delete").localize();
                    }

                    public String getUpLabel() {
                       return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.up").localize();
                    }

                    public String getDownLabel() {
                      return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.down").localize();
                    }

                    public String getConfirmRemoveLabel() {
                       return (String) SciDepartmentGlobalizationUtil.globalize(
                                "sciproject.ui.departments.delete.confirm").localize();
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
