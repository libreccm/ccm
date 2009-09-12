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

package com.arsdigita.aplaws;

import com.arsdigita.xml.Element;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;

import java.util.HashMap;
import java.util.Stack;
import java.math.BigDecimal;

public class ObjectTypeSchemaGenerator extends ObjectTypeTraversal {

    private boolean m_wrapRoot = false;
    private boolean m_wrapObjects = false;
    private boolean m_wrapAttributes = false;



    private Stack m_history = new Stack();
    private HashMap m_elements = new HashMap();
    
    // The xs:element
    private Element m_element;
    // The (optional) xs:complexType
    private Element m_type;
    // The (optional) xs:sequence
    private Element m_sequence;
    // The (optional property
    private Property m_property;
    private Stack m_properties = new Stack();

    private Element m_root;
    private String m_rootName;

    public static final String SCHEMA_PREFIX = "xs:";

    public static final String SCHEMA_NS = 
        "http://www.w3.org/2001/XMLSchema";

    private static HashMap s_types = new HashMap();
    static {
        s_types.put(String.class, "xs:string");
        s_types.put(Boolean.class, "xs:boolean");
        s_types.put(Integer.class, "xs:integer");
        s_types.put(BigDecimal.class, "xs:double");
    }

    protected static String lookupType(Class klass) {
        if (s_types.containsKey(klass)) {
            return (String)s_types.get(klass);
        }
        return "xs:string";
    }
    
    public static void registerType(Class klass, String type) {
        s_types.put(klass, type);
    }

    
    public ObjectTypeSchemaGenerator(String rootName,
                                     String namespace) {
        m_root = new Element(SCHEMA_PREFIX + "schema",
                             SCHEMA_NS);
        m_rootName = rootName;
        
        // Set the namespace for nodes defined by the schema
        m_root.addAttribute("targetNamespace", namespace);
        // Set the default namespace for unqualified nodes
        m_root.addAttribute("xmlns", namespace);
        // All nodes in an instance doc conforming to the schema
        // must be qualified
        m_root.addAttribute("elementFormDefault", "qualified");
    }

