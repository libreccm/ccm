package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.contenttypes.HealthCareFacility;
import com.arsdigita.cms.contenttypes.util.HealthCareFacilityGlobalizationUtil;

import java.text.DateFormat;
import org.apache.log4j.Logger;

/**
 * AuthoringStep for the basic properties of a basic contact
 */
public class HealthCareFacilityPropertiesStep extends SimpleEditStep {

    private static final Logger logger = Logger.getLogger(HealthCareFacilityPropertiesStep.class);
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
    public HealthCareFacilityPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        /* Use a Segmented Panel for the multiple parts of data */
        SegmentedPanel segmentedPanel = new SegmentedPanel();

        setDefaultEditKey(EDIT_BASIC_SHEET_NAME);

        /* The different parts of information are displayed in seperated segments each containing a SimpleEditStep */
        /* Well, not so simple anymore... */

        /* A new SimpleEditStep */
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, parent, EDIT_BASIC_SHEET_NAME);

        /* Create the edit component for this SimpleEditStep and the corresponding link */
        BasicPageForm editBasicSheet = new HealthCareFacilityPropertyForm(itemModel, this);
        basicProperties.add(EDIT_BASIC_SHEET_NAME, (String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.edit_basic_properties").localize(), new WorkflowLockedComponentAccess(editBasicSheet, itemModel), editBasicSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        basicProperties.setDisplayComponent(getHealthCareFacilityPropertySheet(itemModel));

        /* Add the SimpleEditStep to the segmented panel */
        segmentedPanel.addSegment(new Label((String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.basic_properties").localize()), basicProperties);

        // If not disabled via registry, add the ui for attaching an address
        if (!HealthCareFacility.getConfig().getHideAddress()) {
            HealthCareFacilityAddressPropertiesStep addressProperties = new HealthCareFacilityAddressPropertiesStep(itemModel, parent);
            segmentedPanel.addSegment(new Label((String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.address").localize()), addressProperties);
        }

        HealthCareFacilityContactPropertiesStep contactProperties = new HealthCareFacilityContactPropertiesStep(itemModel, parent);
        segmentedPanel.addSegment(new Label((String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contact").localize()), contactProperties);

        /* Sets the composed segmentedPanel as display component */
        setDisplayComponent(segmentedPanel);
    }

    /**
     * Creates and returns the sheet for editing the basic properties
     * of an organization. (@see HealthCareFacilityPropertyForm).
     * 
     * 
     * @param itemModel
     * @return The sheet for editing the properties of the organization.
     */
    public static Component getHealthCareFacilityPropertySheet(ItemSelectionModel itemModel) {


        /* The DisplayComponent for the Basic Properties */
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"), "name");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), "title");

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date"), ContentPage.LAUNCH_DATE, new DomainObjectPropertySheet.AttributeFormatter() {

                public String format(DomainObject obj, String attribute, PageState state) {
                    ContentPage page = (ContentPage) obj;
                    if (page.getLaunchDate() != null) {
                        return DateFormat.getDateInstance(DateFormat.LONG).format(page.getLaunchDate());
                    } else {
                        return (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;
    }
}
