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
package com.arsdigita.search.intermedia;

import com.arsdigita.persistence.GenericDataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import java.math.BigDecimal;

/**
 *
 *
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public class SearchDataQuery extends GenericDataQuery {

    public static final String OBJECT_TYPE = "object_type";
    public static final String OBJECT_ID = "object_id";
    public static final String SUMMARY = "summary";
    public static final String LINK_TEXT = "link_text";
    public static final String URL_STUB = "url_stub";
    public static final String SCORE = "score";

    public SearchDataQuery(Session s, String sql, String[] columns) {
        super(s, sql, columns);
    }

    /**
     * Return the OID of the result
     */
    public OID getOID() {
        return new OID(getObjectType(), getID());
    }

    public String getObjectType() {
        return (String)get(OBJECT_TYPE);
    }

    public BigDecimal getID() {
        return (BigDecimal)get(OBJECT_ID);
    }

    public String getSummary() {
        return (String)get(SUMMARY);
    }

    public String getLinkText() {
        return (String)get(LINK_TEXT);
    }

    public String getUrlStub() {
        return (String)get(URL_STUB);
    }

    public BigDecimal getScore() {
        return (BigDecimal)get(SCORE);
    }

    // XXX: I'm removing this temporarily. See comment in
    //      SimpleSearchSpecificaiton.java rhs@mit.edu

    /*    void dobind(String name, Object value) {
          super.bind(name, value);
          } */
}
