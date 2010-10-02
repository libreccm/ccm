/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import java.text.DateFormat;

/**
 * PropertiesStep for GenericOrganizationalUnits. This properties step uses an 
 * SegmentedPanel to show all relevant data of an organization. To change the 
 * steps shown, extend this class, and overwrite the {@link addSteps()} method.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";
    private SegmentedPanel segmentedPanel;

    public GenericOrganizationalUnitPropertiesStep(ItemSelectionModel itemModel,
                                                   AuthoringKitWizard parent) {
        super(itemModel, parent);

        segmentedPanel = new SegmentedPanel();
        setDefaultEditKey(EDIT_SHEET_NAME);

        addBasicProperties(itemModel, parent);
        addSteps(itemModel, parent);

        setDisplayComponent(segmentedPanel);
    }

    public static Component getGenericOrganizationalUnitPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorganunit.name"),
                  GenericOrganizationalUnit.NAME);
        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorganunit.addendum"),
                  GenericOrganizationalUnit.ADDENDUM);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add((String) ContenttypesGlobalizationUtil.globalize(
                    "cms.ui.authoring.page_launch_date").localize(),
                      ContentPage.LAUNCH_DATE,
                      new DomainObjectPropertySheet.AttributeFormatter() {

                public String format(DomainObject item,
                                     String attribute,
                                     PageState state) {
                    ContentPage page = (ContentPage) item;
                    if (page.getLaunchDate() != null) {
                        return DateFormat.getDateInstance(DateFormat.LONG).
                                format(page.getLaunchDate());
                    } else {
                        return (String) ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;
    }

    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new GenericOrganizationalUnitPropertyForm(itemModel, this);
        basicProperties.add(EDIT_SHEET_NAME,
                            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.edit_basic_properties").
                localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(getGenericOrganizationalUnitPropertySheet(
                itemModel));

        segmentedPanel.addSegment(
                new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorganunit.basic_properties").
                localize()),
                basicProperties);
    }

    /**
     * This method adds the steps for editing the relations of an organization.
     * To change the steps, overwrite this method.
     *
     * @param itemModel
     * @param parent
     */
    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                   parent),
                "cms.contenttypes.ui.orgaunit.contact");        
        addStep(new GenericOrganizationalUnitPersonPropertiesStep(itemModel,
                                                                  parent),
                "cms.contenttypes.ui.orgaunit.persons");
    }

    /**
     * Helper method for editing a step.
     *
     * @param step
     * @param labelKey
     */
    protected void addStep(SimpleEditStep step, String labelKey) {
        segmentedPanel.addSegment(new Label((String) ContenttypesGlobalizationUtil.
                globalize(labelKey).localize()),
                                  step);
    }

    protected SegmentedPanel getSegmentedPanel() {
        return segmentedPanel;
    }
}
