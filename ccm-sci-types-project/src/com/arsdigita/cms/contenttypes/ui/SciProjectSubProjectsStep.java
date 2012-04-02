package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

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

        final BasicItemForm addSubProjectSheet =
                            new GenericOrganizationalUnitSubordinateOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer() {

                    public String getSelectSubordinateOrgaUnitLabel() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.select").localize();
                    }

                    public String getSubordinateOrgaUnitType() {
                        return SciProject.class.getName();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.select.nothing").
                                localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.no_suitable_language_variant").
                                localize();
                    }

                    public String getAddingToItselfMessage() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.adding_to_itself").
                                localize();
                    }

                    public String getAlreadyAddedMessage() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.already_added").
                                localize();
                    }
                });
        add(ADD_SUBPROJECT_SHEET_NAME,
            (String) SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.subproject.add").localize(),
            new WorkflowLockedComponentAccess(addSubProjectSheet, itemModel),
            addSubProjectSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSubordinateOrgaUnitsTable subProjectsTable =
                                                                 new GenericOrganizationalUnitSubordinateOrgaUnitsTable(
                itemModel,
                new GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer() {

                    public String getEmptyViewLabel() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subprojects.empty_view").localize();
                    }

                    public String getNameColumnLabel() {
                         return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subprojects.columns.name").localize();
                    }

                    public String getDeleteColumnLabel() {
                         return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subprojects.colums.delete").localize();
                    }

                    public String getUpColumnLabel() {
                          return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subprojects.columns.up").localize();
                    }

                    public String getDownColumnLabel() {
                          return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subprojects.columns.down").localize();
                    }

                    public String getDeleteLabel() {
                          return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.delete").localize();
                    }

                    public String getUpLabel() {
                         return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.order.up").localize();
                    }

                    public String getDownLabel() {
                       return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.order.down").localize();
                    }

                    public String getConfirmRemoveLabel() {
                          return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.subproject.remove.confirm").localize();
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
