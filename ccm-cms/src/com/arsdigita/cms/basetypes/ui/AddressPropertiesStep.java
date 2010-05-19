/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.basetypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.basetypes.Address;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.text.DateFormat;

public class AddressPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public AddressPropertiesStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        createEditSheet(itemModel);

        setDisplayComponent(getAddressPropertySheet(itemModel));
    }

    protected void createEditSheet(ItemSelectionModel itemModel) {
        BasicPageForm editSheet;
        editSheet = new AddressPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel), editSheet.getSaveCancelSection().getCancelButton());
    }

    public static Component getAddressPropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.name").localize(), Address.NAME);
        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.title").localize(), Address.TITLE);
        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.address").localize(), Address.ADDRESS);
        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.postal_code").localize(), Address.POSTAL_CODE);
        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.city").localize(), Address.CITY);
        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.state").localize(), Address.STATE);

        sheet.add((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.address.iso_country_code").localize(),
                Address.ISO_COUNTRY_CODE,
                new DomainObjectPropertySheet.AttributeFormatter() {

                    public String format(DomainObject item,
                            String attribute,
                            PageState state) {
                        Address address = (Address) item;
                        if (address != null && address.getIsoCountryCode() != null) {
                            return Address.getCountryNameFromIsoCode(address.getIsoCountryCode());
                        } else {
                            return (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
                        }
                    }
                });

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add((String) GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date").localize(),
                    ContentPage.LAUNCH_DATE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            ContentPage page = (ContentPage) item;
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
