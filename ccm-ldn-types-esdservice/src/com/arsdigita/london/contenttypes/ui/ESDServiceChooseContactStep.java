/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.london.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.london.contenttypes.util.ESDServiceGlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring kit step to manipulate the associated Contact object for 
 * <code>ESDService</code> content type.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ESDServiceChooseContactStep.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ESDServiceChooseContactStep extends SimpleEditStep {

  /** The name of the editing sheet added to this step */
  private static String EDIT_SHEET_NAME = "edit";
  
  /**
   * @param itemModel
   * @param parent
   */
  public ESDServiceChooseContactStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
    super(itemModel, parent);

    //Table to display the List of Contacts to choose from.
    ESDServiceContactsTable table = new ESDServiceContactsTable(itemModel , this);
    
    addComponent(EDIT_SHEET_NAME,
                 ESDServiceGlobalizationUtil.globalize(
                 "london.contenttypes.ui.esdservice.select_contact"),
                 new WorkflowLockedComponentAccess(table, itemModel));

    setDisplayComponent(getContactPropertiesSheet(itemModel));
  }

  /**
   * Create a Component to display the properties of Contact object.It only
   * displays limited attributes at the moment, but can be extended to add
   * addtional ones if needed.
   */
  private Component getContactPropertiesSheet(ItemSelectionModel itemModel) {
    DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

    sheet.add(ContactGlobalizationUtil.globalize(
                                      "london.contenttypes.ui.contact.givenname"),
              "serviceContact.givenName");

    sheet.add(ContactGlobalizationUtil.globalize(
                                       "london.contenttypes.ui.contact.familyname"),
              "serviceContact.familyName");

    sheet.add(ContactGlobalizationUtil.globalize(
                                       "london.contenttypes.ui.contact.type"),
              "serviceContact.contactType.typeName");
    
    return sheet;
  }
}
