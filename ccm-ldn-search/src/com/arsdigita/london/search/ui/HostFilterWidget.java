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

package com.arsdigita.london.search.ui;

import com.arsdigita.search.ui.FilterWidget;
import com.arsdigita.search.Search;
import com.arsdigita.search.FilterSpecification;

import com.arsdigita.london.search.HostFilterType;
import com.arsdigita.london.search.HostFilterSpecification;
import com.arsdigita.london.search.Server;
import com.arsdigita.london.search.ServerCollection;

import com.arsdigita.xml.Element;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ArrayParameter;

import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;


public class HostFilterWidget extends FilterWidget {

    public HostFilterWidget() {
        super(new HostFilterType(),
              new ArrayParameter(new OIDParameter(HostFilterType.KEY)));
    }
    
    public FilterSpecification getFilter(PageState state) {        
        return new HostFilterSpecification(getServerIDs(state));
    }

    protected OID[] getServerIDs(PageState state) {
        OID[] servers = (OID[])getValue(state);
        
        if (servers == null) {
            servers = new OID[0];
        }
        return servers;
    }
    
    public void generateBodyXML(PageState state,
                                Element parent) {
        super.generateBodyXML(state, parent);

        OID[] serverIDs = getServerIDs(state);

        ServerCollection servers = Server.retrieveAll();
        while (servers.next()) {
            Server server = servers.getServer();

            Element type = Search.newElement("searchHost");
            type.addAttribute("oid", server.getOID().toString());
            type.addAttribute("title", server.getTitle());
            
            for (int j = 0 ; j < serverIDs.length ; j++) {
                if (serverIDs[j].equals(server.getOID())) {
                    type.addAttribute("isSelected", "1");
                    break;
                }
            }

            parent.addContent(type);
        }
    }
}
