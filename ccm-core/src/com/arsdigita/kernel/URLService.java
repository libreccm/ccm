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
package com.arsdigita.kernel;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.SessionManager;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Supports generically locating a domain object on the site.
 * The URLService can produce the path (relative to the root URL) to a page
 * that displays the domain object identified by a given OID.
 * <P>
 * The service
 * works by delegating to a URLFinder based on the object type of the
 * given OID. A URLFinder must be registered with the URLService for the
 * object type in question or one of its supertypes.  That URLFinder
 * dynamically produces a URL path by a process that potentially involves
 * a few database queries.
 * <p>
 * The URLService is only intended for single-object lookups.  On pages that
 * display many objects (for example, a search results page), the URLs displayed for
 * each object should <em>not</em> be obtained from the URLService (for performance
 * reasons).  Instead, a single "redirector page" should be the target of
 * all those links.  The user clicks on a link,
 * which then uses the URLService to find the object's URL
 * and redirect the user to the resulting URL.  An example of this process
 * is:
 * <ol>
 * <li> User searches for objects based on keywords.
 * <li> Search page displays matching objects, with a link for each
 *      object.  The targets of these links are all the same:
 *          .../display-result?id=:id&object_type=:object_type
 * <li> User clicks on one of those links.
 * <li> Display-result page uses the URLService to find the specified
 *      object's URL and redirect the user there.
 * </ol>
 *
 * The GenericURLFinder class provides a simple URLFinder that can be
 * registered for many object types.
 *
 * @see GenericURLFinder
 *
 * @author Oumi Mehrotra 
 **/
public class URLService {

    public static final String versionId = "$Id: URLService.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Map s_finders = new HashMap();

    /**
     * Returns a URL path to a page that displays the object identified by
     * the given <i>oid</i>. The URL path is relative to the server root.
     * The URL is obtained by delgating to the registered or
     * inherited URLFinder for the given <i>oid</i>'s object type.
     *
     * @throws URLFinderNotFoundException when there is no URLFinder
     * registered for the given <i>oid</i>'s object type nor any of its
     * supertypes.
     *
     * @throws NoValidURLException when the
     * URLFinder registered for the given <i>oid</i>'s object type is unable
     * to produce a valid non-null URL.
     **/
    public static String locate(OID oid)
        throws URLFinderNotFoundException, NoValidURLException {
        return locate(oid,null);
    }
    /**
     * Returns a URL path to a page that displays the object identified by
     * the given <i>oid</i>. The URL path is relative to the server root.
     * The URL is obtained by delgating to the registered or
     * inherited URLFinder for the given <i>oid</i>'s object type.
     *
     * @throws URLFinderNotFoundException when there is no URLFinder
     * registered for the given <i>oid</i>'s object type nor any of its
     * supertypes.
     *
     * @throws NoValidURLException when the
     * URLFinder registered for the given <i>oid</i>'s object type is unable
     * to produce a valid non-null URL.
     **/
    public static String locate(OID oid, String context)
        throws URLFinderNotFoundException, NoValidURLException
    {
        URLFinder f = getFinder(oid.getObjectType());
        if (f==null) {
            throw new URLFinderNotFoundException("There is no URLFinder " +
                                                 "registered for " +
                                                 "data object type " +
                                                 oid.getObjectType().getQualifiedName());
        }
	
        String url = (context == null) ? f.find(oid) : f.find(oid,context);

        if (url == null) {
            throw new NoValidURLException("The URLFinder for " +
                                          oid.getObjectType().getQualifiedName() +
                                          "produced a null URL for " +
                                          oid);
        }
        return url;
    }

    /**
     * Registers a URLFinder for the specified data object type.
     * The registered finder will be
     * used by the locate method for OIDs whose type is equal to
     * the specified type. That is,
     * when <code>locate(x)</code> is executed, the specified
     * <i>finder</i> will be used if the specified
     * <i>dataObjectType</i> is equal to <code>x.getObjectType()</code>.
     *
     * <p>
     * Any object type that does not have a finder registered
     * with this service is not supported by this service.
     *
     * <p>
     * If another finder was already registered for the specified
     * object type, the previous finder is replaced and returned.
     *
     * @param objectType the data object type for which to register
     * the specified finder
     *
     * @param finder the URLFinder that will handle data objects
     * of the specified data object type when the
     * locate method is called
     *
     * @return the previous finder that was
     * registered with this service for this object type.
     **/
    public synchronized static URLFinder registerFinder(ObjectType objectType,
                                                        URLFinder finder) {
        return (URLFinder) s_finders.put(objectType, finder);
    }

    /**
     * Wrapper around registerFinder(ObjectType, URLFinder).
     *
     * @see #registerFinder(ObjectType, URLFinder)
     **/
    public synchronized static URLFinder registerFinder(String objectType,
                                                        URLFinder finder) {
        MetadataRoot meta = SessionManager.getMetadataRoot();
        return (URLFinder) s_finders.put(meta.getObjectType(objectType),
                                         finder);
    }

    /**
     * Returns the URLFinder registered for the given object type.
     **/
    public synchronized static
        URLFinder getRegisteredFinder(ObjectType objectType)
    {
        return (URLFinder) s_finders.get(objectType);
    }

    /**
     * 
     * Returns the URLFinder registered for the given object type.
     *
     **/
    public synchronized static
        URLFinder getRegisteredFinder(String objectType)
    {
        MetadataRoot meta = SessionManager.getMetadataRoot();
        return (URLFinder) s_finders.get(meta.getObjectType(objectType));
    }

    /**
     * Gets the registered or inherited URLFinder for the specified object type.
     * This is the URLFinder that is registered
     * for the specified object type or its closest supertype that has
     * a registered URLFinder. Returns null if there is no supertype that
     * has a registered URLFinder.
     *
     * @param objectType the object type whose registered or inherited
     * URLFinder is to be returned
     *
     * @return the registered or inherited URLFinder for the given object type.
     * Returns null if no finder is registered for the
     * given type or any of its supertypes.
     **/
    public synchronized static URLFinder getFinder(ObjectType objectType) {
        ObjectType type = objectType;
        while (type!=null && !s_finders.containsKey(type)) {
            type = type.getSupertype();
        }
        return (URLFinder) s_finders.get(type);
    }

    /**
     *
     * @see #getFinder(ObjectType)
     **/
    public synchronized static URLFinder getFinder(String objectType) {
        MetadataRoot meta = SessionManager.getMetadataRoot();
        return (URLFinder) s_finders.get(meta.getObjectType(objectType));
    }

    public static OID getNonencodedOID(HttpServletRequest sreq) {

	if (sreq == null) {
	    return null;
	}

	String query = sreq.getQueryString();

	if (query == null) {
	    return null;
	}

	int start = query.indexOf(OID_START);
	if (start == -1) {
	    return null;
	}
	start = start+OID_START.length();

	int end = query.indexOf(OID_END);
	if (end == -1) {
	    end = query.length();
	}

	return OID.valueOf(query.substring(start, end));
    }

    public static final String OID_START = "oid=";
    public static final String OID_END = "&";
}
