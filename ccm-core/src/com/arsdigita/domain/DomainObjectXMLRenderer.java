/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.domain;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.xml.Element;

import java.util.Calendar;
import java.util.Date;
import com.arsdigita.xml.XML;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.Stack;

import java.util.Map;
import java.util.HashMap;

import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * An implementation of DomainObjectTraversal that generates an XML
 * tree representing the DomainObject. The output format of the XML
 * can be controlled using the various setWrapXXX methods detailed
 * below.
 *
 * @version $Id: DomainObjectXMLRenderer.java 2141 2011-01-16 12:17:15Z pboy $
 */
public class DomainObjectXMLRenderer extends DomainObjectTraversal {

    private static final Logger s_log = Logger.getLogger(DomainObjectXMLRenderer.class);
    private static Map s_formatters = new HashMap();
    private Stack m_elements = new Stack();
    protected Element m_element;
    private boolean m_wrapRoot = false;
    private boolean m_wrapObjects = false;
    private boolean m_wrapAttributes = false;
    private boolean m_revisitFullObject = false;
    private Map m_objectElements;
    private String m_namespaceURI;
    private String m_namespacePrefix;
    private DomainObjectXMLFormatter m_formatter;
    private String m_context;

    /**
     * Registers a traversal formatter for an object type in a given
     * context.
     *
     * @param type the object type whose items will be traversed
     * @param formatter the formatter for controlling object traversal
     * @param context the context in which the formatter should be used
     */
    public static void registerFormatter(ObjectType type,
            DomainObjectXMLFormatter formatter,
            String context) {
        s_formatters.put(new AdapterKey(type, context), formatter);
    }

    /**
     * Unregisteres a traversal formatter for an object type in a
     * given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the formatter should be used
     */
    public static void unregisterFormatter(ObjectType type,
            String context) {
        s_formatters.remove(new AdapterKey(type, context));
    }

