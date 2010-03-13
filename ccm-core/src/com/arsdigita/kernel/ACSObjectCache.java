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
package com.arsdigita.kernel;

import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import javax.servlet.ServletRequest;

/**
 * A centralized cache for {@link ACSObject ACSObjects}. At present, it
 * only supports caching of objects in the request. Evenutally, it should
 * support caching objects as request, session and request attributes.
 *
 * <p> For request-scope caching, objects are stored as attributes of the
 * {@link ServletRequest}. The name of the attribute only depends on the
 * object's ID, so that only one copy of each object is stored.
 *
 * @author David Lutterkort
 * @version $Id: ACSObjectCache.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ACSObjectCache {

    private static final Logger s_log =
        Logger.getLogger(ACSObjectCache.class.getName());

    private ACSObjectCache() {
        // nothing, constructor only here to keep people from instantiating
        // this object.
    }

    //
    // Caching of ACSObject in the request attribute
    //
    // FIXME: This should probably go into a separate class

    /**
     * Return the name of the request attribute used to cache the
     * <code>ACSObject</code> with id <code>key</code>. If this is called
     * with two different objects <code>k1</code> and <code>k2</code>, the
     * returned strings will be identical whenever
     * <code>k1.toString().equals(k2.toString())</code>.
     */
    private static String attributeName(Object key) {
        return ACSObject.BASE_DATA_OBJECT_TYPE + ":" + key.toString();
    }

    /**
     * Store <code>obj</code> as a request attribute.
     *
     * @param req the request, in which the object is to be cached.
     * @param obj the object to cache.
     * @pre req != null
     * @pre obj != null &&& obj.getID() != null
     * @post obj.equals(getRequestCache(req, obj.getID()))
     */
    public static void set(ServletRequest req, ACSObject obj) {
        Assert.exists(req);
        Assert.exists(obj);
        Assert.exists(obj.getID());
        req.setAttribute(attributeName(obj.getID()), obj);
    }

    /**
     * Get the <code>ACSObject</code> with ID <code>id</code> from the
     * request <code>req</code>. Return <code>null</code> if the object has
     * not been put into the cache.  If the ID is null then this will
     * return null.
     *
     * @param req the request, in which the object is to be cached.
     * @param id an <code>Object</code> value
     * @return an <code>ACSObject</code> value or null if the id is null
     */
    public static ACSObject get(ServletRequest req, BigDecimal id) {
        if (id == null) {
            return null;
        }
        ACSObject result =  (ACSObject) req.getAttribute(attributeName(id));
        return result;
    }

}
