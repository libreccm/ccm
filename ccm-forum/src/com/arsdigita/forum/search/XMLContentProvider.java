/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum.search;


import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

public class XMLContentProvider implements ContentProvider, Constants {

    private static final Logger s_log = Logger
        .getLogger(XMLContentProvider.class);

	private Post m_post;
    private String m_context;

    public XMLContentProvider(String context, Post post) {
        m_context = context;
        m_post = post;
    }

    public String getContext() {
        return m_context;
    }

    public ContentType getType() {
        return ContentType.XML;
    }

    public byte[] getBytes() {
		
        
        Element root = new Element("forum:post", FORUM_XML_NS);
        DomainObjectXMLRenderer renderer =
            new DomainObjectXMLRenderer(root);

        renderer.setWrapAttributes(true);
        renderer.walk(m_post, PostMetadataProvider.class.getName());

        Document doc = null;
        try {
            doc = new Document(root);
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
             throw new UncheckedWrapperException("Unable to create xml document for post " + m_post.getID(),  ex);
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("XML is " + doc.toString(true));
        }

        
        String xml = doc.toString(true);
        return xml.getBytes();
    }

}