    public Element getRoot() {
        return m_root;
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
     * Method called when the processing of an object
     * starts
     */
    protected void beginObject(ObjectType obj,
                               String path) {
        // XXX deal with revisited objects - xs:choice possibly

        if (m_type != null && m_sequence == null) {
            Element sequence = m_type.newChildElement(SCHEMA_PREFIX + "sequence",
                                                      SCHEMA_NS);
            m_sequence = sequence;
        }
        
        Element parent;
        String name;
        if (m_element == null) {
            if (m_wrapRoot) {
                Element element = m_root.newChildElement(SCHEMA_PREFIX + "element",
                                                         SCHEMA_NS);
                element.addAttribute("name", m_rootName);
                
                Element type = element.newChildElement(SCHEMA_PREFIX + "complexType",
                                                       SCHEMA_NS);
                Element sequence = type.newChildElement(SCHEMA_PREFIX + "sequence",
                                                        SCHEMA_NS);
                
                parent = sequence;
                name = nameFromPath(path);
            } else {
                parent = m_root;
                name = m_rootName;
            }
        } else {
            parent = m_sequence;
            if (m_wrapObjects) {
                name = "object";
            } else {
                name = nameFromPath(path);
            }
        }
        Element element = parent.newChildElement(SCHEMA_PREFIX + "element",
                                                 SCHEMA_NS);
        element.addAttribute("name", name);

        if (m_property != null) {
            if (m_property.isNullable()) {
                element.addAttribute("minOccurs", "0");
            }
            if (m_property.isCollection()) {
                element.addAttribute("maxOccurs", "unbounded");
            }
        }

        Element type = element.newChildElement(SCHEMA_PREFIX + "complexType",
                                               SCHEMA_NS);
        
        Element oid = type.newChildElement(SCHEMA_PREFIX + "attribute",
                                           SCHEMA_NS);
        oid.addAttribute("name", "oid");
        oid.addAttribute("type", "xs:string");
        
        // Add to the path -> element map, not that we use this info yet
        m_elements.put(path, element);

        // Preserve context
        m_history.push(new Element[] { m_element, m_type, m_sequence });

        m_element = element;
        m_type = type;
        m_sequence = null;
    }

    /**
     * Method called when the procesing of an object
     * completes
     */
    protected void endObject(ObjectType obj,
                             String path) {
        Element[] saved = (Element[])m_history.pop();
        m_element = saved[0];
        m_type = saved[1];
        m_sequence = saved[2];
    }

    /**
     * Method called when an attribute is encountered
     */
    protected void handleAttribute(ObjectType obj,
                                   String path,
                                   Property property) {
        if (m_wrapAttributes) {
            if (m_sequence == null) {
                Element sequence = m_type.newChildElement(SCHEMA_PREFIX + "sequence",
                                                          SCHEMA_NS);
                m_sequence = sequence;
            }

            Element element = new Element(SCHEMA_PREFIX + "element",
                                          SCHEMA_NS);
            element.addAttribute("name", property.getName());
            // XXX pdl type -> xs type mapping
            element.addAttribute("type",lookupType(property.getJavaClass()));

            if (property.isNullable()) {
                element.addAttribute("minOccurs", "0");
            }

            // Add to element
            m_sequence.addContent(element);
            
            // Add to the path -> element map
            m_elements.put(path, element);
        } else {
            Element element = new Element(SCHEMA_PREFIX + "attribute",
                                          SCHEMA_NS);
            element.addAttribute("name", property.getName());
            // XXX pdl type -> xs type mapping
            element.addAttribute("type", lookupType(property.getJavaClass()));

            if (property.isRequired()) {
                element.addAttribute("use", "required");
            }

            // Add to element
            m_type.addContent(element);
            
            // Add to the path -> element map
            m_elements.put(path, element);
        }
    }

    /**
     * Method called when the processing of a role
     * starts
     */
    protected void beginRole(ObjectType obj,
                             String path,
                             Property property) {
        if (m_wrapObjects) {
            if (m_sequence == null) {
                Element sequence = m_type.newChildElement(SCHEMA_PREFIX + "sequence",
                                                          SCHEMA_NS);
                m_sequence = sequence;
            }

            Element element = m_sequence.newChildElement(SCHEMA_PREFIX + "element",
                                                         SCHEMA_NS);
            element.addAttribute("name", property.getName());
            if (property.isNullable()) {
                element.addAttribute("minOccurs", "0");
            }
            
            Element type = element.newChildElement(SCHEMA_PREFIX + "complexType",
                                                   SCHEMA_NS);
            Element sequence = type.newChildElement(SCHEMA_PREFIX + "sequence",
                                                    SCHEMA_NS);
            
            // Preserve context
            m_history.push(new Element[] { m_element, m_type, m_sequence });
            
            m_element = element;
            m_type = type;
            m_sequence = sequence;
        }
        m_properties.push(m_property);
        m_property = property;
    }

    /**
     * Method called when the procesing of a role
     * completes
     */
    protected void endRole(ObjectType obj,
                           String path,
                           Property property) {
        if (m_wrapObjects) {
            Element[] saved = (Element[])m_history.pop();
            m_element = saved[0];
            m_type = saved[1];
            m_sequence = saved[2];
        }
        m_property = (Property)m_properties.pop();
    }

    /**
     * Method called when the processing of an association
     * starts
     */
    protected void beginAssociation(ObjectType obj,
                                    String path,
                                    Property property) {
        if (m_wrapObjects) {
            if (m_sequence == null) {
                Element sequence = m_type.newChildElement(SCHEMA_PREFIX + "sequence",
                                                          SCHEMA_NS);
                m_sequence = sequence;
            }

            Element element = m_sequence.newChildElement(SCHEMA_PREFIX + "element",
                                                         SCHEMA_NS);
            element.addAttribute("name", property.getName());
            if (property.isNullable()) {
                element.addAttribute("minOccurs", "0");
            }
            
            Element type = element.newChildElement(SCHEMA_PREFIX + "complexType",
                                                   SCHEMA_NS);
            Element sequence = type.newChildElement(SCHEMA_PREFIX + "sequence",
                                                    SCHEMA_NS);
            
            // Preserve context
            m_history.push(new Element[] { m_element, m_type, m_sequence });
   
            m_element = element;
            m_type = type;
            m_sequence = sequence;
        }
        m_properties.push(m_property);
        m_property = property;
    }

    /**
     * Method called when the procesing of an association
     * completes
     */
    protected void endAssociation(ObjectType obj,
                                  String path,
                                  Property property) {
        if (m_wrapObjects) {
            Element[] saved = (Element[])m_history.pop();
            m_element = saved[0];
            m_type = saved[1];
            m_sequence = saved[2];
        }
        m_property = (Property)m_properties.pop();
    }
    
}
