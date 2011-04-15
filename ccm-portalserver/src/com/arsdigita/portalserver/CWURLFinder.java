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
package com.arsdigita.portalserver;


import com.arsdigita.portalserver.util.GlobalizationUtil;

import com.arsdigita.web.*;
import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import com.arsdigita.domain.*;
import com.arsdigita.kernel.*;
import java.util.*;
import java.net.URLEncoder;
import javax.servlet.http.HttpUtils;

/**
 * <font color="red">Experimental</font>
 *
 * A URLFinder that can be registered for most object types.  The
 * CWURLFinder is constructed with a specified URL pattern such
 * as <code>one-ticket?ticket_id=:id</code>.
 *
 * @author Oumi Mehrotra (oumi@arsdigita.com)
 * @version $Id: CWURLFinder.java $
 **/
public class CWURLFinder implements URLFinder {

    private String m_base;
    private Map m_params;

    /**
     * Constructor
     * @param urlEndingPattern
     */
    public CWURLFinder(String urlEndingPattern) {
        m_params = new HashMap();
        m_base = parseQueryString(urlEndingPattern, m_params);
        // setFormat(urlEndingPattern);
    }

    public String find(OID oid, String context) throws NoValidURLException {
        return find(oid);
    }

    public String find(OID oid) throws NoValidURLException {
        ACSObject obj;

        try {
            obj = (ACSObject) DomainObjectFactory.newInstance(oid);
        } catch (DataObjectNotFoundException e) {
            throw new NoValidURLException("No such data object: " + oid);
        }

        if (obj instanceof Application) {
            return ((Application) obj).getPrimaryURL();
        } else {
            Application app = Application.getContainingApplication(obj);
            if (app == null) {
                throw new NoValidURLException(
                             "Could not find application instance for " + obj);
            }
            return app.getPrimaryURL() + m_base + unparseQueryString(oid);
        }
    }

    /**
     * Copied from com.arsdigita.util.URLRewriter and modified slightly.
     **/
    private String unparseQueryString(OID oid) {
        StringBuffer buf = new StringBuffer(128);
        char sep = '?';
        Iterator keys = m_params.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            Object value = m_params.get(key);
            if (value instanceof String[]) {
                String[] values = (String[])value;
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null) {
                        appendParam(buf, sep, key, getValue(oid, values[i]));
                        sep = '&';
                    }
                }
                continue;
            } else if (value != null) {
                appendParam(buf, sep, key, getValue(oid, value.toString()));
                sep = '&';
            }
        }
        return buf.toString();
    }

    private String getValue(OID oid, String val) {
        if (val.equals(":oid")) {
            return oid.toString();
        } else if (val.charAt(0)==':') {
            return oid.get(val.substring(1)).toString();
        }
        return val;
    }

    //
    // COPIED FROM: com.arsdigita.util.URLRewriter
    //

    private static String parseQueryString(String url, Map params) {
        int qmark = url.indexOf('?');
        if (qmark < 0) {
            return url;
        }
        String base = url.substring(0, qmark);
        String query = url.substring(qmark+1);
        params.putAll(HttpUtils.parseQueryString(query));
        return base;
    }

    /**
     * Appends string representation of a parameter to the given
     * StringBuffer: sep + URLEncode(key) + '=' + URLEncode(value)
     **/
    private static void appendParam(StringBuffer buf, char sep,
                                    String key, String value) {
        buf.append(sep).append(URLEncoder.encode(key))
            .append('=').append(URLEncoder.encode(value));
    }

}
