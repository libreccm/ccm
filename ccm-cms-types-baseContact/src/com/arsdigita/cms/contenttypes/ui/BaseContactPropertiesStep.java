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
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.cms.contenttypes.util.PersonGlobalizationUtil;

import java.text.DateFormat;
import org.apache.log4j.Logger;

/**
 * AuthoringStep for the basic properties of a basic contact
 */
public class BaseContactPropertiesStep extends SimpleEditStep {

    private static final Logger logger = Logger.getLogger(BaseContactPropertiesStep.class);

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
    public BaseContactPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        /* Use a Segmented Panel for the multiple parts of data */
        SegmentedPanel segmentedPanel = new SegmentedPanel();
        
        setDefaultEditKey(EDIT_BASIC_SHEET_NAME);

        /* The different parts of information are displayed in seperated segments each containing a SimpleEditStep */
        /* Well, not so simple anymore... */
        
        /* A new SimpleEditStep */
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, parent, EDIT_BASIC_SHEET_NAME);
        /* Create the edit component for this SimpleEditStep and the corresponding link */
        BasicPageForm editBasicSheet = new BaseContactPropertyForm(itemModel, this);
        basicProperties.add(EDIT_BASIC_SHEET_NAME, "Edit Basic", new WorkflowLockedComponentAccess(editBasicSheet, itemModel), editBasicSheet.getSaveCancelSection().getCancelButton());
        /* Set the displayComponent for this step */
        basicProperties.setDisplayComponent(getBaseContactPropertySheet(itemModel));

        BaseContactPersonPropertiesStep personProperties = new BaseContactPersonPropertiesStep(itemModel, parent);
        BaseContactAddressPropertiesStep addressProperties = new BaseContactAddressPropertiesStep(itemModel, parent);

        /* Add the SimpleEditStep to the segmented panel */
        segmentedPanel.addSegment(new Label("Basic"), basicProperties);
        segmentedPanel.addSegment(new Label((String)GlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.person").localize()), personProperties);
        segmentedPanel.addSegment(new Label((String)GlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.address").localize()), addressProperties);
//        segmentedPanel.addSegment(new Label((String)GlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.baseContactEntries").localize()), BaseContactEntriesSection);        
        
        /* Sets the composed segmentedPanel as display component */
        setDisplayComponent(segmentedPanel);
    }

    /**
     * Creates and returns the sheet for editing the basic properties
     * of an organization. (@see BaseContactPropertyForm).
     * 
     * @param itemModel
     * @return The sheet for editing the properties of the organization.
     */
    public static Component getBaseContactPropertySheet(ItemSelectionModel itemModel) {
        

        /* The DisplayComponent for the Basic Properties */
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"),"name");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"),"title");
  
        if(!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date"), ContentPage.LAUNCH_DATE, new DomainObjectPropertySheet.AttributeFormatter() {

                public String format(DomainObject obj, String attribute, PageState state) {
                    ContentPage page = (ContentPage)obj;
                    if(page.getLaunchDate() != null) {
                        return DateFormat.getDateInstance(DateFormat.LONG).format(page.getLaunchDate());
                    }
                    else {
                        return (String)GlobalizationUtil.globalize("cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;
    }
    
}
