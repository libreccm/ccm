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
package com.arsdigita.kernel.permissions;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Describes a privilege that can be granted or checked.
 *
 * @author Oumi Mehrotra
 * @version 1.0
 * @version $Id: PrivilegeDescriptor.java 1311 2006-09-04 10:30:53Z sskracic $
 */
public class PrivilegeDescriptor {

    private static final String ADMIN_NAME = "admin";
    private static final String EDIT_NAME = "edit";
    private static final String READ_NAME = "read";
    private static final String WRITE_NAME = "write";
    private static final String CREATE_NAME = "create";
    private static final String DELETE_NAME = "delete";
    /**
     * The PrivilegeDescriptor corresponding to the primitive admin privilege
     */
    public static final PrivilegeDescriptor ADMIN =
                                            new PrivilegeDescriptor(ADMIN_NAME);
    /**
     * The PrivilegeDescriptor corresponding to the read and write privilege
     **/
    public static final PrivilegeDescriptor EDIT =
                                            new PrivilegeDescriptor(EDIT_NAME);
    /**
     * The PrivilegeDescriptor corresponding to the primitive read privilege
     */
    public static final PrivilegeDescriptor READ =
                                            new PrivilegeDescriptor(READ_NAME);
    /**
     * The PrivilegeDescriptor corresponding to the primitive write privilege
     */
    public static final PrivilegeDescriptor WRITE =
                                            new PrivilegeDescriptor(WRITE_NAME);
    /**
     * The PrivilegeDescriptor corresponding to the primitive create privilege
     */
    public static final PrivilegeDescriptor CREATE =
                                            new PrivilegeDescriptor(CREATE_NAME);
    /**
     * The PrivilegeDescriptor corresponding to the primitive delete privilege
     */
    public static final PrivilegeDescriptor DELETE =
                                            //  new PrivilegeDescriptor(DELETE_NAME);
                                            new PrivilegeDescriptor(DELETE_NAME);
    private String m_displayName;
    private static Map s_privs = Collections.synchronizedMap(new HashMap());
    private String m_name;
    private String m_columnName;
    private Collection m_implyingPrivs;
    private static final Logger s_log = Logger.getLogger(
            PrivilegeDescriptor.class);

    /**
     * Adds a privilege, identified by a name (string), to the system.
     *
     * @param name
     * @return
     * @exception PersistenceException when there is a persistence
     * error in saving the new privilege.
     *
     * @see #get(String)
     */
    public static PrivilegeDescriptor createPrivilege(String name)
            throws PersistenceException {
        if (get(name) != null) {
            throw new IllegalArgumentException("Privilege " + name
                                               + " already exists");
        }
        DataObject priv = SessionManager.getSession().create(
                "com.arsdigita.kernel.permissions.Privilege");
        priv.set("privilege", name);
        priv.save();
        addChildPrivilege(ADMIN_NAME, name);
        s_log.debug(String.format("Creating privilege %s...", name));
        // Constructor PrivilegeDescriptor is deprecated and should be
        // replaced by the class method get(name),
	// but this does not work under all circumstances. Further
	// investigation of this issue is neccessary.
        PrivilegeDescriptor desc = new PrivilegeDescriptor(name);
        //PrivilegeDescriptor desc = get(name);
        put(desc);
        return desc;
    }

    /**
     * Sets up a tree structure of privileges, if privilegeName is granted,
     * it includes child PrivilegeName as well.

     * @param privilegeName  privilege name will include another privilege
     * @param childPrivilegeName privilege name is included by priv...Name.
     */
    public static void addChildPrivilege(String privilegeName,
                                         String childPrivilegeName) {

        DataOperation addOp =
                      SessionManager.getSession().retrieveDataOperation(
                "com.arsdigita.kernel.permissions.addChildPrivilege");
        addOp.setParameter("privilege", privilegeName);
        addOp.setParameter("childPrivilege", childPrivilegeName);
        addOp.execute();
        synchronized (s_privs) {
            s_privs.remove(privilegeName);
            PrivilegeDescriptor priv = new PrivilegeDescriptor(privilegeName);
            s_privs.put(privilegeName, priv);

        }
    }

    /**
     * Given a privilege name, returns a privilege descriptor or null
     * if the privilege does not exist on the system.
     *
     * @param privilegeName the name of the privilege to return
     * @return a privilege descriptor, or null if the privilege does not exist.
     *
     * @see #createPrivilege(String)
     */
    public static PrivilegeDescriptor get(String privilegeName) {
        return (PrivilegeDescriptor) s_privs.get(privilegeName);
    }

    /**
     * Returns a collection of privilege descriptors for every privilege in
     * the system.
     * @see #get(String)
     *
     * @return a collection of privilege descriptors.
     */
    public static Collection getAll() {
        return s_privs.values();
    }

