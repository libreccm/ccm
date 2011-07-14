package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciPublicPersonalProfile;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.SciMember;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicPersonalProfilePropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public SciPublicPersonalProfilePropertiesStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        super(itemModel, parent);

        //Use a segemented panel to provide an easy for adding addional forms
        //to the step.
        SegmentedPanel segmentedPanel = new SegmentedPanel();

        setDefaultEditKey(EDIT_SHEET_NAME);

        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new SciPublicPersonalProfilePropertyForm(itemModel,
                                                               this);
        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciPublicPersonalProfileGlobalizationUtil.
                globalize(
                "scipublicpersonalprofile.ui.profile.edit_basic_properties").
                localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(getSciPublicPersonalProfilePropertySheet(
                itemModel));

        segmentedPanel.addSegment(new Label((String) SciPublicPersonalProfileGlobalizationUtil.
                globalize("scipublicpersonalprofile.ui.profile.basic_properties").
                localize()), basicProperties);

        setDisplayComponent(segmentedPanel);
    }

    public static Component getSciPublicPersonalProfilePropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciPublicPersonalProfileGlobalizationUtil.globalize(
                "scipublicpersonalprofile.ui.owner"),
                  SciPublicPersonalProfile.OWNER, new OwnerFormatter());

        sheet.add(SciPublicPersonalProfileGlobalizationUtil.globalize(
                "scipublicpersonalprofile.ui.profile_url"),
                  SciPublicPersonalProfile.PROFILE_URL);

        return sheet;
    }

    private static class OwnerFormatter implements
            DomainObjectPropertySheet.AttributeFormatter {

        public String format(DomainObject obj, String attribute, PageState state) {
            SciPublicPersonalProfile profile = (SciPublicPersonalProfile) obj;

            SciMember owner = profile.getOwner();

            if (owner == null) {
                return "";
            } else {
                return owner.getFullName();
            }
        }
    }
}
