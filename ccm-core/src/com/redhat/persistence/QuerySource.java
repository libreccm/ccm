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
package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.oql.Query;

/**
 * QuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: QuerySource.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class QuerySource {

    private Session m_ssn = null;

    void setSession(Session ssn) {
        m_ssn = ssn;
    }

    public Session getSession() {
        return m_ssn;
    }

    public Query getQuery(ObjectType type) {
        throw new UnsupportedOperationException();
    }

    public Query getQuery(PropertyMap keys) {
        throw new UnsupportedOperationException();
    }

    // These should probably be changed to take type signatures.
    public Query getQuery(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Query getQuery(Object obj, Property prop) {
        throw new UnsupportedOperationException();
    }

}
