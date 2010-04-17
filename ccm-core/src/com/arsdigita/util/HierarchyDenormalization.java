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
package com.arsdigita.util;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectObserver;
import com.arsdigita.domain.ObservableDomainObject;

import org.apache.log4j.Logger;

/**
 * <p>
 * A class to allow a column with denormalization to be maintained.
 * This abstract class can be subclassed to allow an object to easily
 * maintain a denormalized hierarcy (such as the default parents for
 * a category).
 * </p>
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: HierarchyDenormalization.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class HierarchyDenormalization {

    private final static Logger s_log =
        Logger.getLogger(HierarchyDenormalization.class);

    public HierarchyDenormalization(String operationName,
                                    ObservableDomainObject object,
                                    String attributeName) {
        this(operationName, object, attributeName, "id");
    }

    // id must be a property in the OID
    public HierarchyDenormalization(String operationName,
                                    ObservableDomainObject object,
                                    String attributeName, String id) {
        object.addObserver(new Observer(operationName, attributeName, id));
    }

    private class Observer implements DomainObjectObserver {

        private final String m_attributeName;
        private final String m_id;
        private final String m_operationName;
        private boolean m_isModified = false;
        private String m_oldAttributeValue;
        private String m_newAttributeValue;

        Observer(String operationName, String attributeName, String id) {
            m_id = id;
            m_operationName = operationName;
            m_attributeName = attributeName;
        }

        public void set(DomainObject dobj, String name,
                        Object old_value, Object new_value) {

            if (!name.equals(m_attributeName)) { return; }

            if (m_isModified) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got another set on " + dobj + "." + 
                                name + " old " + old_value + " new " + 
                                new_value);
                }
                m_newAttributeValue = (String) new_value;
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got set on " + dobj + "." + name + 
                                " old " + old_value + " new " + new_value);
                }
                m_oldAttributeValue = (String) old_value;
                m_newAttributeValue = (String) new_value;
                m_isModified = true;
            }

        }

        public void add(DomainObject dobj, String name,
                        DataObject dataObject) { }

        public void remove(DomainObject dobj, String name,
                           DataObject dataObject) { }

        public void clear(DomainObject dobj, String name) { }

        public void beforeSave(DomainObject dobj) { 
            if (s_log.isDebugEnabled()) {
                s_log.debug("In before save for " + dobj);
            }
        }

        public void afterSave(DomainObject dobj) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("In after save for " + dobj);
            }
            if (!m_isModified) { return; }

            if (s_log.isDebugEnabled()) {
                s_log.debug("After save: oid:" + dobj +
                            " new value is:" + m_newAttributeValue +
                            " old value is:" + m_oldAttributeValue);
            }

            if ((m_oldAttributeValue == null
                 && m_newAttributeValue == null)
                || (m_oldAttributeValue != null
                    && m_oldAttributeValue.equals(m_newAttributeValue))) {

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Aborting because both null, or equal");
                }
                return;
            }

            if (m_oldAttributeValue == null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Aborting because old is null");
                }
                // after save triggered by autoflush in before save
                m_isModified = false;
                return;
            }

            DataOperation operation =
                SessionManager.getSession().retrieveDataOperation
                (m_operationName);
            operation.setParameter("id", dobj.getOID().get(m_id));
            operation.setParameter("newPrefix", m_newAttributeValue);
            operation.setParameter
                ("oldPrefixLength", new Integer
                 (m_oldAttributeValue.length()));
            operation.setParameter("oldPrefix", m_oldAttributeValue);
            operation.execute();

            m_isModified = false;
        }

        public void beforeDelete(DomainObject dobj) { }
        public void afterDelete(DomainObject dobj) { }

        public boolean equals(Object other) {
            if (other instanceof Observer) {
                Observer o = (Observer) other;
                return m_operationName.equals(o.m_operationName)
                    && m_id.equals(o.m_id)
                    && m_attributeName.equals(o.m_attributeName);
            }

            return false;
        }

        public int hashCode() {
            return m_operationName.hashCode();
        }
    }

    /**
     * @deprecated HierarchyDenormalization now uses a DomainObjectObserver
     */
    public void beforeSave() { }

    /**
     * @deprecated HierarchyDenormalization now uses a DomainObjectObserver
     */
    public void afterSave() { }
}
