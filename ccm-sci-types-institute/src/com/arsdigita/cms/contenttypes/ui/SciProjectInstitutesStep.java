package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Step for associating a project with institutes. Activated if the
 * ccm-sci-types-project module is installed and the 
 * {@code com.arsdigita.cms.contenttypes.sciinstitute.enable.project_institutes_step} 
 * is set to true.
 * 
 * @author Jens Pelzetter  
 * @version $Id$
 */
public class SciProjectInstitutesStep extends SimpleEditStep {

    private final static String ADD_INSTITUTE_SHEET_NAME =
                                "SciProjectAddInstitute";
    public final static String ASSOC_TYPE = "ProjectOf";

    public SciProjectInstitutesStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectInstitutesStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent,
                                    final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addInstitutesSheet =
                            new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public String getSelectSuperiorOrgaUnitLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.institute.select").localize();
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciInstitute.BASE_DATA_OBJECT_TYPE;
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.institute.select.nothing").
                                localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.institute.no_suitable_languge_variant").localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.institute.adding_to_itself").
                                localize();
                    }

                    public String getAlreadyAddedMessage() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.institute.already_added").
                                localize();
                    }
                });

        add(ADD_INSTITUTE_SHEET_NAME,
            (String) SciInstituteGlobalizationUtil.globalize(
                "sciproject.ui.institut.add").localize(),
            new WorkflowLockedComponentAccess(addInstitutesSheet, itemModel),
            addInstitutesSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable institutesTable =
                                                              new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public String getEmptyViewLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.empty_view").localize();
                    }

                    public String getNameColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.columns.name").localize();
                    }

                    public String getDeleteColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.columns.delete").localize();
                    }

                    public String getUpColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.columns.up").localize();
                    }

                    public String getDownColumnLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.columns.down").localize();
                    }

                    public String getDeleteLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.delete").localize();
                    }

                    public String getUpLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.up").localize();
                    }

                    public String getDownLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.down").localize();
                    }

                    public String getConfirmRemoveLabel() {
                        return (String) SciInstituteGlobalizationUtil.globalize(
                                "sciproject.ui.instituts.delete.confirm").localize();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }
                    
                    public String getContentType() {
                        return "com.arsdigita.cms.contenttypes.SciInstitute";
                    }
                });
        setDisplayComponent(institutesTable);
    }
}
