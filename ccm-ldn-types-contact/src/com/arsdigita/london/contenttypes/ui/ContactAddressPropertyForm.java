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

import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.london.contenttypes.Contact;
import com.arsdigita.london.contenttypes.ContactAddress;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * A form class to edit the properties of <code>ContactAddress</code> object.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ContactAddressPropertyForm.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ContactAddressPropertyForm extends BasicItemForm {
  
  private static final Logger s_log = Logger.getLogger(
                                             ContactAddressPropertyForm.class);

  /** Name of this form */
  private static final String ID = "Contact_address_edit";
  
  /**
   * Creates a new form to edit the <code>ContactAddress</code> object properties 
   * associated with the <code>Contact</code> object specified by the
   * item selection model passed in.
   *
   * @param itemModel The ItemSelectionModel to use to obtain the Contact 
   *                  object to work on
   */
  public ContactAddressPropertyForm(ItemSelectionModel itemModel) {
    super(ID, itemModel);
  }

    /**
     * Adds widgets to edit the address properties to form.
     * Only paon and streetDesc are required, rest are optional.
     * This was cut down into small methods for subclasses to pick and choose.
     **/
    @Override
    protected void addWidgets() {
        addSAON();
        addPAON();
        addStreetDesc();
        addStreetRefNo();
        addLocality();
        addTown();
        addArea();
        addPostTown();
        addPostCode();
        addPropRefNo();
    }

    protected void addSAON() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.saon")));
        ParameterModel saonParam = new StringParameter(ContactAddress.SAON);
        TextField saon = new TextField(saonParam);
        add(saon);
    }

    protected void addPAON() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.paon")));
        ParameterModel paonParam = new StringParameter(ContactAddress.PAON);
        TextField paon = new TextField(paonParam);
        paon.addValidationListener(new NotNullValidationListener());
        add(paon);
    }
    
    protected void addStreetDesc() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.streetdesc")));
        ParameterModel streetDescParam = new StringParameter(ContactAddress.STREET_DESC);
        TextField streetDesc = new TextField(streetDescParam);
        streetDesc.addValidationListener(new NotNullValidationListener());
        add(streetDesc);
    }
    
    protected void addStreetRefNo() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.streetrefno")));
        ParameterModel streetRefNoParam = new StringParameter(ContactAddress.STREET_REF_NO);
        TextField streetRefNo = new TextField(streetRefNoParam);
        add(streetRefNo);
    }
    
    protected void addLocality() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.locality")));
        ParameterModel localityParam = new StringParameter(ContactAddress.LOCALITY);
        TextField locality = new TextField(localityParam);
        add(locality);
    }
    
    protected void addTown() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.town")));
        ParameterModel townParam = new StringParameter(ContactAddress.TOWN);
        TextField town = new TextField(townParam);
        add(town);
    }
    
    protected void addArea() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.administrative_area")));
        ParameterModel adAreaParam = new StringParameter(ContactAddress.ADMINISTRATIVE_AREA);
        TextField adArea = new TextField(adAreaParam);
        add(adArea);
    }
    
    protected void addPostTown() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.posttown")));
        ParameterModel postTownParam = new StringParameter(ContactAddress.POST_TOWN);
        TextField postTown = new TextField(postTownParam);
        add(postTown);
    }
    
    protected void addPostCode() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.postcode")));
        ParameterModel postCodeParam = new StringParameter(ContactAddress.POST_CODE);
        TextField postCode = new TextField(postCodeParam);
        add(postCode);
    }
    
    protected void addPropRefNo() {
        add(new Label(ContactGlobalizationUtil
                .globalize("london.contenttypes.ui.contact.address.proprefno")));
        ParameterModel propRefNoParam = new StringParameter(ContactAddress.PROP_REF_NO);
        TextField propRefNo = new TextField(propRefNoParam);
        add(propRefNo);
    }

  /**
   * Initialize Form values from Contact object.
   */
  public void init(FormSectionEvent fse) {

    FormData data = fse.getFormData();
    Contact contact = (Contact) this.getItemSelectionModel().getSelectedObject(fse.getPageState());
    ContactAddress ctAddress = contact.getContactAddress();

    if(ctAddress != null){

      data.put(ContactAddress.PAON, ctAddress.getPaon());
      data.put(ContactAddress.SAON, ctAddress.getSaon());
      data.put(ContactAddress.STREET_DESC, ctAddress.getStreetDesc());
      data.put(ContactAddress.STREET_REF_NO, ctAddress.getStreetRefNo());
      data.put(ContactAddress.LOCALITY, ctAddress.getLocality());
      data.put(ContactAddress.TOWN, ctAddress.getTown());
      data.put(ContactAddress.ADMINISTRATIVE_AREA, ctAddress.getAdministrativeArea());
      data.put(ContactAddress.POST_TOWN, ctAddress.getPostTown());
      data.put(ContactAddress.POST_CODE, ctAddress.getPostCode());
      data.put(ContactAddress.PROP_REF_NO, ctAddress.getReferenceNo());

    }
  }

  /**
   * Process the form submission event.
   * Create a new <code>ContactAddress</code> object and associate it with
   * <code>Contact</code> object if one does not exist yet.
   */
  public void process(FormSectionEvent fse) {

    FormData data = fse.getFormData();
    Contact contact = (Contact) this.getItemSelectionModel().getSelectedObject(fse.getPageState());
    s_log.debug("Process Event Object :" + contact);

    // save only if save button was pressed
    if (contact != null
      && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
        
      ContactAddress ctAddress = contact.getContactAddress();
      //User submitted the Form.If the associated ContactAddress does not exist
      //just create it.
      if(ctAddress == null){
        ctAddress = new ContactAddress();
        ctAddress.setName("address-for-contact-"+ contact.getID());
      }

      ctAddress.setPaon((String) data.get(ContactAddress.PAON));
      ctAddress.setSaon((String) data.get(ContactAddress.SAON));
      ctAddress.setStreetDesc((String) data.get(ContactAddress.STREET_DESC));
      ctAddress.setStreetRefNo((String) data.get(ContactAddress.STREET_REF_NO));

      ctAddress.setLocality((String) data.get(ContactAddress.LOCALITY));
      ctAddress.setTown((String) data.get(ContactAddress.TOWN));
      ctAddress.setAdministrativeArea((String) data.get(ContactAddress.ADMINISTRATIVE_AREA));
      ctAddress.setPostTown((String) data.get(ContactAddress.POST_TOWN));
      ctAddress.setPostCode((String) data.get(ContactAddress.POST_CODE));
      ctAddress.setReferenceNo((String) data.get(ContactAddress.PROP_REF_NO));

      ctAddress.save();

      //If not present then only set and save Contact object.
      if(contact.getContactAddress() == null){
        contact.setContactAddress(ctAddress);
        contact.save();
      }
    }
  }

}
