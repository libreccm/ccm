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
package com.arsdigita.domain.xml;

import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.DomainObjectTraversalAdapter;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.DomainObjectXMLFormatter;
import com.arsdigita.domain.SimpleDomainObjectXMLFormatter;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Classes;
import com.arsdigita.xml.Formatter;
import com.arsdigita.xml.NodeGenerator;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;
import java.util.HashMap;

/**
 * TraversalHandler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class TraversalHandler extends DefaultHandler {

    public final static String versionId = "$Id: TraversalHandler.java 1534 2007-03-23 12:08:00Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(TraversalHandler.class);

    private static final String XMLNS =
        "http://xmlns.redhat.com/schemas/waf/xml-renderer-rules";

    private Map m_adapters;
    private SimpleDomainObjectTraversalAdapter m_adapter;
    private SimpleDomainObjectXMLFormatter m_formatters;
    private String m_objectType;
    private String m_context;
    private String m_typeContext;

    // Temp state var
    private boolean m_attrs;

    public TraversalHandler() {
        m_adapters = new HashMap();
    }

    public void characters(char[] ch, int start, int len) {}

    public void startElement(String uri, String localName, String qn,
                             Attributes attrs) {
        if (!XMLNS.equals(uri)) {
            s_log.warn("Ignoring attribute " + localName +
                       " " + qn + " " + uri);
            return;
        }

        if (localName.equals("adapters")) {
            // nada
        } else if (localName.equals("context")) {
            m_context = attrs.getValue("name");
            if (m_context == null) {
                throw new RuntimeException
                    ("the 'name' attribute is compulsory " +
                     "on the <context> element");
            }
        } else if (localName.equals("adapter")) {
            m_objectType = attrs.getValue("objectType");
            if (m_objectType == null) {
                throw new RuntimeException
                    ("the 'objectType' attribute is " +
                     "compulsory on the <context> element");
            }
            m_typeContext = attrs.getValue("context");
            if (m_typeContext != null) {
                s_log.warn("The 'context' attribute on the <adapter> " +
                           "element is deprecated. Please group your " +
                           "multiple <adapter> elements in a single " +
                           "<context name=\"foo\">...</context> element " +
                           "instead");
            }

            String traversalClass = attrs.getValue("traversalClass");
            if (traversalClass == null) {
                traversalClass = SimpleDomainObjectTraversalAdapter
                    .class.getName();
            }

            String parentType = attrs.getValue("extends");
            if (parentType == null) {
                m_adapter = (SimpleDomainObjectTraversalAdapter)
                    Classes.newInstance(traversalClass);
            } else {
                Object parent = m_adapters.get(parentType);
                if (parent == null) {
                    ObjectType ot = SessionManager.getMetadataRoot()
                        .getObjectType(parentType);
                    if (ot != null) {
                        parent = DomainObjectTraversal.findAdapter
                            (ot, m_typeContext == null ?
                             m_context : m_typeContext);
                    }
                }
                if (parent == null) {
                    s_log.error("Cannot find adapter definition for extends='" +
                                parentType + "' on type " + m_objectType +
                                " in context " + m_context);
                }
                s_log.info("Setting parent adapter for " + m_objectType +
                           " in context " + m_context + " to " + parentType);
                m_adapter = (SimpleDomainObjectTraversalAdapter)
                    Classes.newInstance
                    (traversalClass,
                     new Class[] { SimpleDomainObjectTraversalAdapter.class },
                     new Object[] { parent });
            }
            m_adapters.put(m_objectType, m_adapter);

            String formatter = attrs.getValue("formatter");
            if (formatter == null) {
                formatter = SimpleDomainObjectXMLFormatter
                    .class.getName();
            }
            m_formatters = (SimpleDomainObjectXMLFormatter)
                Classes.newInstance(formatter);
        } else if (localName.equals("attributes")) {
            m_attrs = true;
            String rule = attrs.getValue("rule");
            if (rule == null) {
                throw new RuntimeException
                    ("the 'rule' attribute is " +
                     "compulsory on the <attributes> element");
            }
            s_log.debug("Set attribute rule " + rule);
            if ("include".equals(rule)) {
                m_adapter.setAttributeRule
                    (SimpleDomainObjectTraversalAdapter.RULE_INCLUDE);
            } else {
                m_adapter.setAttributeRule
                    (SimpleDomainObjectTraversalAdapter.RULE_EXCLUDE);
            }
        } else if (localName.equals("associations")) {
            m_attrs = false;
            String rule = attrs.getValue("rule");
            if (rule == null) {
                throw new RuntimeException
                    ("the 'rule' attribute is " +
                     "compulsory on the <associations> element");
            }
            s_log.debug("Set association rule " + rule);
            if ("exclude".equals(rule)) {
                m_adapter.setAssociationRule
                    (SimpleDomainObjectTraversalAdapter.RULE_EXCLUDE);
            } else {
                m_adapter.setAssociationRule
                    (SimpleDomainObjectTraversalAdapter.RULE_INCLUDE);
            }
        } else if (localName.equals("formatter")) {
            String property = (String)attrs.getValue("property");
            String klass = (String)attrs.getValue("class");

            Formatter formatter = (Formatter)Classes.newInstance(klass);
            m_formatters.addFormatter(property, formatter);
        } else if (localName.equals("generator")) {
	    String property = (String) attrs.getValue("property");
	    String klass = (String) attrs.getValue("class");

	    NodeGenerator generator = (NodeGenerator)Classes.newInstance(klass);
	    m_formatters.addGenerator(property, generator);
        } else if (localName.equals("property")) {
            String prop = (String)attrs.getValue("name");
            if (prop == null) {
                throw new RuntimeException
                    ("the 'name' attribute is " +
                     "compulsory on the <property> element");
            }

            if (m_attrs) {
                s_log.debug("Adding attribute property " + prop);
                m_adapter.addAttributeProperty(prop);
            } else {
                s_log.debug("Adding association property " + prop);
                m_adapter.addAssociationProperty(prop);
            }
        } else {
            s_log.warn("Unhandled element " + qn);
        }
    }

    public void endElement(String uri, String localName, String qn) {
        if (!XMLNS.equals(uri)) {
            s_log.warn("Ignoring attribute " + localName +
                       " " + qn + " " + uri);
            return;
        }

        if (localName.equals("adapters")) {
            // nada
        } else if (localName.equals("context")) {
            m_context = null;
        } else if (localName.equals("adapter")) {
            s_log.info("Registering adapter for " + m_objectType +
                       " in context " + m_context);
            registerAdapter
                (m_objectType,
                 m_adapter,
                 m_typeContext == null ? m_context : m_typeContext);
            if (!m_formatters.isEmpty()) {
                registerFormatter
                (m_objectType,
                 m_formatters,
                 m_typeContext == null ? m_context : m_typeContext);
            }
            m_objectType = null;
            m_adapter = null;
            m_typeContext = null;
            m_formatters = null;
        } else if (localName.equals("attributes")) {
            // nada
        } else if (localName.equals("formatter")) {
            // nada
	} else if (localName.equals("generator")) {
	    // nada
        } else if (localName.equals("associations")) {
            // nada
        } else if (localName.equals("property")) {
            // nada
        } else {
            s_log.warn("Unhandled element " + qn);
        }
    }

    protected void registerAdapter(String objectType,
                                   DomainObjectTraversalAdapter adapter,
                                   String context) {
        DomainObjectTraversal.registerAdapter(objectType,
                                              adapter,
                                              context);
    }

    protected void registerFormatter(String objectType,
                                     DomainObjectXMLFormatter formatter,
                                     String context) {
        DomainObjectXMLRenderer.registerFormatter(objectType,
                                                  formatter,
                                                  context);
    }
}