    /**
     * Registers a traversal formatter for an object type in a given
     * context.
     *
     * @param type the object type whose items will be traversed
     * @param formatter the formatter for controlling object traversal
     * @param context the context in which the formatter should be used
     */
    public static void registerFormatter(String type,
            DomainObjectXMLFormatter formatter,
            String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering formatter "
                    + formatter.getClass().getName() + " for type " + type
                    + " in context " + context);
        }

        registerFormatter(MetadataRoot.getMetadataRoot().getObjectType(type),
                formatter,
                context);
    }

    /**
     * Unregisteres a traversal formatter for an object type in a
     * given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the formatter should be used
     */
    public static void unregisterFormatter(String type,
            String context) {
        unregisterFormatter(MetadataRoot.getMetadataRoot().getObjectType(type),
                context);
    }

    /**
     * Retrieves the traversal formatter for an object type in a given
     * context.
     *
     * @param type the object type to lookup
     * @param context the formatter context
     */
    public static DomainObjectXMLFormatter getFormatter(
            ObjectType type,
            String context) {
        return (DomainObjectXMLFormatter) s_formatters.get(new AdapterKey(type, context));
    }

    /**
     * Retrieves the closest matching traversal formatter for an object type
     * in a given context. The algorithm looks for an exact match, then
     * considers the supertype, and the supertype's supertype. If no match
     * could be found at all, returns null
     *
     * @param type the object type to search for
     * @param context the formatter context
     */
    public static DomainObjectXMLFormatter findFormatter(ObjectType type,
            String context) {
        DomainObjectXMLFormatter formatter = null;
        while (formatter == null && type != null) {
            formatter = getFormatter(type, context);
            if (s_log.isDebugEnabled()) {
                s_log.debug("getFormatter(" + type + "," + context + ")=" + formatter);
            }
            type = type.getSupertype();
        }
        return formatter;
    }

    /**
     * Creates a new DomainObject XML renderer
     * that outputs XML into the element passed into
     * the constructor.
     *
     * @param root the XML element in which to output children
     */
    public DomainObjectXMLRenderer(Element root) {
        m_element = root;
        m_objectElements = new HashMap();
    }

    public void setNamespace(String prefix,
            String uri) {
        m_namespacePrefix = prefix;
        m_namespaceURI = uri;
    }

    protected Object format(DomainObject obj,
            String path,
            Property prop,
            Object value) {
        if (m_formatter != null) {
            String propertyPath = appendToPath(path, prop.getName());
            Object rendered = m_formatter.format(obj,
                    propertyPath,
                    prop, value);
            if (s_log.isDebugEnabled()) {
                s_log.debug("FORMAT " + obj + " m_formatter=" + m_formatter + " rendered=" + rendered);
            }
            if (rendered == null) {
                // try supertype formatters
                ObjectType objectType = obj.getObjectType().getSupertype();
                DomainObjectXMLFormatter formatter = m_formatter;
                while (rendered == null && formatter != null && objectType != null) {
                    formatter = findFormatter(objectType, m_context);
                    if (formatter != null) {
                        rendered = formatter.format(obj, propertyPath, prop, value);
                    } else {
                        rendered = null;
                    }
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("FALLBACK supertype " + objectType + " formatter=" + formatter + " rendered=" + rendered);
                    }
                    objectType = objectType.getSupertype();
                }
            }
            if (rendered != null) {
                return rendered;
            } // else fallback to default below
        }
        s_log.debug("DEFAULT XML.format");
        return XML.format(value);
    }

    @Override
    protected void walk(DomainObject obj,
            String context,
            DomainObjectTraversalAdapter adapter) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Traversing " + obj + " for context " + context + " "
                    + "using adapter " + adapter);
        }

        m_formatter = findFormatter(obj.getObjectType(), context);
        m_context = context;

        if (s_log.isDebugEnabled()) {
            s_log.debug("Found formatter " + m_formatter);
        }

        super.walk(obj, context, adapter);
    }

    /**
     * Determines XML output for root object.
     * If set to true a separate element will
     * be output for the root object, if false,
     * then the element passed into the constructor
     * will be used.
     */
    public void setWrapRoot(boolean value) {
        m_wrapRoot = value;
    }

    /**
     * Determines XML output used for objects.
     * If set to true, then a wrapper XML element
     * will be generated for the association,
     * and then individual elements generated for
     * each object. If false then no wrapper
     * XML element will be produced.
     */
    public void setWrapObjects(boolean value) {
        m_wrapObjects = value;
    }

    /**
     * Determines XML output used for scalar
     * attributes. If set to true, then each
     * attribute is output as a separate element,
     * otherwise, attributes are output as simple
     * attributes.
     */
    public void setWrapAttributes(boolean value) {
        m_wrapAttributes = value;
    }

    /**
     * Determines XML output used for objects.
     * If set to true, then repeated objects will generate full xml. 
     * If false then only the OID will be printed.
     */
    public void setRevisitFullObject(boolean value) {
        m_revisitFullObject = value;
    }

    public boolean isWrappingAttributes() {
        return m_wrapAttributes;
    }

    public boolean isWrappingObjects() {
        return m_wrapObjects;
    }

    public boolean isWrappingRoot() {
        return m_wrapRoot;
    }

    protected void beginObject(DomainObject obj,
            String path) {
        if (m_wrapRoot || !path.equals("/object")) {
            String name = m_wrapObjects ? "object" : nameFromPath(path);
            Element element = newElement(m_element, name);

            m_elements.push(m_element);
            m_element = element;
        }
        m_element.addAttribute("oid", obj.getOID().toString());
        if (m_revisitFullObject) {
            m_objectElements.put(obj.getOID(), m_element);
        }
    }

    protected void endObject(DomainObject obj,
            String path) {
        if (m_wrapRoot || !path.equals("/object")) {
            m_element = (Element) m_elements.pop();
        }
    }

    protected void revisitObject(DomainObject obj,
            String path) {
        Element priorElement = null;
        if (m_revisitFullObject) {
            priorElement = (Element) m_objectElements.get(obj.getOID());
        }
        if (priorElement != null && (m_elements.search(priorElement) == -1)) {
            String name = m_wrapObjects ? "object" : nameFromPath(path);
            Element element = newElement(m_element, name, priorElement);
        } else {
            String name = m_wrapObjects ? "object" : nameFromPath(path);
            Element element = newElement(m_element, name);
            element.addAttribute("oid", obj.getOID().toString());
        }
    }

    protected void handleAttribute(DomainObject obj,
            String path,
            Property property) {
        String name = property.getName();
        Object value = obj.get(name);

        if (value != null) {
            if (m_wrapAttributes) {
                Object formattedValue = format(obj, path, property, value);
                if (formattedValue instanceof Element) {
                    m_element.addContent((Element) formattedValue);

                } else {
                    Element element = newElement(m_element, name);
                    element.setText((String) format(obj, path, property, value));

                    // Quasimodo:
                    // Special handling of date field, should be done somewhere else
                    // but that seems to be a problem
                    if (value instanceof Date) {
                        Date date = (Date) value;
                        Calendar calDate = Calendar.getInstance();
                        calDate.setTime(date);
                        
                        // locale-independent date output
                        element.addAttribute("year", Integer.toString(calDate.get(Calendar.YEAR)));
                        element.addAttribute("month", Integer.toString(calDate.get(Calendar.MONTH) + 1));
                        element.addAttribute("day", Integer.toString(calDate.get(Calendar.DAY_OF_MONTH)));
                        element.addAttribute("hour", Integer.toString(calDate.get(Calendar.HOUR_OF_DAY)));
                        element.addAttribute("minute", Integer.toString(calDate.get(Calendar.MINUTE)));
                        element.addAttribute("second", Integer.toString(calDate.get(Calendar.SECOND)));

                        // Quasimodo: BEGIN
                        // Add attributes for date and time
                        Locale negLocale = com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale();
                        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, negLocale);
                        DateFormat longDateFormatter = DateFormat.getDateInstance(DateFormat.LONG, negLocale);
                        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, negLocale);
                        element.addAttribute("date", dateFormatter.format(date));
                        element.addAttribute("longDate", longDateFormatter.format(date));
                        element.addAttribute("time", timeFormatter.format(date));
                        element.addAttribute("monthName", calDate.getDisplayName(Calendar.MONTH, Calendar.LONG, negLocale));
                        // Quasimodo: END

                    }
                }
            } else {
                m_element.addAttribute(property.getName(),
                        (String) format(obj, path, property, value));
            }
        }
    }

    protected void beginRole(DomainObject obj,
            String path,
            Property property) {
        if (m_wrapObjects) {
            Element element = newElement(m_element, property.getName());
            m_elements.push(m_element);
            m_element = element;
        }
    }

    protected void endRole(DomainObject obj,
            String path,
            Property property) {
        if (m_wrapObjects) {
            m_element = (Element) m_elements.pop();
        }
    }

    protected void beginAssociation(DomainObject obj,
            String path,
            Property property) {
        if (m_wrapObjects) {
            Element element = newElement(m_element, property.getName());
            m_elements.push(m_element);
            m_element = element;
        }
    }

    protected void endAssociation(DomainObject obj,
            String path,
            Property property) {
        if (m_wrapObjects) {
            m_element = (Element) m_elements.pop();
        }
    }

    protected Element getCurrentElement() {
        return m_element;
    }

    protected void setCurrentElement(Element element) {
        m_element = element;
    }

    protected Stack getElementStack() {
        return m_elements;
    }

    protected Element newElement(Element parent,
            String name) {
        return m_namespaceURI == null
                ? parent.newChildElement(name)
                : parent.newChildElement(m_namespacePrefix + ":" + name,
                m_namespaceURI);
    }

    protected Element newElement(Element parent,
            String name,
            Element copy) {
        return m_namespaceURI == null
                ? parent.newChildElement(name, copy)
                : parent.newChildElement(m_namespacePrefix + ":" + name,
                m_namespaceURI,
                copy);
    }
}
