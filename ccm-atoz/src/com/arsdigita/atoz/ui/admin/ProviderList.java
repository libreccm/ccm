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

package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;

import com.arsdigita.persistence.OID;

import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainCollection;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.xml.Element;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.IOException;

import org.apache.log4j.Logger;

public class ProviderList extends SimpleContainer {

    private static final Logger s_log = Logger.getLogger(ProviderList.class);

    private static final String XMLNS =
        "http://xmlns.redhat.com/atoz/1.0";

    private static final String EDIT = "edit";
    private static final String DELETE = "delete";

    private ACSObjectSelectionModel m_provider;

    public ProviderList(ACSObjectSelectionModel provider) {
        super("atoz:providerList", XMLNS);

        m_provider = provider;
    }
    
    public void respond(PageState state) {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();
        
        if (EDIT.equals(key)) {
            AtoZProvider provider = (AtoZProvider)DomainObjectFactory
                .newInstance(OID.valueOf(value));
            m_provider.setSelectedObject(state, provider);
        } else if (DELETE.equals(key)) {
            AtoZProvider provider = (AtoZProvider)DomainObjectFactory
                .newInstance(OID.valueOf(value));
            provider.delete();
            m_provider.clearSelection(state);
        } else {
            s_log.warn("Unknown control event " + key + ":" + value);
        }
    }

    public void generateXML(PageState state,
                            Element parent) {
        Element content = generateParent(parent);
        
        AtoZ atoz = (AtoZ)Kernel.getContext().getResource();
        DomainCollection providers = atoz.getProviders();
        while (providers.next()) {
            AtoZProvider provider = (AtoZProvider)providers.getDomainObject();
            
            Element providerEl = content.newChildElement("atoz:provider", XMLNS);
            
            try {
                state.setControlEvent(this, DELETE, provider.getOID().toString());
                providerEl.addAttribute("deleteURL", state.stateAsURL());
                state.setControlEvent(this, EDIT, provider.getOID().toString());
                providerEl.addAttribute("editURL", state.stateAsURL());
                state.clearControlEvent();
            } catch (IOException ex) {
                throw new UncheckedWrapperException("damn", ex);
            }

            if (provider.equals(m_provider.getSelectedObject(state))) {
                providerEl.addAttribute("isSelected", "yes");
            }

            DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(providerEl);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);
            
            xr.walk(provider, ProviderList.class.getName());
        }
    }
}
