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
package com.arsdigita.cms.search;


import com.arsdigita.cms.CMS;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.ContentProvider;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Logger;

public class XMLContentProvider implements ContentProvider {

    private static final Logger s_log = Logger
        .getLogger(XMLContentProvider.class);

    private DomainObject m_obj;
    private String m_context;
    private String m_adapterContext;

    public XMLContentProvider(String context,
                              DomainObject obj,
                              String adapterContext) {
        m_context = context;
        m_obj = obj;
        m_adapterContext = adapterContext;
    }

    public String getContext() {
        return m_context;
    }

    public ContentType getType() {
        return ContentType.XML;
    }

    public byte[] getBytes() {
        
        Element root = new Element("cms:item", CMS.CMS_XML_NS);
        DomainObjectXMLRenderer renderer =
            new DomainObjectXMLRenderer(root);

        renderer.setWrapAttributes(true);
        renderer.walk(m_obj, m_adapterContext);

        Document doc = null;
        try {
            doc = new Document(root);
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            final String message =
                (String) GlobalizationUtil.globalize
                    ("cms.cannot_create_xml_document").localize();
            throw new UncheckedWrapperException(message,  ex);
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("XML is " + doc.toString(true));
        }

        // Hmm, why on earth doesn't this method return
        // Element directly ?!?!
        String xml = doc.toString(true);
        return xml.getBytes();
    }

}
