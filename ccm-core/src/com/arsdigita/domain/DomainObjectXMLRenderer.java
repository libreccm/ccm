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
 * An implementation of DomainObjectTraversal that generates an XML tree representing the DomainObject. The output
 * format of the XML can be controlled using the various setWrapXXX methods detailed below.
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
     * Registers a traversal formatter for an object type in a given context.
     *
     * @param type the object type whose items will be traversed
     * @param formatter the formatter for controlling object traversal
     * @param context the context in which the formatter should be used
     */
    public static void registerFormatter(final ObjectType type,
                                         final DomainObjectXMLFormatter formatter,
                                         final String context) {
        s_formatters.put(new AdapterKey(type, context), formatter);
    }

    /**
     * Unregisteres a traversal formatter for an object type in a given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the formatter should be used
     */
    public static void unregisterFormatter(final ObjectType type, final String context) {
        s_formatters.remove(new AdapterKey(type, context));
    }

    /**
     * Registers a traversal formatter for an object type in a given context.
     *
     * @param type the object type whose items will be traversed
     * @param formatter the formatter for controlling object traversal
     * @param context the context in which the formatter should be used
     */
    public static void registerFormatter(final String type,
                                         final DomainObjectXMLFormatter formatter,
                                         final String context) {
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
     * Unregisteres a traversal formatter for an object type in a given context
     *
     * @param type the object type whose items will be traversed
     * @param context the context in which the formatter should be used
     */
    public static void unregisterFormatter(final String type,
                                           final String context) {
        unregisterFormatter(MetadataRoot.getMetadataRoot().getObjectType(type),
                            context);
    }

    /**
     * Retrieves the traversal formatter for an object type in a given context.
     *
     * @param type the object type to lookup
     * @param context the formatter context
     */
    public static DomainObjectXMLFormatter getFormatter(final ObjectType type, final String context) {
        return (DomainObjectXMLFormatter) s_formatters.get(new AdapterKey(type, context));
    }

    /**
     * Retrieves the closest matching traversal formatter for an object type in a given context. The algorithm looks for
     * an exact match, then considers the supertype, and the supertype's supertype. If no match could be found at all,
     * returns null
     *
     * @param type the object type to search for
     * @param context the formatter context
     * @return  
     */
    public static DomainObjectXMLFormatter findFormatter(final ObjectType type, final String context) {
        DomainObjectXMLFormatter formatter = null;
        ObjectType curType = type;
        while (formatter == null && curType != null) {
            formatter = getFormatter(curType, context);
            if (s_log.isDebugEnabled()) {
                s_log.debug("getFormatter(" + curType + "," + context + ")=" + formatter);
            }
            curType = curType.getSupertype();
        }
        return formatter;
    }

    /**
     * Creates a new DomainObject XML renderer that outputs XML into the element passed into the constructor.
     *
     * @param root the XML element in which to output children
     */
    public DomainObjectXMLRenderer(final Element root) {
        m_element = root;
        m_objectElements = new HashMap();
    }

    public void setNamespace(final String prefix, final String uri) {
        m_namespacePrefix = prefix;
        m_namespaceURI = uri;
    }

    protected Object format(final DomainObject obj, final String path, final Property prop, final Object value) {
        if (m_formatter != null) {
            final String propertyPath = appendToPath(path, prop.getName());
            Object rendered = m_formatter.format(obj, propertyPath, prop, value);
            if (s_log.isDebugEnabled()) {
                s_log.debug("FORMAT " + obj + " m_formatter=" + m_formatter + " rendered=" + rendered);
            }

            if (rendered == null) {
                // try supertype formatters
                ObjectType objectType = obj.getObjectType().getSupertype();
                DomainObjectXMLFormatter formatter = m_formatter;
                while (rendered == null && formatter != null && objectType != null) {
                    formatter = findFormatter(objectType, m_context);
                    if (formatter == null) {
                        rendered = null;
                    } else {
                        rendered = formatter.format(obj, propertyPath, prop, value);
                    }
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("FALLBACK supertype " + objectType + " formatter=" + formatter + " rendered="
                                    + rendered);
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
    protected void walk(final DomainObject obj, final String context, final DomainObjectTraversalAdapter adapter) {
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
     * Determines XML output for root object. If set to true a separate element will be output for the root object, if
     * false, then the element passed into the constructor will be used.
     * @param value 
     */
    public void setWrapRoot(final boolean value) {
        m_wrapRoot = value;
    }

    /**
     * Determines XML output used for objects. If set to true, then a wrapper XML element will be generated for the
     * association, and then individual elements generated for each object. If false then no wrapper XML element will be
     * produced.
     * @param value 
     */
    public void setWrapObjects(final boolean value) {
        m_wrapObjects = value;
    }

    /**
     * Determines XML output used for scalar attributes. If set to true, then each attribute is output as a separate
     * element, otherwise, attributes are output as simple attributes.
     */
    public void setWrapAttributes(final boolean value) {
        m_wrapAttributes = value;
    }

    /**
     * Determines XML output used for objects. If set to true, then repeated objects will generate full xml. If false
     * then only the OID will be printed.
     */
    public void setRevisitFullObject(final boolean value) {
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

    protected void beginObject(final DomainObject obj, final String path) {
        //if (m_wrapRoot || !path.equals("/object")) {
        if (m_wrapRoot || !("/object".equals(path))) {

            final String name;
            if (m_wrapObjects) {
                name = "object";
            } else {
                name = nameFromPath(path);
            }
            //String name = m_wrapObjects ? "object" : nameFromPath(path);
            final Element element = newElement(m_element, name);

            m_elements.push(m_element);
            m_element = element;
        }
        m_element.addAttribute("oid", obj.getOID().toString());
        if (m_revisitFullObject) {
            m_objectElements.put(obj.getOID(), m_element);
        }
    }

    protected void endObject(final DomainObject obj, final String path) {
        //if (m_wrapRoot || !path.equals("/object")) {
        if (m_wrapRoot || !("/object".equals(path))) {
            m_element = (Element) m_elements.pop();
        }
    }

    protected void revisitObject(final DomainObject obj, final String path) {
        Element priorElement = null;
        if (m_revisitFullObject) {
            priorElement = (Element) m_objectElements.get(obj.getOID());
        }
        if (priorElement != null && (m_elements.search(priorElement) == -1)) {
            final String name = m_wrapObjects ? "object" : nameFromPath(path);
            newElement(m_element, name, priorElement);
        } else {
            final String name = m_wrapObjects ? "object" : nameFromPath(path);
            final Element element = newElement(m_element, name);
            element.addAttribute("oid", obj.getOID().toString());
        }
    }

    protected void handleAttribute(final DomainObject obj, final String path, final Property property) {
        final String name = property.getName();
        final Object value = obj.get(name);

        if (value != null) {
            if (m_wrapAttributes) {
                final Object formattedValue = format(obj, path, property, value);
                if (formattedValue instanceof Element) {
                    m_element.addContent((Element) formattedValue);

                } else {
                    final Element element = newElement(m_element, name);
                    element.setText((String) format(obj, path, property, value));

                    // Quasimodo:
                    // Special handling of date field, should be done somewhere else
                    // but that seems to be a problem
                    if (value instanceof Date) {
                        final Date date = (Date) value;
                        final Calendar calDate = Calendar.getInstance();
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
                        final Locale negLocale = com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale();
                        final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, negLocale);
                        final DateFormat longDateFormatter = DateFormat.getDateInstance(DateFormat.LONG, negLocale);
                        final DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, negLocale);
                        element.addAttribute("date", dateFormatter.format(date));
                        element.addAttribute("longDate", longDateFormatter.format(date));
                        element.addAttribute("time", timeFormatter.format(date));
                        element.addAttribute("monthName", calDate.getDisplayName(Calendar.MONTH, Calendar.LONG,
                                                                                 negLocale));
                        // Quasimodo: END

                    }
                }
            } else {
                m_element.addAttribute(property.getName(),
                                       (String) format(obj, path, property, value));
            }
        }
    }

    protected void beginRole(final DomainObject obj, final String path, final Property property) {
        if (m_wrapObjects) {
            final Element element = newElement(m_element, property.getName());
            m_elements.push(m_element);
            m_element = element;
        }
    }

    protected void endRole(final DomainObject obj, final String path, final Property property) {
        if (m_wrapObjects) {
            m_element = (Element) m_elements.pop();
        }
    }

    protected void beginAssociation(final DomainObject obj, final String path, final Property property) {
        if (m_wrapObjects) {
            final Element element = newElement(m_element, property.getName());
            m_elements.push(m_element);
            m_element = element;
        }
    }

    protected void endAssociation(final DomainObject obj, final String path, final Property property) {
        if (m_wrapObjects) {
            m_element = (Element) m_elements.pop();
        }
    }

    protected Element getCurrentElement() {
        return m_element;
    }

    protected void setCurrentElement(final Element element) {
        m_element = element;
    }

    protected Stack getElementStack() {
        return m_elements;
    }

    protected Element newElement(final Element parent, final String name) {
       if (m_namespaceURI == null) {
           return parent.newChildElement(name);
       } else {
           final StringBuffer nameBuffer = new StringBuffer();
           nameBuffer.append(m_namespacePrefix);
           nameBuffer.append(':');
           nameBuffer.append(name);
           
           return parent.newChildElement(name, m_namespaceURI);
       }
        
        
//        return m_namespaceURI == null
//               ? parent.newChildElement(name)
//               : parent.newChildElement(m_namespacePrefix + ":" + name,
//                                        m_namespaceURI);
    }

    protected Element newElement(final Element parent, final String name, final Element copy) {
        if (m_namespaceURI == null) {
           return parent.newChildElement(name, copy);
       } else {
           final StringBuffer nameBuffer = new StringBuffer();
           nameBuffer.append(m_namespacePrefix);
           nameBuffer.append(':');
           nameBuffer.append(name);
           
           return parent.newChildElement(name, m_namespaceURI, copy);
       }
        
//        return m_namespaceURI == null
//               ? parent.newChildElement(name, copy)
//               : parent.newChildElement(m_namespacePrefix + ":" + name,
//                                        m_namespaceURI,
//                                        copy);
    }

}
