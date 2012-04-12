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

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZCategoryAlias;
import com.arsdigita.atoz.AtoZCategoryProvider;

import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.categorization.Category;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;


public class CategoryProviderAliasList extends SimpleContainer {
    
    private ACSObjectSelectionModel m_provider;
    private static final String DELETE = "delete";

    public CategoryProviderAliasList(ACSObjectSelectionModel provider) {
        m_provider = provider;
    }

    public void respond(PageState state) {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();
        
        if (DELETE.equals(key)) {
            AtoZCategoryAlias alias = (AtoZCategoryAlias)DomainObjectFactory
                .newInstance(OID.valueOf(value));

            AtoZCategoryProvider provider = (AtoZCategoryProvider)
                m_provider.getSelectedObject(state);
            provider.removeAlias(alias);
        }
    }

    public void generateXML(PageState state,
                            Element parent) {
        Element content = AtoZ.newElement("categoryProviderAliasList");
        exportAttributes(content);
        
        AtoZCategoryProvider provider = (AtoZCategoryProvider)
            m_provider.getSelectedObject(state);
        
        DomainCollection entries = provider.getAliases();
        while (entries.next()) {
        	AtoZCategoryAlias alias = (AtoZCategoryAlias)entries.getDomainObject();
            Element el = AtoZ.newElement("categoryProviderAlias");
            el.addAttribute("letter", XML.format(alias.getLetter()));
            el.addAttribute("title", XML.format(alias.getTitle()));

            Element elName = new Element("name");
            elName.setText(alias.getCategory().getName());
            el.addContent(elName);

            try {
                state.setControlEvent(this, DELETE, entries.getDomainObject()
                                      .getOID().toString());
                el.addAttribute("deleteURL", state.stateAsURL());
                state.clearControlEvent();
            } catch (IOException ex) {
                throw new UncheckedWrapperException("damn", ex);
            }

            DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(el);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);

            xr.walk(entries.getDomainObject(),
                    CategoryProviderAliasList.class.getName());

            content.addContent(el);
        }
        
        parent.addContent(content);
    }
    
}
