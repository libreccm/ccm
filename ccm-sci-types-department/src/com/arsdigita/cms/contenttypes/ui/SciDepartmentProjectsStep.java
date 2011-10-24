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

                    public String getSelectSubordinateOrgaUnitLabel() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.select").localize();
                    }

                    public String getSubordinateOrgaUnitType() {
                        return "com.arsdigita.cms.contenttypes.SciProject";
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.select.nothing").
                                localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.no_suitable_language_variant").
                                localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.adding_to_itself").
                                localize();
                    }

                    public String getAlreadyAddedMessage() {
                        return (String) SciDepartmentGlobalizationUtil.globalize(
                                "scidepartment.ui.projects.already_added").
                                localize();
                    }
                });
        add(ADD_PROJECT_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.projects.add").localize(),
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
                });

        setDisplayComponent(projectsTable);
    }
}
