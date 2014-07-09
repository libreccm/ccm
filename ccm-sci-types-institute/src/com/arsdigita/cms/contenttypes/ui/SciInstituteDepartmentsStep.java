package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Authoring step for adding departments to an institute. This step is
 * attached by the initializer if the ccm-sci-types-department module 
 * has been installed.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteDepartmentsStep extends SimpleEditStep {

    private String ADD_DEPARTMENT_SHEET_NAME = "SciInstituteAddDepartment";
    public final static String ASSOC_TYPE = "DepartmentOf";

    public SciInstituteDepartmentsStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciInstituteDepartmentsStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent,
                                       final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addDepartmentSheet =
                            new GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer() {

                    public String getSelectSubordinateOrgaUnitLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.select").localize();
                    }

                    public String getSubordinateOrgaUnitType() {
                        return "com.arsdigita.cms.contenttypes.SciDepartment";
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.select.nothing").
                                localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.no_suitable_language_variant").
                                localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.adding_to_itself").
                                localize();
                    }

                    public String getAlreadyAddedMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.already_added").
                                localize();
                    }
                });

        add(ADD_DEPARTMENT_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.departments.add"),
            new WorkflowLockedComponentAccess(addDepartmentSheet, itemModel),
            addDepartmentSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSubordinateOrgaUnitsTable departmentsTable =
                                                                 new GenericOrganizationalUnitSubordinateOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer() {

                    public String getEmptyViewLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.empty_view").
                                localize();
                    }

                    public String getNameColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.columns.name").
                                localize();
                    }

                    public String getDeleteColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.columns.delete").
                                localize();
                    }

                    public String getUpColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.columns.up").
                                localize();
                    }

                    public String getDownColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.columns.down").
                                localize();
                    }

                    public String getDeleteLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.delete").localize();
                    }

                    public String getUpLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.up").localize();
                    }

                    public String getDownLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.down").localize();
                    }

                    public String getConfirmRemoveLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciinstitute.ui.departments.delete.confirm").
                                localize();
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
