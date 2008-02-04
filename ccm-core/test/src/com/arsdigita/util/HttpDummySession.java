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
 *
 */
package com.arsdigita.util;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionContext;

public class HttpDummySession implements javax.servlet.http.HttpSession {

    Hashtable m_map = new Hashtable();
    long m_creationTime = System.currentTimeMillis();
    int m_maxInactive = -1;

    public  java.lang.Object getAttribute(java.lang.String name) {
        return m_map.get(name);
    }

    public java.util.Enumeration getAttributeNames() {
        return m_map.keys();
    }

    public long getCreationTime() {
        return m_creationTime;
    }

    public java.lang.String getId() {
        return "dummy_session";
    }

    public long getLastAccessedTime() {
        return System.currentTimeMillis();
    }

    public int getMaxInactiveInterval() {
        return m_maxInactive;
    }

    public HttpSessionContext getSessionContext() {
        return null;
    }

    public ServletContext getServletContext() {
        return null;
    }

    public java.lang.Object getValue(java.lang.String name) {
        return getAttribute(name);
    }

    public java.lang.String[] getValueNames() {
        throw new UnsupportedOperationException();
    }

    public void invalidate() {
        m_map.clear();
    }

    public boolean isNew() {
        return false;
    }

    public void putValue(java.lang.String name, java.lang.Object value) {
        setAttribute(name, value);
    }

    public void removeAttribute(java.lang.String name) {
        m_map.remove(name);
    }

    public  void removeValue(java.lang.String name) {
        removeAttribute(name);
    }

    public void setAttribute(java.lang.String name, java.lang.Object value) {
        m_map.put(name, value);
    }

    public void setMaxInactiveInterval(int interval) {
        m_maxInactive = interval;
    }
}
