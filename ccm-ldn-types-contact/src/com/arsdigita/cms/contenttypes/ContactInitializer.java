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
package com.arsdigita.cms.contenttypes;

import java.util.ArrayList;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.contenttypes.ui.contact.AddContactPropertiesStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
// import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.runtime.DomainInitEvent;
// import com.arsdigita.runtime.LegacyInitEvent;

/**
 * Initializer class to initialize <code>ContentType Contact</code>.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ContactInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContactInitializer extends ContentTypeInitializer {

  public ContactInitializer() {
      super("ccm-ldn-types-contact.pdl.mf", Contact.BASE_DATA_OBJECT_TYPE);
  }

  public String[] getStylesheets() {
      return new String [] {
          "/static/content-types/com/arsdigita/cms/contenttypes/Contact.xsl",
      };
  }
  
  public String getTraversalXML() {
    return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Contact.xml";
  }

  private static ArrayList phoneTypesList = new ArrayList(10);
  
  static {
    phoneTypesList.add("Office");
    phoneTypesList.add("Mobile");
    phoneTypesList.add("Fax");
    phoneTypesList.add("Home");
    phoneTypesList.add("Minicom");
  }
  
  /**
   * Return the statically initialized list of phone types.
   */
    public static ArrayList getPhoneTypes(){
	return phoneTypesList;
    }
    
    // public void init(LegacyInitEvent evt) {
    public void init(DomainInitEvent evt) {
	super.init(evt);
	
	if (ContentSection.getConfig().getHasContactsAuthoringStep()) {

	    // Add the "Contact"authoring step
	    AuthoringKitWizard.registerAssetStep(
                    getBaseType(),
                    getAuthoringStep(), getAuthoringStepLabel(),
                    getAuthoringStepDescription(), getAuthoringStepSortKey());
	    
	    // and sort out the display	- at the moment this is just the
            // basic properties, addresses and phones
	    ContentItemTraversalAdapter associatedContactTraversalAdapter =
                                        new ContentItemTraversalAdapter();
	    associatedContactTraversalAdapter.addAssociationProperty("/object/phones");
	    associatedContactTraversalAdapter.addAssociationProperty("/object/contactAddress");
	    
	    ContentItemTraversalAdapter.registerAssetAdapter(
                    "associatedContactForItem",
                    associatedContactTraversalAdapter,
                    "com.arsdigita.cms.dispatcher.SimpleXMLGenerator");
	}
	
    }
    
    private int getAuthoringStepSortKey() {
	// TODO - workout what this does and possibly make it configurable
	return 1;
    }
    
    private GlobalizedMessage getAuthoringStepDescription() {
	return new GlobalizedMessage(
            "com.arsdigita.cms.contenttypes.contact_authoring_step_description",
            "com.arsdigita.cms.contenttypes.ContactResources");
    }
    
    private GlobalizedMessage getAuthoringStepLabel() {
	return new GlobalizedMessage(
                "com.arsdigita.cms.contenttypes.contact_authoring_step_label",
                "com.arsdigita.cms.contenttypes.ContactResources");
    }
    
    private Class getAuthoringStep() {
	return AddContactPropertiesStep.class;
    }
    
    private String getBaseType() {
	return ContentPage.BASE_DATA_OBJECT_TYPE;
    }
}
