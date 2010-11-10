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

import org.apache.log4j.Logger;
import com.arsdigita.persistence.metadata.Property;
import java.util.HashSet;

/**
 * This is a general purpose traversal adaptor
 * that allows/denies processing of a property
 * based on the path to the property, and its 
 * presence in an inclusion/exclusion set.
 *
 * Instances of this class can be configured using
 * the DomainObjectTraversalInitializer
 *
 * <p>See <code>com.arsdigita.cms.installer.DomainObjectTraversalInitializer</code>.
 */
public class SimpleDomainObjectTraversalAdapter
        implements DomainObjectTraversalAdapter {

    private static final Logger s_log =
                                Logger.getLogger(
            SimpleDomainObjectTraversalAdapter.class);
    /**
     * Rule that indicates the set of properties should be treated
     * as an inclusion list. ie, don't allow any properties except
     * those listed. This is the default for associations
     */
    public final static int RULE_INCLUDE = 0;
    /**
     * Rule that indicates the set of properties should be treated
     * as an exclusion list. ie, allow through all properties,
     * except those listed. This is the default for attributes.
     */
    public final static int RULE_EXCLUDE = 1;
    private HashSet m_attr = new HashSet();
    private HashSet m_assoc = new HashSet();
    private int m_attrRule = RULE_EXCLUDE;
    private int m_assocRule = RULE_INCLUDE;
    private SimpleDomainObjectTraversalAdapter m_parent;

    /**
     * Creates a new traversal adapter, with no parent
     * delegate. If no explicit rule is present it will
     * return false if RULE_INCLUDE is set, or true if
     * RULE_EXCLUDE is set.
     */
    public SimpleDomainObjectTraversalAdapter() {
        this(null);
    }

    /**
     * Creates a new traversal adapter, extending the rules
     * defined by a parent. If there is no explicit rule
     * for the property questioned, it will delegate the
     * query to the parent.
     * @param parent the parent adapter to delegate to
     */
    public SimpleDomainObjectTraversalAdapter(
            SimpleDomainObjectTraversalAdapter parent) {
        m_parent = parent;
    }

    public SimpleDomainObjectTraversalAdapter getParent() {
        return m_parent;
    }

    public int getAttributeRule() {
        return m_attrRule;
    }

    /**
     * Set the rule for processing attributes
     *
     * @param rule the new processing rule
     */
    public void setAttributeRule(int rule) {
        m_attrRule = rule;
    }

    public int getAssociationRule() {
        return m_assocRule;
    }

    /**
     * Set the rule for processing associations
     *
     * @param rule the new processing rule
     */
    public void setAssociationRule(int rule) {
        m_assocRule = rule;
    }

    public HashSet getAttributeProperties() {
        return m_attr;
    }

    /**
     * Add a property to the attribute property set.
     *
     * @param prop the full path to the property
     */
    public void addAttributeProperty(String prop) {
        m_attr.add(prop);
    }

    /**
     * Removes a property from the attribute property set.
     * 
     * @param prop full path of the property to remove
     */
    public void removeAttributeProperty(String prop) {
        s_log.debug(String.format("Removing attribute property '%s'", prop));
        m_attr.remove(prop);
    }

    /**
     * Clears the property set.
     */
    public void clearAttributeProperties() {
        m_attr.clear();
    }

    public HashSet getAssociationProperties() {
        return m_assoc;
    }

    /**
     * Add a property to the association property set.
     *
     * @param prop the full path to the property
     */
    public void addAssociationProperty(String prop) {
        m_assoc.add(prop);
    }

    /**
     * Removes a association property from the association property set.
     *
     * @param prop full path of the property to remove
     */
    public void removeAssociationProperty(String prop) {
        s_log.debug(String.format("Removing association property '%s'", prop));
        m_assoc.remove(prop);
    }

    /**
     * Clears the association properties set.
     */
    public void clearAssociationProperties() {
        m_assoc.clear();
    }

    /**
     * Determines whether or not to allow processing
     * of a property, based on the property set and
     * the processing rule
     */
    public boolean processProperty(DomainObject obj,
                                   String path,
                                   Property prop,
                                   String context) {
        if (prop.isAttribute()) {
            boolean result = m_attr.contains(path);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Check attr " + path + " contains " + result + " "
                            + m_attrRule);
            }
            if (!result && m_parent != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No explicit rule, delegating to parent");
                }
                return m_parent.processProperty(obj, path, prop, context);
            }
            return m_attrRule == RULE_INCLUDE ? result : !result;
        } else {
            boolean result = m_assoc.contains(path);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Check assoc " + path + " contains " + result + " "
                            + m_assocRule);
            }
            if (!result && m_parent != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No explicit rule, delegating to parent");
                }
                return m_parent.processProperty(obj, path, prop, context);
            }
            return m_assocRule == RULE_INCLUDE ? result : !result;
        }
    }
}
