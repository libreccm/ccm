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
package com.arsdigita.xmlutil;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.JDOMFactory;

/**
 * Document subtype for XML tests. Right now, only used to store a reference to the JDOMFactory
 * used to construct the document. This allows child elements with import instructions to access the
 * correct JDOMFactory to build child documents.
 */
public class TestDocument extends Document {
    public TestDocument(Element rootElement, JDOMFactory factory) {
        super(rootElement);
        m_factory = factory;
    }

    public TestDocument(Element rootElement, DocType docType, JDOMFactory factory) {
        super(rootElement, docType);
        m_factory = factory;
    }

    public JDOMFactory getFactory() {
        return m_factory;
    }


    private JDOMFactory m_factory;
}
