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

import java.io.IOException;

import javax.servlet.ServletException;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZCategoryProvider;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

public class CategoryProviderContentTypeBlacklist extends SimpleContainer {

    private ACSObjectSelectionModel m_provider;

    private static final String DELETE = "delete";

    public CategoryProviderContentTypeBlacklist(ACSObjectSelectionModel provider) {
        m_provider = provider;
    }

    public void respond(PageState state) throws ServletException {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();

        if (DELETE.equals(key)) {
            ContentType contentType = (ContentType) DomainObjectFactory
                    .newInstance(OID.valueOf(value));

            AtoZCategoryProvider provider = (AtoZCategoryProvider) m_provider
                    .getSelectedObject(state);
            provider.removeContentTypeBlock(contentType);
        }
    }

    public void generateXML(PageState state, Element parent) {
        Element content = AtoZ
                .newElement("categoryProviderContentTypeBlackList");
        exportAttributes(content);

        AtoZCategoryProvider provider = (AtoZCategoryProvider) m_provider
                .getSelectedObject(state);

        DomainCollection entries = provider.getContentTypeBlackList();
        while (entries.next()) {
            Element el = AtoZ.newElement("categoryProviderContentTypeBlock");

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
                    CategoryProviderContentTypeBlacklist.class.getName());

            content.addContent(el);
        }

        parent.addContent(content);
    }

}
