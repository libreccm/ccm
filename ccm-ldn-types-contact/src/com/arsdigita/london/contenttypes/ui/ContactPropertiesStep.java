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
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.london.contenttypes.Contact;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.bebop.PageState;

/**
 * Authoring kit step to view/edit basic properties of Contact object.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * 
 * @version $Id: ContactPropertiesStep.java 287 2005-02-22 00:29:02Z sskracic $
 *  
 */
public class ContactPropertiesStep extends SimpleEditStep {

  /** The name of the editing sheet added to this step */
  public static String EDIT_SHEET_NAME = "edit";

  public ContactPropertiesStep(ItemSelectionModel itemModel,
                               AuthoringKitWizard parent) {
    super(itemModel, parent);

    BasicPageForm editSheet;

    editSheet = new ContactPropertyForm(itemModel);
    add(EDIT_SHEET_NAME,
        GlobalizationUtil.globalize("cms.ui.edit"), 
        new WorkflowLockedComponentAccess(editSheet, itemModel),
        editSheet.getSaveCancelSection().getCancelButton());

    setDisplayComponent(getContactPropertySheet(itemModel));
  }

  /**
   * Returns a component that displays the properties of the Contact specified
   * by the ItemSelectionModel passed in.
   * 
   * @param itemModel
   *          The ItemSelectionModel to use @pre itemModel != null
   * @return A component to display the state of the basic properties of the
   */
  public static Component getContactPropertySheet(ItemSelectionModel itemModel) {
    DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

    sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"),
              Contact.TITLE);

    sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"),
              Contact.NAME);

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.givenname"),
              Contact.GIVEN_NAME);

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.familyname"),
              Contact.FAMILY_NAME);
    
    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.suffix"),
              Contact.SUFFIX);

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.type"),
              Contact.CONTACT_TYPE,
              new DomainObjectPropertySheet.AttributeFormatter() {
                  public String format(DomainObject item,
                                       String attribute,
                                       PageState state) {
                      Contact contact = (Contact) item;
                      if (contact.getContactType() != null) {
                          return contact.getContactTypeName();
                      } else {
                          return (String) GlobalizationUtil
                                          .globalize("cms.ui.unknown")
                                          .localize();
                      }
                  }
              });
    
    sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.description"),
              Contact.DESCRIPTION);

    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.emails"),
              Contact.EMAILS);
    
    sheet.add(ContactGlobalizationUtil.globalize(
              "london.contenttypes.ui.contact.orgname"),
              Contact.ORG_NAME);
    
    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.deptname"),
              Contact.DEPT_NAME);
    
    sheet.add(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.role"),
              Contact.ROLE);

    return sheet;
  }

}
