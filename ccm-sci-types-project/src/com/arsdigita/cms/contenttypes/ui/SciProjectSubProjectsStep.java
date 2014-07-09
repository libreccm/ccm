package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciProjectSubProjectsStep extends SimpleEditStep {

    private String ADD_SUBPROJECT_SHEET_NAME = "SciProjectAddSubProject";
    public final static String ASSOC_TYPE = "SubProject";

    public SciProjectSubProjectsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectSubProjectsStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent,
                                     final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addSubProjectSheet
                                = new GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer() {

                    public GlobalizedMessage getSelectSubordinateOrgaUnitLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.select");
                    }

                    public String getSubordinateOrgaUnitType() {
                        return SciProject.class.getName();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.no_suitable_language_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.already_added");
                    }

                });
        add(ADD_SUBPROJECT_SHEET_NAME,
            SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.subproject.add"),
            new WorkflowLockedComponentAccess(addSubProjectSheet, itemModel),
            addSubProjectSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSubordinateOrgaUnitsTable subProjectsTable
                                                                     = new GenericOrganizationalUnitSubordinateOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subprojects.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subprojects.columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subprojects.colums.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subprojects.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subprojects.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.order.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.order.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.subproject.remove.confirm");
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getContentType() {
                        return "com.arsdigita.cms.contenttypes.SciProjectBundle";
                    }

                });

        setDisplayComponent(subProjectsTable);

    }

}
