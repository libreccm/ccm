package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Authoring step for adding departments to an institute. This step is attached by the initializer
 * if the ccm-sci-types-department module has been installed.
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

        final BasicItemForm addDepartmentSheet
                            = new GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer() {

                    public GlobalizedMessage getSelectSubordinateOrgaUnitLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.select");
                    }

                    public String getSubordinateOrgaUnitType() {
                        return "com.arsdigita.cms.contenttypes.SciDepartment";
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.no_suitable_language_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.already_added");
                    }

                });

        add(ADD_DEPARTMENT_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.departments.add"),
            new WorkflowLockedComponentAccess(addDepartmentSheet, itemModel),
            addDepartmentSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSubordinateOrgaUnitsTable departmentsTable
                                                                 = new GenericOrganizationalUnitSubordinateOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.columns.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.departments.delete.confirm");
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
