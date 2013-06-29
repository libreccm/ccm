package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;

import org.apache.log4j.Logger;

/**
 * AuthoringStep for the basic properties of a basic contact
 */
public class GenericContactPropertiesStep extends SimpleEditStep {

    private static final Logger logger = Logger.getLogger(
                                         GenericContactPropertiesStep.class);
    /**
     * Name of the this edit sheet (Don't know if this this really needed.
     * It has the same value in almost all PropertiesStep classes)
     */
    public static final String EDIT_BASIC_SHEET_NAME = "editBasic";

    /**
     * Constructor for the PropertiesStep.
     *
     * @param itemModel
     * @param parent
     */
    public GenericContactPropertiesStep(ItemSelectionModel itemModel, 
                                        AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_BASIC_SHEET_NAME);
        SegmentedPanel segmentedPanel = createEditSheet(itemModel, parent);

        /* Sets the composed segmentedPanel as display component */
        setDisplayComponent(segmentedPanel);
    }

    /**
     * 
     * @param itemModel
     * @param parent
     * @return 
     */
    protected SegmentedPanel createEditSheet(ItemSelectionModel itemModel, 
                                             AuthoringKitWizard parent) {
        /* Use a Segmented Panel for the multiple parts of data */
        SegmentedPanel segmentedPanel = new SegmentedPanel();

        /* The different parts of information are displayed in seperated 
         * segments each containing a SimpleEditStep */
        /* Well, not so simple anymore... */
        /* A new SimpleEditStep */
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, 
                                                            parent, 
                                                            EDIT_BASIC_SHEET_NAME);

        /* Create the edit component for this SimpleEditStep and the corresponding link */
        BasicPageForm editBasicSheet = new GenericContactPropertyForm(itemModel, this);
        basicProperties.add(
                EDIT_BASIC_SHEET_NAME, 
                ContenttypesGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.contact.edit_basic_properties"), 
                new WorkflowLockedComponentAccess(
                        editBasicSheet, 
                        itemModel), 
                        editBasicSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        basicProperties.setDisplayComponent(getContactPropertySheet(itemModel));

        /* Add the SimpleEditStep to the segmented panel */
        segmentedPanel
            .addSegment(new Label(ContenttypesGlobalizationUtil
                        .globalize("cms.contenttypes.ui.contact.basic_properties")),
                        basicProperties);

        // If not disabled via registry, add the ui for attaching a person
        if (!GenericContact.getConfig().getHidePerson()) {

            GenericContactPersonPropertiesStep personProperties = new 
                    GenericContactPersonPropertiesStep(itemModel, parent);
            segmentedPanel
                .addSegment(new Label(ContenttypesGlobalizationUtil
                            .globalize("cms.contenttypes.ui.contact.person")), 
                            personProperties);

        }

        if (!GenericContact.getConfig().getHideAddress()) {

            GenericContactAddressPropertiesStep addressProperties = new 
                    GenericContactAddressPropertiesStep(itemModel, parent);
            segmentedPanel
                .addSegment(new Label(ContenttypesGlobalizationUtil
                            .globalize("cms.contenttypes.ui.contact.address")), 
                            addressProperties);

        }

        GenericContactEntriesPropertiesStep contactEntries = new 
                GenericContactEntriesPropertiesStep(itemModel, parent);
        segmentedPanel
            .addSegment(new Label(ContenttypesGlobalizationUtil
                        .globalize("cms.contenttypes.ui.contact.contactEntry")), 
                        contactEntries);

        return segmentedPanel;
    }

    /**
     * Creates and returns the sheet for editing the basic properties
     * of a contact. (@see GenericContactPropertyForm).
     * 
     * @param itemModel
     * @return The sheet for editing the properties of the contact.
     */
    public static Component getContactPropertySheet(ItemSelectionModel itemModel) {


        /* The DisplayComponent for the Basic Properties */
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), "title");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"), "name");

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }

        return sheet;
    }
}
