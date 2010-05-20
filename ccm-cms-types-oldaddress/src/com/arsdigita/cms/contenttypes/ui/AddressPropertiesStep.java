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
import com.arsdigita.cms.contenttypes.Address;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.util.AddressGlobalizationUtil;

import java.text.DateFormat;

/**
 * Authoring step to edit the simple attributes of the Address content type (and
 * its subclasses). This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 *
 * @author <a href="mailto:dominik@redhat.com">Dominik Kacprzak</a>
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 *
 **/
public class AddressPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    public AddressPropertiesStep(ItemSelectionModel itemModel,
                                 AuthoringKitWizard parent ) {
        super(itemModel, parent );

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new AddressPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", 
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent(getAddressPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the Address specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the address content type.
     **/
    public static Component getAddressPropertySheet(ItemSelectionModel
                                                   itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add( (String) AddressGlobalizationUtil.globalize
                   ("cms.contenttypes.ui.name").localize(),  
                   Address.NAME );
        sheet.add( (String) AddressGlobalizationUtil.globalize
                   ("cms.contenttypes.ui.title").localize(),  
                   Address.TITLE);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
           sheet.add( (String) AddressGlobalizationUtil.globalize
                      ("cms.ui.authoring.page_launch_date").localize(),
                     ContentPage.LAUNCH_DATE,
                     new DomainObjectPropertySheet.AttributeFormatter() {
                         public String format(DomainObject item,
                                              String attribute,
                                              PageState state) {
                             ContentPage page = (ContentPage) item;
                             if (page.getLaunchDate() != null) {
                                 return DateFormat.getDateInstance(DateFormat.LONG)
                                     .format(page.getLaunchDate());
                             } else {
                                 return (String)AddressGlobalizationUtil.globalize
                                        ("cms.ui.unknown").localize();
                             }
                         }
                     });
        }
        sheet.add( (String) AddressGlobalizationUtil.globalize
                   ("cms.contenttypes.ui.address.address").localize(), 
                   Address.ADDRESS);
        if (!Address.getConfig().getHideCountryCodeSelection()) { 
            sheet.add( (String) AddressGlobalizationUtil.globalize
                       ("cms.contenttypes.ui.address.iso_country_code").localize(),
                       Address.ISO_COUNTRY_CODE,
                       new DomainObjectPropertySheet.AttributeFormatter() {
                           public String format( DomainObject item,
                                                 String attribute,
                                                 PageState state ) {
                              Address address = ( Address ) item;
                              if ( address.getCountryIsoCode() != null ) {
                                  return address.getCountryIsoCode();
                              } else {
                                  return (String)AddressGlobalizationUtil.globalize
                                         ("cms.ui.unknown").localize();
                              }
                           }
                      });
        }
        if (!Address.getConfig().getHidePostalCode()) {
            sheet.add( (String) AddressGlobalizationUtil.globalize
                       ("cms.contenttypes.ui.address.postal_code").localize(), 
                       Address.POSTAL_CODE);
        }
        sheet.add((String) AddressGlobalizationUtil
                  .globalize("cms.contenttypes.ui.address.phone").localize(), 
                  Address.PHONE);
        sheet.add((String) AddressGlobalizationUtil.globalize
                  ("cms.contenttypes.ui.address.mobile").localize(), 
                  Address.MOBILE);
        sheet.add((String) AddressGlobalizationUtil.globalize
                  ("cms.contenttypes.ui.address.fax").localize(), 
                  Address.FAX);
        sheet.add((String) AddressGlobalizationUtil.globalize
                  ("cms.contenttypes.ui.address.email").localize(), 
                  Address.EMAIL);

        sheet.add((String) AddressGlobalizationUtil.globalize
                  ("cms.contenttypes.ui.address.uri").localize(), 
                  Address.URI);

        sheet.add((String) AddressGlobalizationUtil.globalize
                  ("cms.contenttypes.ui.address.notes").localize(), 
                  Address.NOTES);

        return sheet;
    }
}
