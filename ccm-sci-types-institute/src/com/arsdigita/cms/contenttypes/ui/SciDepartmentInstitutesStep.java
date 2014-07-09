package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.contenttypes.SciInstituteInitializer;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Authoring step for associating departments to an institute
 * This step is attached by the {@link SciInstituteInitializer} only if
 * the ccm-sci-types-department module has been installed and the config 
 * parameter {@code com.arsdigita.cms.contenttypes.sciinstitute.enable.department_institutes_step} 
 * is set to true.
 *  
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentInstitutesStep extends SimpleEditStep {

    private final static String ADD_INSTITUTE_SHEET_NAME =
                                "SciDepartmentAddInstitute";
    public static final String ASSOC_TYPE = "DepartmentOf";

    public SciDepartmentInstitutesStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentInstitutesStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent,
                                       final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addInstituteSheet =
                            new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public String getSelectSuperiorOrgaUnitLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institute.select").localize();
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciInstitute.BASE_DATA_OBJECT_TYPE;
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institute.select.nothing").
                                localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institute.no_suitable_language_variant").
                                localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institute.adding_to_itself").
                                localize();
                    }

                    public String getAlreadyAddedMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institute.already_added").
                                localize();
                    }
                });

        add(ADD_INSTITUTE_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "scidepartment.ui.institute.add"),
            new WorkflowLockedComponentAccess(addInstituteSheet, itemModel),
            addInstituteSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable institutesTable =
                                                              new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public String getEmptyViewLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.empty_view").
                                localize();
                    }

                    public String getNameColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.columns.name").
                                localize();
                    }

                    public String getDeleteColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.columns.delete").
                                localize();
                    }

                    public String getUpColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.columns.up").
                                localize();
                    }

                    public String getDownColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.columns.down").
                                localize();
                    }

                    public String getDeleteLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.delete").localize();
                    }

                    public String getUpLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.up").localize();
                    }

                    public String getDownLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.down").localize();
                    }

                    public String getConfirmRemoveLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "scidepartment.ui.institutes.delete.confirm").
                                localize();
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
