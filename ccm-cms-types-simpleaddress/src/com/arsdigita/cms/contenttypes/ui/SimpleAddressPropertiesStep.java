/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.cms.contenttypes.SimpleAddress;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.util.SimpleAddressGlobalizationUtil;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.text.DateFormat;

/**
 * Authoring step to edit the simple attributes of the SimpleAddress content
 * type (and its subclasses). This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 *
 * @author <a href="mailto:dominik@redhat.com">Dominik Kacprzak</a>
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 *
 */
public class SimpleAddressPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    public SimpleAddressPropertiesStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent ) {
        super(itemModel, parent );

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new SimpleAddressPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent(getAddressPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the SimpleAddress
     * specified by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the address content type.
     **/
    public static Component getAddressPropertySheet(ItemSelectionModel
                                                   itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"),  
                  SimpleAddress.TITLE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"),  
                  SimpleAddress.NAME );
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }
        sheet.add(SimpleAddressGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.simpleaddress.address"),
                  SimpleAddress.ADDRESS);
        if (!SimpleAddress.getConfig().getHideCountryCodeSelection()) {
            sheet.add(SimpleAddressGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.simpleaddress.iso_country_code"),
                      SimpleAddress.ISO_COUNTRY_CODE,
                      new DomainObjectPropertySheet.AttributeFormatter() {
                          public String format( DomainObject item,
                                                 String attribute,
                                                 PageState state ) {
                              SimpleAddress address = ( SimpleAddress ) item;
                              if ( address.getCountryIsoCode() != null ) {
                                  return address.getCountryIsoCode();
                              } else {
                                  return (String)GlobalizationUtil
                                                 .globalize("cms.ui.unknown")
                                                 .localize();
                              }
                           }
                      });
        }
        if (!SimpleAddress.getConfig().getHidePostalCode()) {
            sheet.add(SimpleAddressGlobalizationUtil.globalize(
                              "cms.contenttypes.ui.simpleaddress.postal_code"),
                      SimpleAddress.POSTAL_CODE);
        }
        sheet.add(SimpleAddressGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.simpleaddress.phone"),
                  SimpleAddress.PHONE);
        sheet.add(SimpleAddressGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.simpleaddress.mobile"),
                  SimpleAddress.MOBILE);
        sheet.add(SimpleAddressGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.simpleaddress.fax"),
                  SimpleAddress.FAX);
        sheet.add(SimpleAddressGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.simpleaddress.email"),
                  SimpleAddress.EMAIL);

        sheet.add(SimpleAddressGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.simpleaddress.uri"),
                  SimpleAddress.URI);

        sheet.add(SimpleAddressGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.simpleaddress.notes"),
                  SimpleAddress.NOTES);

        return sheet;
    }
}
