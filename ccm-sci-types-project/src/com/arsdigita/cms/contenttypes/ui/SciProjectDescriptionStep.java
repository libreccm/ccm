package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectDescriptionStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "SciProjectDescription";
    private final SegmentedPanel segmentedPanel;

    public SciProjectDescriptionStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent) {
        super(itemModel, parent);

        segmentedPanel = new SegmentedPanel();
        setDefaultEditKey(EDIT_SHEET_NAME);

        addSteps(itemModel, parent);

        setDisplayComponent(segmentedPanel);
    }

    protected SegmentedPanel getSegmentedPanel() {
        return segmentedPanel;
    }

    protected void addSteps(final ItemSelectionModel itemModel,
                            final AuthoringKitWizard parent) {
        addStep(new SciProjectDescriptionTextStep(itemModel, parent),
                "sciproject.ui.steps.description.title");
        
        if (SciProject.getConfig().getEnableSponsor()) {
            addStep(new SciProjectSponsorStep(itemModel, parent),
                    "sciproject.ui.steps.sponsor.title");
        }
        
        if (SciProject.getConfig().getEnableFunding()) {
            addStep(new SciProjectFundingStep(itemModel, parent),
                    "sciproject.ui.steps.funding.title");
        }
    }

    protected void addStep(final SimpleEditStep step,
                           final String labelKey) {
        segmentedPanel.addSegment(
                new Label(SciProjectGlobalizationUtil.globalize(labelKey)), step);
    }

//    private String EDIT_PROJECT_DESC_SHEET_NAME = "editProjectDesc";
//    private String UPLOAD_PROJECT_DESC_SHEET_NAME = "uploadProjectDesc";
//
//    public SciProjectDescriptionStep(final ItemSelectionModel itemModel,
//                                     final AuthoringKitWizard parent) {
//        this(itemModel, parent, null);
//    }
//
//    public SciProjectDescriptionStep(final ItemSelectionModel itemModel,
//                                     final AuthoringKitWizard parent,
//                                     final String prefix) {
//        super(itemModel, parent, prefix);
//
//        final BasicItemForm editDescForm =
//                            new SciProjectDescriptionEditForm(itemModel);
//        add(EDIT_PROJECT_DESC_SHEET_NAME,
//            SciProjectGlobalizationUtil.globalize("sciproject.ui.desc.edit"),
//            new WorkflowLockedComponentAccess(editDescForm, itemModel),
//            editDescForm.getSaveCancelSection().getCancelButton());
//
//        final SciProjectDescriptionUploadForm uploadDescForm =
//                                              new SciProjectDescriptionUploadForm(
//                itemModel);
//        add(UPLOAD_PROJECT_DESC_SHEET_NAME,
//            SciProjectGlobalizationUtil.globalize("sciproject.ui.desc.upload"),
//            new WorkflowLockedComponentAccess(uploadDescForm, itemModel),
//            uploadDescForm.getSaveCancelSection().getCancelButton());
//
//        setDisplayComponent(
//                getSciProjectEditDescSheet(itemModel));
//
//    }
//
//    public static Component getSciProjectEditDescSheet(
//            final ItemSelectionModel itemModel) {
//        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
//                itemModel);
//
//        sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.desc"),
//                  SciProject.PROJECT_DESCRIPTION);
//
//        if (SciProject.getConfig().getEnableFunding()) {
//            sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.funding"),
//                      SciProject.FUNDING);
//        }
//        if (SciProject.getConfig().getEnableFundingVolume()) {
//            sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.funding.volume"),
//                      SciProject.FUNDING_VOLUME);
//        }
//
//        return sheet;
//    }
}
