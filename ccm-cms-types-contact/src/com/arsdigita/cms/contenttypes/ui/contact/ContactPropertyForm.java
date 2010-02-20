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

import java.math.BigDecimal;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Contact;
import com.arsdigita.cms.contenttypes.ContactType;
import com.arsdigita.cms.contenttypes.ContactTypesCollection;
import com.arsdigita.cms.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * Form to edit basic properties of <code>Contact</code> object. Used by
 * <code>ContactPropertiesStep</code> authoring kit step.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ContactPropertyForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContactPropertyForm extends BasicPageForm {

  /** Name of this form */
  public static final String ID = "Contact_edit";

  /**
   * Creates a new form to edit the Contact object specified by the item
   * selection model passed in.
   * 
   * @param itemModel
   *          The ItemSelectionModel to use to obtain the Contact to work on
   */
  public ContactPropertyForm(ItemSelectionModel itemModel) {
    super(ID, itemModel);
  }

    /**
     * Adds widgets to the form. This has been cut into small methods
     * so that subclasses can pick and choose.
     */
    protected void addWidgets() {
        addBasicPageFormWidgets();
        addGivenNameWidget();
        addFamilyNameWidget();
        addSuffixWidget();
        addContactTypeWidget();
        addDescriptionWidget();
        addEmailsWidget();
        addOrganizationNameWidget();
        addDepartmentNameWidget();
        addRoleWidget();
    }

    protected void addBasicPageFormWidgets() {
        super.addWidgets();
    }

    protected void addGivenNameWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_givenname")));
        ParameterModel givenNameParam = new StringParameter(Contact.GIVEN_NAME);
        TextField givenName = new TextField(givenNameParam);
        add(givenName);
    }

    protected void addFamilyNameWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_familyname")));
        ParameterModel familyNameParam = new StringParameter(Contact.FAMILY_NAME);
        TextField familyName = new TextField(familyNameParam);
        add(familyName);
    }
    
    protected void addSuffixWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_suffix")));
        ParameterModel suffixParam = new StringParameter(Contact.SUFFIX);
        TextField suffix = new TextField(suffixParam);
        add(suffix);
    }
    
    protected void addContactTypeWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_type")));
        ParameterModel contactTypeParam = new StringParameter(Contact.CONTACT_TYPE);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        add(contactType);
        // retrieve Contact types
        ContactTypesCollection ctTypes = ContactTypesCollection.getContactTypesCollection();
        while (ctTypes.next()) {
            contactType.addOption(new Option(ctTypes.getContactTypeID().toString(), ctTypes.getContactTypeName()));
        }
    }

    protected void addDescriptionWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_description")));
        ParameterModel descParam = new StringParameter(Contact.DESCRIPTION);
        TextArea desc = new TextArea(descParam);
        desc.setRows(5);
        add(desc);
    }
    
    protected void addEmailsWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_emails")));
        ParameterModel emailsParam = new StringParameter(Contact.EMAILS);
        TextField emails = new TextField(emailsParam);
        add(emails);
    }
    
    protected void addOrganizationNameWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_orgname")));
        ParameterModel orgNameParam = new StringParameter(Contact.ORG_NAME);
        TextField orgName = new TextField(orgNameParam);
        add(orgName);
    }
    
    protected void addDepartmentNameWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_deptname")));
        ParameterModel deptParam = new StringParameter(Contact.DEPT_NAME);
        TextField deptName = new TextField(deptParam);
        add(deptName);
    }
    
    protected void addRoleWidget() {
        add(new Label(ContactGlobalizationUtil.globalize("cms.contenttypes.ui.contact_role")));
        ParameterModel roleParam = new StringParameter(Contact.ROLE);
        TextField role = new TextField(roleParam);
        add(role);        
    }

  /**
   * Initialize Form values from Contact object.
   */
  public void init(FormSectionEvent fse) {

    FormData data = fse.getFormData();
    Contact contact = (Contact) super.initBasicWidgets(fse);

    if (contact.getContactType() != null) {
      data.put(Contact.CONTACT_TYPE, contact.getContactType().getID());
    }

    data.put(Contact.GIVEN_NAME, contact.getGivenName());
    data.put(Contact.FAMILY_NAME, contact.getFamilyName());
    data.put(Contact.SUFFIX, contact.getSuffix());
    data.put(Contact.EMAILS, contact.getEmails());
    data.put(Contact.DESCRIPTION, contact.getDescription());
    data.put(Contact.ORG_NAME, contact.getOrganisationName());
    data.put(Contact.DEPT_NAME, contact.getDeptName());
    data.put(Contact.ROLE, contact.getRole());
  }

  /**
   * Process the form submission event.
   */
  public void process(FormSectionEvent fse) {

    FormData data = fse.getFormData();
    Contact contact = (Contact) super.processBasicWidgets(fse);

    // save only if save button was pressed
    if (contact != null
      && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

      contact.setGivenName((String) data.get(Contact.GIVEN_NAME));
      contact.setFamilyName((String) data.get(Contact.FAMILY_NAME));
      contact.setSuffix((String) data.get(Contact.SUFFIX));
      contact.setEmails((String) data.get(Contact.EMAILS));
      String contactTypeIDStr = (String) data.get(Contact.CONTACT_TYPE);
      if (contactTypeIDStr != null) {
	  BigDecimal contactTypeID =
	      new BigDecimal(contactTypeIDStr);
	  ContactType ctType = new ContactType(contactTypeID);
	  contact.setContactType(ctType);
      }
      
      contact.setDescription((String) data.get(Contact.DESCRIPTION));
      contact.setOrganisationName((String) data.get(Contact.ORG_NAME));
      contact.setDeptName((String) data.get(Contact.DEPT_NAME));
      contact.setRole((String) data.get(Contact.ROLE));
      contact.save();
    }
  }
}
