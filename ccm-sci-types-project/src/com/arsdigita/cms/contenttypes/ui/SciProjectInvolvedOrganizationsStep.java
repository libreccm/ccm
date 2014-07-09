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
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciProjectInvolvedOrganizationsStep extends SimpleEditStep {

    private final static String ADD_INVOLVED_ORGANIZATION_STEP = "SciProjectAddInvolvedOrganization";
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

        final BasicItemForm addInvolvedOrgaSheet
                            = new GenericOrganizationalUnitSuperiorOrgaUnitAddForm(
                itemModel,
                new GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer() {

                    public GlobalizedMessage getSelectSuperiorOrgaUnitLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.select");
                    }

                    public String getSuperiorOrgaUnitType() {
                        return GenericOrganizationalUnit.class.getName();
                    }

                    public String getAssocType() {
                        return ASSOC_TYPE;
                    }

                    public GlobalizedMessage getNothingSelectedMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.select.nothing");
                    }

                    public GlobalizedMessage getNoSuitableLanguageVariantMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.no_suitable_language_variant");
                    }

                    public GlobalizedMessage getAddingToItselfMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.adding_to_itself");
                    }

                    public GlobalizedMessage getAlreadyAddedMessage() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.already_added");
                    }

                });
        add(ADD_INVOLVED_ORGANIZATION_STEP,
            SciProjectGlobalizationUtil.globalize("sciproject.ui.involved_orgas.add"),
            new WorkflowLockedComponentAccess(addInvolvedOrgaSheet, itemModel),
            addInvolvedOrgaSheet.getSaveCancelSection().getCancelButton());

        final GenericOrganizationalUnitSuperiorOrgaUnitsTable involvedTable
                                                              = new GenericOrganizationalUnitSuperiorOrgaUnitsTable(
                itemModel, new GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer() {

                    public GlobalizedMessage getEmptyViewLabel() {
                        return SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.empty_view");
                    }

                    public GlobalizedMessage getNameColumnLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas..columns.name");
                    }

                    public GlobalizedMessage getDeleteColumnLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.columns.delete");
                    }

                    public GlobalizedMessage getUpColumnLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.columns.up");
                    }

                    public GlobalizedMessage getDownColumnLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.columns.down");
                    }

                    public GlobalizedMessage getDeleteLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.delete");
                    }

                    public GlobalizedMessage getUpLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.up");
                    }

                    public GlobalizedMessage getDownLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.down");
                    }

                    public GlobalizedMessage getConfirmRemoveLabel() {
                        return  SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.involved_orgas.delete.confirm");
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
