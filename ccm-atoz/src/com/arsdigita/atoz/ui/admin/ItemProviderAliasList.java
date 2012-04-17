/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZItemAlias;
import com.arsdigita.atoz.AtoZItemProvider;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import java.io.IOException;

public class ItemProviderAliasList extends SimpleContainer {
    
    private ACSObjectSelectionModel m_provider;
    private static final String DELETE = "delete";

    public ItemProviderAliasList(ACSObjectSelectionModel provider) {
        m_provider = provider;
    }

    @Override
    public void respond(PageState state) {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();
        
        if (DELETE.equals(key)) {
            AtoZItemAlias alias = (AtoZItemAlias) DomainObjectFactory
                .newInstance(OID.valueOf(value));
	    alias.delete();
        }
    }

    @Override
    public void generateXML(PageState state,
                            Element parent) {
        Element content = AtoZ.newElement("itemProviderAliasList");
        exportAttributes(content);
        
        AtoZItemProvider provider = (AtoZItemProvider)
            m_provider.getSelectedObject(state);
        
        DomainCollection entries = provider.getAliases();
        while (entries.next()) {
	    AtoZItemAlias alias = (AtoZItemAlias) entries.getDomainObject();

            Element el = AtoZ.newElement("itemProviderAlias");
            el.addAttribute("letter", XML.format(entries.get("letter")));
            el.addAttribute("title", XML.format(entries.get("title")));
            el.addAttribute("itemName", XML.format(alias.getContentItem().getDisplayName()));

            try {
                state.setControlEvent(this, DELETE, alias.getOID().toString());
                el.addAttribute("deleteURL", state.stateAsURL());
                state.clearControlEvent();
            } catch (IOException ex) {
                throw new UncheckedWrapperException("Could not add delete link.", ex);
            }

            DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(el);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);

            xr.walk(entries.getDomainObject(),
                    ItemProviderAliasList.class.getName());

            content.addContent(el);
        }
        
        parent.addContent(content);
    }    
}
