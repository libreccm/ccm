/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectInvolvedOrganizationsStep extends SimpleEditStep {

    private final static String ADD_INVOLVED_ORGANIZATION_STEP =
                                "SciProjectAddInvolvedOrganization";
    public final static String ASSOC_TYPE = "InvolvedOrganization";

    public SciProjectInvolvedOrganizationsStep(
            final ItemSelectionModel itemModel,
                                               final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectInvolvedOrganizationsStep(
            final ItemSelectionModel itemModel,
                                               final AuthoringKitWizard parent,
                                               final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addInvolvedOrgaSheet =
                            new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public String getSelectSuperiorOrgaUnitLabel() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.select").localize();
                    }

                    public String getSuperiorOrgaUnitType() {
                        return GenericOrganizationalUnit.class.getName();                                
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public String getNothingSelectedMessage() {
                        return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.select.nothing").localize();
                    }

                    public String getNoSuitableLanguageVariantMessage() {
                           return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.no_suitable_language_variant").localize();
                    }

                    public String getAddingToItselfMessage() {
                           return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.adding_to_itself").localize();
                    }

                    public String getAlreadyAddedMessage() {
                           return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.already_added").localize();
                    }
                });
        add(ADD_INVOLVED_ORGANIZATION_STEP, 
            SciProjectGlobalizationUtil.globalize("sciproject.ui.involved_orgas.add"),
            new WorkflowLockedComponentAccess(addInvolvedOrgaSheet, itemModel),
            addInvolvedOrgaSheet.getSaveCancelSection().getCancelButton());
        
        final GenericOrganizationalUnitSuperiorOrgaUnitsTable involvedTable = new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel, new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

            public String getEmptyViewLabel() {
                 return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.empty_view").localize();
            }

            public String getNameColumnLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas..columns.name").localize();
            }

            public String getDeleteColumnLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.columns.delete").localize();
            }

            public String getUpColumnLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.columns.up").localize();
            }

            public String getDownColumnLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.columns.down").localize();
            }

            public String getDeleteLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.delete").localize();
            }

            public String getUpLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.up").localize();
            }

            public String getDownLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.down").localize();
            }

            public String getConfirmRemoveLabel() {
                return (String) SciProjectGlobalizationUtil.globalize(
                                "sciproject.ui.involved_orgas.delete.confirm").localize();
            }

            public String getAssocType() {
                return ASSOC_TYPE;
            }
            
            public String getContentType() {
                return null;
            }
        });
        
        setDisplayComponent(involvedTable);
    }
}
