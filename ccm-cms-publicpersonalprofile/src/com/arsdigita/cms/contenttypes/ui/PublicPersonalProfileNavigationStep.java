package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavigationStep extends SimpleEditStep {

    public static final String EDIT_NAV_ITEM_SHEET_NAME = "editNavItem";
    public static final String EDIT_NAV_GENERATED_ITEM_SHEET_NAME =
                               "editGeneratedNavItem";

    public PublicPersonalProfileNavigationStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicPersonalProfileNavigationStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm editNavItemSheet =
                      new PublicPersonalProfileNavigationAddForm(itemModel,
                                                                 this);
        add(EDIT_NAV_ITEM_SHEET_NAME,
            (String) PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.profile.content.add").localize(),
            new WorkflowLockedComponentAccess(editNavItemSheet, itemModel),
            editNavItemSheet.getSaveCancelSection().getCancelButton());

        BasicItemForm editGeneratedNavItemSheet =
                      new PublicPersonalProfileNavigationGeneratedAddForm(
                itemModel,
                this);
        add(EDIT_NAV_GENERATED_ITEM_SHEET_NAME,
            (String) PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.profile.generated_content.add").
                localize(),
            new WorkflowLockedComponentAccess(editGeneratedNavItemSheet,
                                              itemModel),
            editGeneratedNavItemSheet.getSaveCancelSection().getCancelButton());

        PublicPersonalProfileNavigationTable navTable =
                                             new PublicPersonalProfileNavigationTable(
                itemModel, this);
        setDisplayComponent(navTable);
    }
}
