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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DefaultJDOMFactory;

public abstract class TestJDOMFactory extends DefaultJDOMFactory {
    public TestJDOMFactory() throws JDOMException {
        super();
        m_types.put( Namespaces.TEST.getPrefix() + ":" + TestSet.NAME, TestSet.class);
        m_types.put( Namespaces.TEST.getPrefix() + ":" + TestDefinition.NAME, TestDefinition.class);
        System.out.println("Mapping");
        for (Iterator iterator = m_types.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            System.out.println("Mapping: " + entry.getKey() + " to " + entry.getValue());
        }

        DocImporter importer = new DocImporter(true);

        Document doc;
        try {
            doc = importer.getDocumentAsFile(getTypeDefs());

        } catch(JDOMException e) {
            throw new JDOMException("Error importing " + getTypeDefs(), e);
        }

        addTypes(doc);
        System.out.println("Mapping, take2");
        for (Iterator iterator = m_types.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            System.out.println("Mapping: " + entry.getKey() + " to " + entry.getValue());
        }
    }

    private void addTypes(Document doc) throws XMLException {
         List types = doc.getRootElement().getChildren();
        for (Iterator iterator = types.iterator(); iterator.hasNext();) {
            Element typeMap = (Element) iterator.next();
            String elementName = typeMap.getAttributeValue("element");
            String className = typeMap.getAttributeValue("class");
            addElementMapping(className, elementName);
        }
    }

    private void addElementMapping(String className, String elementName) throws XMLException {
        try {
            Class theClass = Class.forName(className);
            m_types.put( elementName, theClass );

        } catch(ClassNotFoundException ce) {
            throw new XMLException("Invalid class " + className + " mapped to element " + elementName,
                    ce);
        }
    }

    public abstract String getTypeDefs();

    public Document document(Element element) {
        return new TestDocument(element, this);
    }

    public Document document(Element element, DocType type) {
        return new TestDocument(element, type, this);
    }

    public Element element(String name) {
        Element e = makeSpecificElement(name);
        if (null == e) {
            e = super.element(name);
        }
        return e;
    }

    public Element element(String name, Namespace ns) {
        Element e = makeSpecificElement(name, ns.getPrefix());
        if (null == e) {
            e = super.element(name, ns);
        }
        return e;
    }
    public Element element(String name, String uri) {
        Element e = makeSpecificElement(name, Namespace.getNamespace(uri).getPrefix());
        if (null == e) {
            e = super.element(name, uri);
        }
        return e;
    }
    public Element element(String name, String prefix, String uri) {
        Element e = makeSpecificElement(name, prefix);
        if (null == e) {
            e = super.element(name, prefix, uri);
        }
        return e;
    }

    private Element makeSpecificElement(String name, String prefix) {
        String qualifiedName = prefix + ":" + name;
        Element elem = makeSpecificElement(qualifiedName);

        return elem;
    }

    private Element makeSpecificElement(String qualifiedName) {
        Element elem = null;
//       System.out.println("looking for: " + qualifiedName);
        Class specificType = (Class) m_types.get(qualifiedName);
        if (specificType != null) {
            try {
                elem = (Element) specificType.newInstance();
            } catch(Exception e) {
                String msg = "Unable to create specific type: " +
                        specificType + " for element: " + qualifiedName +
                        ". Creating org.jdom.Element instead.";
                s_log.error(msg, e);
            }

        }
        return elem;
    }

    // Map of element type names to the java.lang.Class representing that xml Element, Attribute, etc..
    // IMPORTANT: These types _MUST_ provide a no-arg constructor. Note that JDOM types do not presently do this.
    // This requirement might change as other types are added.
    private Map m_types = new HashMap();
    private static final Logger s_log =
            Logger.getLogger(TestJDOMFactory.class.getName());

}
