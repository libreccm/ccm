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
package com.arsdigita.dispatcher;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.HashMap;

/**
 * A variable whose value is specific to each request. Objects that
 * need to store values that change in every request should decare
 * them to be <code>RequestValue</code>. These variables hold their
 * values only during a duration of a request. They get reinitialized
 * by a call to {@link #initialValue initialValue} for every new HTTP
 * request.
 *
 * <p> For example, a class that wants to implement a request specific
 * property <code>foo</code> would do the following:
 *
 * <pre>
 * public class SomeClass {
 *   private RequestValue m_foo;
 *
 *   public SomeClass() {
 *     m_foo = new RequestValue() {
 *  protected Object initialValue(HttpServletRequest r) {
 *    // Foo could be a much more complicated value
 *    return r.getRequestURI();
 *  }
 *     };
 *   }
 *
 *   public String getFoo(HttpServletRequest r) {
 *     return (String)m_foo.get(r);
 *   }
 *
 *   public void setFoo(HttpServletRequest r, String v) {
 *     m_foo.set(r, v);
 *   }
 * }
 * </pre>
 *
 * @author Eric Lorenzo
 * @version $Id: RequestValue.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class RequestValue {

    public static final String versionId = "$Id: RequestValue.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final String ATTRIBUTE_KEY =
        "com.arsdigita.dispatcher.RequestValue";

    // Fetch the map used to store RequestValues, possibly creating it
    // along the way
    private Map getMap(HttpServletRequest request) {
        Map result = (Map)request.getAttribute(ATTRIBUTE_KEY);
        if (result == null) {
            // FIXME: This lock is paranoid.  We can remove it if either:
            //
            //     We know that only one thread will be touching a
            //     request object at a time.  (Seems likely, but, like
            //     I said, I'm paranoid)
            //
            //         OR
            //
            //     We modify BaseDispatcherServlet to go ahead and
            //     create the Map and put it into the request at
            //     startup time, in which case we don't have to create
            //     the map here at all.
            //
            synchronized (request) {
                result = (Map)request.getAttribute(ATTRIBUTE_KEY);
                if (result == null) {
                    result = new HashMap();
                    request.setAttribute(ATTRIBUTE_KEY, result);
                }
            }
        }
        return result;
    }

    /**
     * Return the value to be used during the request represented by
     * <code>request</code>. This method is called at most once per
     * request, the first time the value of this
     * <code>RequestValue</code> is requested with {@link #get
     * get}. <code>RequestValue</code> must be subclassed, and this
     * method overridden. Typically, an anonymous inner class will be
     * used.
     *
     *
     * @param request represents the current request
     * @return the initial value for this request local variable
     **/
    protected Object initialValue(HttpServletRequest request) {
        return null;
    }

    /**
     * Return the request specific value for this variable for the
     * request associated with <code>request</code>.
     *
     * @param request represents the current request
     * @return the value for this request local variable
     **/
    public Object get(HttpServletRequest request) {
        Map map = getMap(request);

        Object result;

        // FIXME: More paranoid locking.
        synchronized (map) {
            if (map.containsKey(this)) {
                result = map.get(this);
            } else {
                result = initialValue(request);
                map.put(this, result);
            }
        }

        return result;
    }


    /**
     * This convenience method works like get(HttpServletRequest), but
     * uses DispatcherHelper.getRequest() to get the request object.
     *
     * @return the value for this request-local variable
     **/
    public Object get() {
        return get(DispatcherHelper.getRequest());
    }


    /**
     * <p>Set a new value for the request local variable and associate
     * it with the request represented by <code>request</code></p>
     *
     * @param request represents the current request
     * @param value the new value for this request value
     **/
    public void set(HttpServletRequest request, Object value) {
        Map map = getMap(request);
        // FIXME: More paranoid locking.
        synchronized (map) {
            map.put(this, value);
        }
    }


    /**
     * <p>This convenience method works like set(HttpServletRequest,
     * Object), but uses DispatcherHelper.getRequest() to get the
     * request object.</p>
     *
     * @param request represents the current request
     * @param value the new value for this request value
     **/
    public void set(Object value) {
        set(DispatcherHelper.getRequest(), value);
    }
}
