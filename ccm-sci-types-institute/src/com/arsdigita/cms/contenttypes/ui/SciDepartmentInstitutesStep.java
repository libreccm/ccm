package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.contenttypes.SciInstituteInitializer;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Authoring step for associating departments to an institute This step is attached by the
 * {@link SciInstituteInitializer} only if the ccm-sci-types-department module has been installed
 * and the config parameter
 * {@code com.arsdigita.cms.contenttypes.sciinstitute.enable.department_institutes_step} is set to
 * true.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentInstitutesStep extends SimpleEditStep {

    private final static String ADD_INSTITUTE_SHEET_NAME = "SciDepartmentAddInstitute";
    public static final String ASSOC_TYPE = "DepartmentOf";

    public SciDepartmentInstitutesStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentInstitutesStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent,
                                       final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addInstituteSheet
                            = new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public GlobalizedMessage getSelectSuperiorOrgaUnitLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institute.select");
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciInstitute.BASE_DATA_OBJECT_TYPE;
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institute.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institute.no_suitable_language_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institute.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institute.already_added");
                    }

                });

        add(ADD_INSTITUTE_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "scidepartment.ui.institute.add"),
            new WorkflowLockedComponentAccess(addInstituteSheet, itemModel),
            addInstituteSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable institutesTable
                                                              = new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.columns.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "scidepartment.ui.institutes.delete.confirm");
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getContentType() {
                        return "com.arsdigita.cms.contenttypes.SciInstituteBundle";
                    }

                });

        setDisplayComponent(institutesTable);
    }

}
