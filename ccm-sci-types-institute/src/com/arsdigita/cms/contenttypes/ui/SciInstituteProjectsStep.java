package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Authoring step for adding projects to an institute. This step is attached by the initializer if
 * the ccm-sci-types-project module has been installed.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciInstituteProjectsStep extends SimpleEditStep {

    private String ADD_INSTITUTE_SHEET_NAME = "SciInstituteAddProject";
    public final static String ASSOC_TYPE = "ProjectOf";

    public SciInstituteProjectsStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciInstituteProjectsStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent,
                                    final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addProjectSheet
                            = new GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer() {

                    public GlobalizedMessage getSelectSubordinateOrgaUnitLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.select");
                    }

                    public String getSubordinateOrgaUnitType() {
                        return "com.arsdigita.cms.contenttypes.SciProject";
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.no_suitable_language_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.already_added");
                    }

                });
        add(ADD_INSTITUTE_SHEET_NAME,
            SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.projects.add"),
            new WorkflowLockedComponentAccess(addProjectSheet, itemModel),
            addProjectSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSubordinateOrgaUnitsTable projectsTable
                                                                 = new GenericOrganizationalUnitSubordinateOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.columns.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return SciInstituteGlobalizationUtil.globalize(
                            "sciinstitute.ui.projects.delete.confirm");
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getContentType() {
                        return "com.arsdigita.cms.contenttypes.SciProject";
                    }

                });

        setDisplayComponent(projectsTable);
    }

}
