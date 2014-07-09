package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Step for associating a project with institutes. Activated if the ccm-sci-types-project module is
 * installed and the
 * {@code com.arsdigita.cms.contenttypes.sciinstitute.enable.project_institutes_step} is set to
 * true.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciProjectInstitutesStep extends SimpleEditStep {

    private final static String ADD_INSTITUTE_SHEET_NAME = "SciProjectAddInstitute";
    public final static String ASSOC_TYPE = "ProjectOf";

    public SciProjectInstitutesStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectInstitutesStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent,
                                    final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addInstitutesSheet
                                = new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public GlobalizedMessage getSelectSuperiorOrgaUnitLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.institute.select");
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciInstitute.BASE_DATA_OBJECT_TYPE;
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.institute.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.institute.no_suitable_languge_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.institute.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.institute.already_added");
                    }

                });

        add(ADD_INSTITUTE_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "sciproject.ui.institut.add"),
            new WorkflowLockedComponentAccess(addInstitutesSheet, itemModel),
            addInstitutesSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable institutesTable
                                                                  = new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.columns.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciproject.ui.instituts.delete.confirm");
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
