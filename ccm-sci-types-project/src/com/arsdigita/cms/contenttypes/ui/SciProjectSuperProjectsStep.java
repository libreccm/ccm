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
public class SciProjectSuperProjectsStep extends SimpleEditStep {

    private final static String ADD_SUPERPROJECT_SHEET_NAME = "SciProjectAddSuperProject";
    public final static String ASSOC_TYPE = "SubProject";

    public SciProjectSuperProjectsStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectSuperProjectsStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent,
                                       final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addSuperProjectSheet =
                            new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public String getSelectSuperiorOrgaUnitLabel() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.select").localize();
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciProject.class.getName();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                         return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.select.nothing").localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                       return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.no_suitable_language_variant").localize();
                    }

                    public String getAddingToItselfMessage() {
                         return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.adding_to_itself").localize();
                    }

                    public String getAlreadyAddedMessage() {
                         return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.already_added").localize();
                    }
                });
        add(ADD_SUPERPROJECT_SHEET_NAME,
            (String) SciProjectGlobalizationUtil.globalize("sciproject.ui.superproject.add").localize(),
            new WorkflowLockedComponentAccess(addSuperProjectSheet, itemModel),
            addSuperProjectSheet.getSaveCancelSection().getCancelButton());
        
        final GenericOrganizationalUnitSuperiorOrgaUnitsTable superProjectsTable = new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel, new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

            public String getEmptyViewLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.empty_view").localize();
            }

            public String getNameColumnLabel() {
               return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.name").localize();
            }

            public String getDeleteColumnLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.delete").localize();
            }

            public String getUpColumnLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.up").localize();
            }

            public String getDownColumnLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.down").localize();
            }

            public String getDeleteLabel() {
               return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.delete").localize();
            }

            public String getUpLabel() {
               return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.order.up").localize();
            }

            public String getDownLabel() {
               return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.order.down").localize();
            }

            public String getConfirmRemoveLabel() {
               return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.remove.confirm").localize();
            }

            public String getAssocType() {
                return ASSOC_TYPE;
            }
        });
        
        setDisplayComponent(superProjectsTable);
        
    }
}
