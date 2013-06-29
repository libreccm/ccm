/*
 * Copyright (C) 2009 Sören Bernstein
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
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.util.GlobalizationUtil;
import java.text.DateFormat;

/**
 * 
 * 
 */
public class GenericPersonPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor.
     * 
     * @param itemModel
     * @param parent 
     */
    public GenericPersonPropertiesStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent) {
        super(itemModel, parent);

        /* Use a Segmented Panel for the multiple parts of data */
        SegmentedPanel segmentedPanel = new SegmentedPanel();

        setDefaultEditKey(EDIT_SHEET_NAME);

        /* A new SimpleEditStep */
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, parent,
                                                            EDIT_SHEET_NAME);

        /* Create the edit component for this SimpleEditStep and the corresponding link */
        BasicPageForm editBasicSheet = new GenericPersonPropertyForm(itemModel,
                                                                     this);
        basicProperties.add(EDIT_SHEET_NAME,
                            ContenttypesGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.genericperson.edit_basic_properties"), 
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection()
                                          .getCancelButton());

        /* Set the displayComponent for this step */
        basicProperties.setDisplayComponent(getGenericPersonPropertySheet(
                itemModel));

        /* Add the SimpleEditStep to the segmented panel */
        segmentedPanel.addSegment(new 
                Label(ContenttypesGlobalizationUtil
                    .globalize("cms.contenttypes.ui.genericperson.basic_properties")), 
                basicProperties);


//        GenericPersonContactPropertiesStep contactProperties = new GenericPersonContactPropertiesStep(itemModel, parent);
//        segmentedPanel.addSegment(new Label((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.contact").localize()), contactProperties);

        GenericPersonAliasPropertiesStep aliasStep =
                                         new GenericPersonAliasPropertiesStep(
                itemModel, parent);
        segmentedPanel.addSegment(new Label(ContenttypesGlobalizationUtil.
                globalize("cms.contenttypes.ui.genericperson.alias_title")),
                                  aliasStep);

        /* Sets the composed segmentedPanel as display component */
        setDisplayComponent(segmentedPanel);

    }

    public static Component getGenericPersonPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericperson.surname"),
                  GenericPerson.SURNAME);
        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericperson.givenname"),
                  GenericPerson.GIVENNAME);
        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericperson.titlepre"),
                  GenericPerson.TITLEPRE);
        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericperson.titlepost"),
                  GenericPerson.TITLEPOST);
        sheet.add(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericperson.birthdate"),
                  GenericPerson.BIRTHDATE,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject item,
                                 String attribute,
                                 PageState state) {
                GenericPerson person = (GenericPerson) item;
                if (person.getBirthdate() != null) {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(person.
                            getBirthdate());
                } else {
                    return (String) GlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                }
            }
        });

        sheet.add(ContenttypesGlobalizationUtil
                  .globalize("cms.contenttypes.ui.genericperson.gender"),
                  GenericPerson.GENDER,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject item,
                                 String attribute,
                                 PageState state) {
                //ContentPage page = (ContentPage) item;
                GenericPerson person = (GenericPerson) item;
                if (person.getGender() != null) {
                    return (String) ContenttypesGlobalizationUtil.globalize(
                                    "cms.contenttypes.ui.genericperson.gender."
                                    + person.getGender().toLowerCase()).localize();
                } else {
                    return (String) GlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                }
            }
        });

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(ContenttypesGlobalizationUtil.globalize(
                                  "cms.ui.authoring.page_launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }

        sheet.add(ContenttypesGlobalizationUtil.globalize(
                              "cms.contenttypes.ui.genericperson.description"),
                  GenericPerson.DESCRIPTION);
        return sheet;
    }
}