    /**
     * Deletes the privilege described by this from the system.
     *
     * @exception PersistenceException when there is a persistence
     * error in saving the new privilege.
     *
     * @see #get(String)
     */
    public void deletePrivilege()
            throws PersistenceException {
        OID oid = new OID("com.arsdigita.kernel.permissions.Privilege",
                          m_name);

        DataObject priv = SessionManager.getSession().retrieve(oid);
        priv.delete();

        s_privs.remove(m_name);
    }

    /**
     * Returns the privilege name.
     *
     * @return the privilege name.
     **/
    public final String getName() {
        return m_name;
    }

    /**
     * Returns the display name for the privilege, or just the
     * privilege name if no display name is defined.
     *
     * @return the display name
     */
    public String getDisplayName() {
        if (m_displayName != null) {
            return m_displayName;
        } else {
            return m_name;
        }
    }

    /**
     * Equivalent to getName().
     * @return the privilege name
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Get column name from privilege - pd_priv_xxx column name mapping.
     * @return the column name from dnm_privilege_col_map table
     */
    public String getColumnName() {
        return m_columnName;
    }

    /**
     * Determines whether this PrivilegeDescriptor is equal to some other
     * PrivilegeDescriptor. Equality is based on privilege name.
     * @return <code>true</code> if the privilege descriptors are equal;
     * <code>false</code> otherwise.
     */
    public boolean equals(Object o) {
        if (!(o instanceof PrivilegeDescriptor)) {
            return false;
        }
        if (m_name == null) {
            return false;
        }
        PrivilegeDescriptor p = (PrivilegeDescriptor) o;

        return m_name.equals(p.getName());
    }

    public int hashCode() {
        return m_name.hashCode();
    }

    /**
     * Create a new privilege descriptor for use with PermissionDescriptor
     * and PermissionService.
     *
     * @param name The name of the privilege.
     * @deprecated see #get
     **/
    public PrivilegeDescriptor(String name) {
        m_name = name;

        DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.kernel.permissions.PrivilegeColumnNameMap");
        query.setParameter("privilege", name);
        if (!query.next()) {
            query.close();
            throw new RuntimeException("Couldn't find column name for privilege - "
                                       + name);
        } else {
            m_columnName = (String) query.get("columnName");
        }
        query.close();
    }

    /**
     * Puts a privilege descriptor into the internal cache that is used by
     * the get method. The put method supports extendibility by allowing
     * subclasses to be returned by the get method.
     * @param privDesc
     */
    protected static void put(PrivilegeDescriptor privDesc) {
        if (privDesc == null) {
            throw new NullPointerException("privDesc is null");
        }
        s_privs.put(privDesc.getName(), privDesc);
    }

    /**
     * Returns the list of privilege names that imply this privilege.
     * @return a collection of the privilege names that imply this privilege.
     */
    public synchronized Collection getImplyingPrivilegeNames() {
        if (m_implyingPrivs != null) {
            return m_implyingPrivs;
        }
        m_implyingPrivs = new HashSet();
        DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.kernel.permissions.ImpliedPrivilege");
        query.setParameter("childPrivilege", m_name);
        while (query.next()) {
            m_implyingPrivs.add((String) query.get("privilege"));
        }
        return m_implyingPrivs;
    }

    /**
     * Determine whether or not this privilege is implied by the
     * specified privilege.
     *
     * @return true if the privilege on which this method is invoked
     * is implied by the specified privilege, false otherwise
     */
    public boolean isImpliedBy(PrivilegeDescriptor privilege) {
        return getImplyingPrivilegeNames().contains(privilege.getName());
    }

    /* this is only necessary ...
     * 1. to support the deprectated constructor.  Someone could construct
     *    a privilege named X that has different state than
     *    PrivilegeDescriptor.get(X).  So we make the methods in this class
     *    rely on getCanonical rather than this.
     * 2. The static constants ADMIN, READ, etc. have to be created before
     *    s_privs is fully initialized (for example, s_privs may be initialized
     *    in client code that extends PrivilegeDescriptor).  Therefore,
     *    ADMIN, READ, etc. are special cases of #1 above.
     */
    private PrivilegeDescriptor getCanonical() {
        return get(this.getName());
    }

    /**
     * Initializes the PrivilegeDescriptor's internal cache of privileges.
     * Reads privileges from database and stores in the internal Map s_privs.
     * Called from the kernel initializer.
     */
    public static void initialize() {
        DataCollection privs = SessionManager.getSession().retrieve(
                "com.arsdigita.kernel.permissions.Privilege");
        while (privs.next()) {
            String name = (String) privs.get("privilege");
            final PrivilegeDescriptor desc = new PrivilegeDescriptor(name);
            put(desc);
        }
        privs.close();
    }
}
