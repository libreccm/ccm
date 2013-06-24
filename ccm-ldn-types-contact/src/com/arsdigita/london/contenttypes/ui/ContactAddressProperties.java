/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.london.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring kit step to manipulate <code>ContactAddress</code> object
 * associated with <code>Contact</code> object.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ContactAddressProperties.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContactAddressProperties extends SimpleEditStep {

  /** The name of the editing sheet added to this step */
  public static String EDIT_SHEET_NAME = "edit-contact-address";

  public ContactAddressProperties(ItemSelectionModel itemModel,
                                  AuthoringKitWizard parent) {
    super(itemModel, parent);

    BasicItemForm form;

    form = new ContactAddressPropertyForm(itemModel);

    add(EDIT_SHEET_NAME,
        GlobalizationUtil.globalize("cms.ui.edit"), 
        new WorkflowLockedComponentAccess(form, itemModel),
        form.getSaveCancelSection().getCancelButton());

    setDisplayComponent(getAddressPropertySheet(itemModel));
  }

  /**
   * Returns a component that displays the properties of the Contact
   * specified by the ItemSelectionModel passed in.
   *
   * @param itemModel The ItemSelectionModel to use
   * @pre itemModel != null
   * @return A component to display the state of the basic properties
   *  of the
   **/
  public static Component getAddressPropertySheet(ItemSelectionModel itemModel) {
    DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.saon"),
              "contactAddress.saon");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.paon"),
              "contactAddress.paon");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.streetdesc"),
              "contactAddress.streetDesc");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.streetrefno"),
              "contactAddress.streetRefNo");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.locality"),
              "contactAddress.locality");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.town"),
              "contactAddress.town");
      
    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.administrative_area"),
              "contactAddress.administrativeArea");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.posttown"),
              "contactAddress.postTown");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.postcode"),
              "contactAddress.postCode");

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.address.proprefno"),
              "contactAddress.referenceNo");

    return sheet;
  }

}
