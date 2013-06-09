/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.contenttypes.MOTDItem;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;


/**
 * Displays most recent MOTD
 *
 * @author <a href="mailto:scott@arsdigita.com">Scott Seago</a>
 * @version $Id: MOTDComponent.java 755 2005-09-02 13:42:47Z sskracic $
 *
 */

public class MOTDComponent extends SimpleComponent {

    public final static String BEBOP_XML_NS = 
        "http://www.arsdigita.com/bebop/1.0";
    
    public MOTDComponent() {
        super();
    }

    /**
     * Generate XML for the domain object supplied by the
     * selection model.
     */
    public void generateXML(PageState state, Element parent) {

        Element messages = new Element("bebop:motd", BEBOP_XML_NS);
        messages.addAttribute("id", getIdAttr());

        String categoryID = state.getRequest().getParameter("categoryID");
      
        if ( categoryID == null ) {
            try {
                DataCollection motds = 
                    SessionManager.getSession()
                                  .retrieve(MOTDItem.BASE_DATA_OBJECT_TYPE);
                motds.addFilter("version = 'live'");
                motds.addOrder("id desc");
              
                MOTDItem msg;

                while (motds.next()) {                    
                    
                    msg = new MOTDItem(motds.getDataObject());

                    Element aMsg = new Element("bebop:message", BEBOP_XML_NS);
                    aMsg.addAttribute("id", msg.getID().toString());
                    aMsg.addAttribute("title", msg.getTitle());
                    aMsg.addAttribute("message", msg.getMessage());
                    aMsg.addAttribute("pubDate", msg.getPublicationDate());

                    messages.addContent(aMsg);                    
                }
                parent.addContent(messages);

                motds.close();

            } catch (PersistenceException ex) {
                //
            }
        }
    }
}


