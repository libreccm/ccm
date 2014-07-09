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

                    public GlobalizedMessage getSelectSuperiorOrgaUnitLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.select");
                    }

                    public String getSuperiorOrgaUnitType() {
                        return SciProject.class.getName();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                         return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                       return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.no_suitable_language_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                         return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                         return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.already_added");
                    }
                });
        add(ADD_SUPERPROJECT_SHEET_NAME,
            SciProjectGlobalizationUtil.globalize("sciproject.ui.superproject.add"),
            new WorkflowLockedComponentAccess(addSuperProjectSheet, itemModel),
            addSuperProjectSheet.getSaveCancelSection().getCancelButton());
        
        final GenericOrganizationalUnitSuperiorOrgaUnitsTable superProjectsTable = new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel, new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

            public GlobalizedMessage getEmptyViewLabel() {
                return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.empty_view");
            }

            public GlobalizedMessage getNameColumnLabel() {
               return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.name");
            }

            public GlobalizedMessage getDeleteColumnLabel() {
                return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.delete");
            }

            public GlobalizedMessage getUpColumnLabel() {
                return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.up");
            }

            public GlobalizedMessage getDownColumnLabel() {
                return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superprojects.columns.down");
            }

            public GlobalizedMessage getDeleteLabel() {
               return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.delete");
            }

            public GlobalizedMessage getUpLabel() {
               return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.order.up");
            }

            public GlobalizedMessage getDownLabel() {
               return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.order.down");
            }

            public GlobalizedMessage getConfirmRemoveLabel() {
               return  SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.superproject.remove.confirm");
            }

            public String getAssocType() {
                return ASSOC_TYPE;
            }
            
            public String getContentType() {
                return "com.arsdigita.cms.contenttypes.SciProjectBundle";
            }
        });
        
        setDisplayComponent(superProjectsTable);
        
    }
}
