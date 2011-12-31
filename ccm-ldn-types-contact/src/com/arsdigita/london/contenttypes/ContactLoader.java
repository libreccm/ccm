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
package com.arsdigita.london.contenttypes;

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;
import com.arsdigita.runtime.ScriptContext;

/**
 * Loader for <code>ContentType</code> <code>Contact</code>.
 * 
 * Also loads a fixed set of <code>ContactType</code> objects.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ContactLoader.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ContactLoader extends AbstractContentTypeLoader {

  private static final String[] TYPES = {
      "/WEB-INF/content-types/com/arsdigita/london/contenttypes/Contact.xml"
  };

  /**
   * @see com.arsdigita.cms.contenttypes.AbstractContentTypeLoader#getTypes()
   */
  public String[] getTypes() {
      return TYPES;
  }
  
  /**
   * @see com.arsdigita.runtime.Script#run(com.arsdigita.runtime.ScriptContext)
   */
    @Override
  public void run(ScriptContext context) {
    super.run(context);
    loadContactTypes();
  }

  /** 
   * Create the fixed set of ContactTypes.
   * This is a quick hack for now.If we need to extend it then we have
   * to move it into an XML file and load it from initializer after 
   * checking it. 
   */
  protected void loadContactTypes(){
    ContactType ct = new ContactType();
    ct.setName("ContactType-Service-Provision");
    ct.setTypeName("Service Provision");
    ct.save();

    ct = new ContactType();
    ct.setName("ContactType-Enquiry");
    ct.setTypeName("Enquiry");
    ct.save(); 

    ct = new ContactType();
    ct.setName("ContactType-Complaint");
    ct.setTypeName("Complaint");
    ct.save();

    ct = new ContactType();
    ct.setName("ContactType-Escalation");
    ct.setTypeName("Escalation");
    ct.save();

  }

}