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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpUtils;
import java.net.URLEncoder;

/**
 *
 * A URLFinder that can be registered for most object types.  The
 * GenericURLFinder is constructed with a specified URL pattern such
 * as <code>one-ticket?ticket_id=:id</code>.  For a given OID, the
 * URL path is determined as follows:
 *
 *   <ol>
 *   <li> Try to find the package instance to which the specified
 *        object belongs.  The current process involves
 *        examining persistence metadata to try to navigate from the
 *        given OID to its package instance.  This process will eventually
 *        be replaced with a call to some other kernel service (to be
 *        developed) that deals with the scoping of objects to package
 *        instances.  Until then, the process is as follows:
 *        <ul>
 *        <li> Get the data object's "packageInstance" property (if it has
 *             such a property and the property is of type PackageInstance).
 *        <li> If no such property exists, use metadata to figure out
 *             if this data object has a composite role property (a
 *             required, visible property where
 *             <code>com.arsdigita.persistence.metadata.Property.isComposite()==true
 *             </code>).
 *         <li>If a composite role exists,
 *             fetch the composite role, and
 *             then repeat the process with that object.  See the example below.
 *        </ul>
 *   <li> Once the package instance is determined, get the package
 *        instance's primary mount point (see
 *        PackageInstance.getDefaultMountPoint()).
 *   <li> Once the primary mount point is determined, get its URL path.
 *   <li> Append the URL pattern (which was specified to the constructor
 *        of GenericURLFinder).
 *   <li> Substitute terms like <code>:id</code> with values from the given
 *        OID.
 *   </ol>
 *
 *   For example, suppose we have the following PDL fragment:
 *   <blockquote><pre>
 *       model examples;
 *       object type Forum extends ACSObject {
 *           PackageInstance[1..1] packageInstance;
 *       }
 *       object type Message extends ACSObject {...}
 *       association {
 *           Forum[1..1] forum;
 *           composite Message[0..n] messages;
 *           // NOTE: composite means component in PDL
 *       }
 *   </pre></blockquote>
 *
 *   We can register GenericURLFinder with the URLService for both of
 *   these object types with the following code:
 *   <blockquote><pre>
 *      URLService.registerFinder("examples.Forum",
 *                            new GenericURLFinder("index?forum_id=:id"));
 *      URLService.registerFinder("examples.Message",
 *                            new GenericURLFinder("message?message_id=:id"));
 *   </pre></blockquote>
 *
 * The GenericURLFinder registered for <code>examples.Forum</code> will
 * work because <code>examples.Forum</code> has a <code>packageInstance</code>
 * property of type <code>PackageInstance</code>.
 * <p>
 *
 * The finder registered for <code>examples.Message</code> will work
 * because <code>examples.Message</code> has a composite role (in this
 * case it is the role called <code>forum</code>), and the composite
 * object has a <code>packageInstance</code> property of type
 * <code>PackageInstance</code>.
 *
 * @author Oumi Mehrotra
 *
 */
public class GenericURLFinder implements URLFinder {

    public static final String versionId = "$Id: GenericURLFinder.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private String m_base;
    private Map m_params;

    public GenericURLFinder(String urlEndingPattern) {
        m_params = new HashMap();
        m_base = parseQueryString(urlEndingPattern, m_params);
        // setFormat(urlEndingPattern);
    }

    public String find(OID oid, String context) throws NoValidURLException {
	return find(oid);
    }

    public String find(OID oid) throws NoValidURLException {
        DataObject dobj = SessionManager.getSession().retrieve(oid);
        if (dobj==null) {
            throw new NoValidURLException("No such data object " + oid);
        }
        DataObject packageInstanceData =
            getPackageInstanceData(dobj);
        if (packageInstanceData == null) {
            throw new NoValidURLException("Could not find package " +
                                          "instance for " + dobj);
        }
        PackageInstance pkg = new PackageInstance(packageInstanceData);
        SiteNode sn = pkg.getDefaultMountPoint();
        if (sn == null) {
            throw new NoValidURLException("Could not find site node for " +
                                          "package instance " + pkg);
        }
        return sn.getURL() + m_base + unparseQueryString(oid);
    }

    private DataObject getPackageInstanceData(DataObject dobj) {

        if (!isACSObject(dobj.getObjectType())) {
            // For compatibility, the GenericURLFinder still tries to support
            // domain objects that are not ACSObject, using some metadata
            // driven guesswork to find a package instance for the object.
            return guessPackageInstanceData(dobj);
        }

        final String queryName =
            "com.arsdigita.kernel.PackageInstanceForObject";
        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("objectID", dobj.get("id"));
        DataObject pkgInst = null;
        if (query.next()) {
            pkgInst = (DataObject) query.get(MDUtil.PACKAGE_INSTANCE);
        }
        query.close();

        return pkgInst;
    }

    // This is how URLFinder used to work, before the generic container
    // hierarchy was introduced for ACSObject.  Now, this method is
    // never called for an ACSObject.  But I've left in the logic that
    // deals with ACSObject so that I can switch back and forth between
    // the new algorithm and the old algorithm for debugging.
    private DataObject guessPackageInstanceData(DataObject dobj) {
        ObjectType o = dobj.getObjectType();

        if (isACSObject(o)) {
            o = getType( (String) dobj.get(ACSObject.OBJECT_TYPE) );
            dobj.specialize(o);
        }

        if (isPackageInstance(o)) {
            return dobj;
        }
        if (MDUtil.hasPackageInstanceRole(o)) {
            return (DataObject) dobj.get(MDUtil.PACKAGE_INSTANCE);
        }
        // recurse to composite.
        DataObject composite = getComposite(dobj);
        if (composite != null) {
            return guessPackageInstanceData(composite);
        }

        // The object is not a package instance, has no packageInstance role,
        // and has no visible required composite role.  Therefore we can't
        // generically figure out what package instance the object belongs
        // to.
        return null;
    }

    private static DataObject getComposite(DataObject dobj) {
        Property compositeRole = MDUtil.getCompositeRole(dobj.getObjectType());
        if (compositeRole==null) {
            return null;
        } else {
            return (DataObject) dobj.get(compositeRole.getName());
        }
    }

    private static boolean isACSObject(ObjectType type) {
        return (type.isSubtypeOf(getType(ACSObject.BASE_DATA_OBJECT_TYPE)));
    }

    private static boolean isPackageInstance(ObjectType type) {
        return (type.isSubtypeOf(getType(PackageInstance.BASE_DATA_OBJECT_TYPE)));
    }

    private static ObjectType getType(String typeName) {
        return SessionManager.getMetadataRoot().getObjectType(typeName);
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
        if (val.charAt(0)==':') {
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
