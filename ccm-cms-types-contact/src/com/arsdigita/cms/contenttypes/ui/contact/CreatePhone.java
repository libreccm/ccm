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
package com.arsdigita.cms.contenttypes.ui.contact;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Contact;
import com.arsdigita.cms.contenttypes.ContactInitializer;
import com.arsdigita.cms.contenttypes.ContactPhone;
import com.arsdigita.cms.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

import java.util.ArrayList;

/**
 * Form to Create objects of type <code>ContactPhone</code>.
 * 
 * @author Shashin Shinde <sshinde@redhat.com>
 *
 * @version $Id: CreatePhone.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
class CreatePhone extends BasicItemForm {

  private ItemSelectionModel m_model;

  /**
   * Creates a new form to create the <code>ContactPhone</code> object
   * type associated with the <code>Contact</code> object specified
   * by the item selection model passed in.
   * 
   * @param itemModel
   *          The ItemSelectionModel to use to obtain the Contact object to
   *          work on
   */
  public CreatePhone(ItemSelectionModel itemModel) {
    super("Contact_phone_create", itemModel);
    m_model = itemModel;
  }

  /**
   * Adds widgets to edit the address properties to form. Only paon and
   * streetDesc are required, rest are optional.
   */
  protected void addWidgets() {

    add(new Label(ContactGlobalizationUtil.globalize(
    "cms.contenttypes.ui.contact.phone_number")));
    ParameterModel phoneNoParam = new StringParameter(ContactPhone.PHONE_NUMBER);
    TextField phoneNo = new TextField(phoneNoParam);
    phoneNo.addValidationListener(new NotNullValidationListener());
    add(phoneNo);

    add(new Label(ContactGlobalizationUtil.globalize(
    "cms.contenttypes.ui.phone_type")));
    ParameterModel phoneTypeParam = new StringParameter(ContactPhone.PHONE_TYPE);
    SingleSelect phoneType = new SingleSelect(phoneTypeParam);
    add(phoneType);
    // retrieve static Phone types
    ArrayList phTypes = ContactInitializer.getPhoneTypes();
    for (int i = 0; i < phTypes.size(); i++) {
      phoneType.addOption(
          new Option(phTypes.get(i).toString(), phTypes.get(i).toString()));
    }
  }

  /**
   */
  public void init(FormSectionEvent e) {
    //Do nothing.
  }

  /**
   * Create an object of type <code>ContactPhone</code> and add it to Contact
   * object retrieved from passed in ItemSelectionModel.
   */
  public void process(FormSectionEvent fse) {
    FormData data = fse.getFormData();
    Contact contact = (Contact) m_model.getSelectedObject(fse.getPageState());

    // save only if save button was pressed
    if (contact != null
        && getSaveCancelSection().getSaveButton().isSelected(
            fse.getPageState())) {

      ContactPhone cph = new ContactPhone();
      cph.setName("phone-for-contact-" + contact.getID());

      cph.setPhoneType((String) data.get(ContactPhone.PHONE_TYPE));
      cph.setPhoneNumber((String) data.get(ContactPhone.PHONE_NUMBER));
      cph.save();

      //Add it to the contact.
      contact.addPhone(cph);
    }
  }
}
