package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Step for editing the basic properties of an {@link SciMember}.
 *
 * @author Jens Pelzetter
 * @see SciMember
 * @see GenericPerson
 */
public class SciMemberPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public SciMemberPropertiesStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent) {
        super(itemModel, parent);

        BasicPageForm editSheet;
        editSheet = new SciMemberPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.member.edit_basic_properties").localize(),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getSciMemberPropertySheet(itemModel));

    }

    public static Component getSciMemberPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet;

        sheet = (DomainObjectPropertySheet) GenericPersonPropertiesStep.
                getGenericPersonPropertySheet(itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.member.associatedMember"),
                SciMember.ASSOCIATED_MEBER);
        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.member.formerMember"),
                  SciMember.FORMER_MEMBER);

        return sheet;
    }
}
