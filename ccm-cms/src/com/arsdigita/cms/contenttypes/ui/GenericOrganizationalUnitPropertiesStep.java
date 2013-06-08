/*
 * Copyright (c) 2010 Jens Pelzetter
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
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
import com.arsdigita.globalization.GlobalizedMessage;
import java.text.DateFormat;

/**
 * PropertiesStep for GenericOrganizationalUnits. This properties step uses an 
 * SegmentedPanel to show all relevant data of an organization. To change the 
 * steps shown, extend this class, and overwrite the {@link addSteps()} method.
 *
 * @author Jens Pelzetter
 * @version $Id$
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
                "cms.contenttypes.ui.genericorgaunit.title"),
                  GenericOrganizationalUnit.TITLE);
        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorganunit.addendum"),
                  GenericOrganizationalUnit.ADDENDUM);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(ContenttypesGlobalizationUtil.globalize(
                    "cms.ui.authoring.page_launch_date"),
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
                new Label(ContenttypesGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.genericorganunit.basic_properties")),
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
        /* jensp, 2011-05-01:
         * Member step is now a full step, to improve performence of the 
         * content center gui.
        addStep(new GenericOrganizationalUnitPersonPropertiesStep(itemModel,
                                                                  parent),
                "cms.contenttypes.ui.orgaunit.persons");
         
         */
    }

    /**
     * Helper method for editing a step.
     *
     * @param step
     * @param labelKey
     */
    protected void addStep(SimpleEditStep step, String labelKey) {
        segmentedPanel.addSegment(new Label(ContenttypesGlobalizationUtil.
                globalize(labelKey)),
                                  step);
    }

    protected void addStep(SimpleEditStep step, GlobalizedMessage label) {
        segmentedPanel.addSegment(new Label(label), step);
    }

    protected SegmentedPanel getSegmentedPanel() {
        return segmentedPanel;
    }
}
