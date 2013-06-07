/*
 * Copyright (C) 2009 Jens Pelzetter
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
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.text.DateFormat;

public class GenericAddressPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public GenericAddressPropertiesStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        createEditSheet(itemModel);

        setDisplayComponent(getAddressPropertySheet(itemModel));
    }

    protected void createEditSheet(ItemSelectionModel itemModel) {
        BasicPageForm editSheet;
        editSheet = new GenericAddressPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, 
            "Edit", 
            new WorkflowLockedComponentAccess(editSheet, itemModel), 
            editSheet.getSaveCancelSection().getCancelButton());
    }

    public static Component getAddressPropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.name"), GenericAddress.NAME);
        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.title"), GenericAddress.TITLE);
        sheet.add(ContenttypesGlobalizationUtil
                  .globalize("cms.contenttypes.ui.address.address"), 
                  GenericAddress.ADDRESS);
        sheet.add(ContenttypesGlobalizationUtil
                  .globalize("cms.contenttypes.ui.address.postal_code"), 
                  GenericAddress.POSTAL_CODE);
        sheet.add(ContenttypesGlobalizationUtil
                  .globalize("cms.contenttypes.ui.address.city"), 
                  GenericAddress.CITY);
        sheet.add(ContenttypesGlobalizationUtil
                  .globalize("cms.contenttypes.ui.address.state"), 
                  GenericAddress.STATE);

        sheet.add(ContenttypesGlobalizationUtil
                  .globalize("cms.contenttypes.ui.address.iso_country_code"),
                GenericAddress.ISO_COUNTRY_CODE,
                new DomainObjectPropertySheet.AttributeFormatter() {

                    public String format(DomainObject item,
                            String attribute,
                            PageState state) {
                        GenericAddress address = (GenericAddress) item;
                        if (address != null && address.getIsoCountryCode() != null) {
                            return GenericAddress
                                   .getCountryNameFromIsoCode(address.getIsoCountryCode());
                        } else {
                            return (String) GlobalizationUtil
                                            .globalize("cms.ui.unknown")
                                            .localize();
                        }
                    }
                });

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.ui.authoring.page_launch_date"),
                    ContentPage.LAUNCH_DATE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            ContentPage page = (ContentPage) item;
                            if (page.getLaunchDate() != null) {
                                return DateFormat
                                       .getDateInstance(DateFormat.LONG)
                                       .format(page.getLaunchDate());
                            } else {
                                return (String) GlobalizationUtil
                                                .globalize("cms.ui.unknown")
                                                .localize();
                            }
                        }
                    });
        }

        return sheet;
    }
}
