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
package com.arsdigita.cms.ui.role;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>This class contains helper methods for creating roles in a
 * content section.</p>
 *
 * @author Michael Pih
 * @version $Id: RoleFactory.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class RoleFactory {
    public static final String versionId = 
        "$Id: RoleFactory.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    public final static String CMS_ROLE_PRIVILEGES =
        "com.arsdigita.cms.getRolePrivileges";
    public final static String PRIVILEGE = "privilege";
    public final static String SORT_ORDER = "sortOrder";

    public final static String CMS_PRIVILEGES =
        "com.arsdigita.cms.getPrivileges";
    public final static String PRETTY_NAME = "prettyName";

    public RoleFactory() {}

    /**
     * Map checked privileges to the new role.
     */
    private static void createPrivileges(Role role, String[] privileges,
                                         ContentSection section) {
        if (privileges != null) {
            for ( int i = 0; i < privileges.length; i++ ) {
                String p = privileges[i];
                if (p.equals(SecurityManager.CMS_CATEGORY_ADMIN) ||
                    p.equals(SecurityManager.CMS_CATEGORIZE_ITEMS)) {
                    RootCategoryCollection coll = Category.getRootCategories(section);
                    while (coll.next()) {
                        if (p.equals(SecurityManager.CMS_CATEGORY_ADMIN)) {
                            role.grantPermission(coll.getCategory(),
                                                 PrivilegeDescriptor.ADMIN);
                        } else {
                            role.grantPermission(coll.getCategory(),
                                                 Category.MAP_DESCRIPTOR);
                        }
                    }
                }
                role.grantPermission(section, PrivilegeDescriptor.get(p));
            }
        }

    }

    /**
     * Map checked privileges to the new role.
     */
    static void updatePrivileges(Role role, String[] privileges,
                                 ContentSection section) {
        Session session = SessionManager.getSession();
        DataQuery dq = session.retrieveQuery(CMS_ROLE_PRIVILEGES);
        Filter filter = dq.addFilter("granteeId = :granteeId");
        filter = dq.addFilter("objectId = :objectId");
        filter.set("granteeId", role.getGroup().getID());
        filter.set("objectId", section.getID());
        dq.addOrder(SORT_ORDER);

        List lst;
        if (privileges == null) {
            lst = Collections.EMPTY_LIST;
        } else {
            lst = Arrays.asList(privileges);
        }
        ArrayList newPrivs = new ArrayList();

        while ( dq.next() ) {
            String priv = (String) dq.get(PRIVILEGE);
            if (lst.indexOf(priv) == -1) {
                role.revokePermission(section, PrivilegeDescriptor.get(priv));
                if (priv.equals(SecurityManager.CMS_CATEGORY_ADMIN) ||
                    priv.equals(SecurityManager.CMS_CATEGORIZE_ITEMS)) {
                    RootCategoryCollection coll = Category.getRootCategories(section);
                    while (coll.next()) {
                        if (priv.equals(SecurityManager.CMS_CATEGORY_ADMIN)) {
                            role.revokePermission(coll.getCategory(),
                                                 PrivilegeDescriptor.ADMIN);
                        } else {
                            role.revokePermission(coll.getCategory(),
                                                 Category.MAP_DESCRIPTOR);
                        }
                    }
                }
            } else {
                newPrivs.add(priv);
            }
        }
        dq.close();

        createPrivileges(role, privileges, section);
    }


    ////////////////////////////
    //
    //   CMS Privilege stuff.
    //


    /**
     * Fetch the CMS privileges associated with a role and content
     * section.
     *
     * @param section The content section
     * @param role The role
     * @return An array of privileges
     */
    public static String[] getRolePrivileges(ContentSection section, Role role) {
        Group roleGroup = role.getGroup();

        DataQuery dq = getRolePrivileges(section.getID(), roleGroup.getID());
        ArrayList privs = new ArrayList((int) dq.size());
        while ( dq.next() ) {
            privs.add((String) dq.get(PRIVILEGE));
        }
        dq.close();

        return (String[]) privs.toArray(new String[0]);
    }

    /**
     * Fetch the CMS privileges associated with a role and content
     * section.
     *
     * @param sectionId The id of the content section
     * @param granteeId The id of the group of the role
     * @return DataQuery containing privileges associated with the
     * role and content section.
     */
    public static DataQuery getRolePrivileges
        (BigDecimal sectionId, BigDecimal granteeId) {

        // Check privileges mapped to this group.
        Session session = SessionManager.getSession();
        DataQuery dq = session.retrieveQuery(CMS_ROLE_PRIVILEGES);
        Filter filter = dq.addFilter("granteeId = :granteeId");
        filter = dq.addFilter("objectId = :objectId");
        filter.set("granteeId", granteeId);
        filter.set("objectId", sectionId);
        dq.addOrder(SORT_ORDER);

        return dq;
    }

    /**
     * Get the pretty name of a CMS privilege.
     * MP: Globalize the privilege label.
     *
     * @param privilege The privilege key
     * @return The pretty name of a privilege
     * @pre ( privilege != null && privilege is a CMS privilege )
     */
    public static String getPrivilegeLabel(String privilege) {

        String result = null;

        DataQuery dq = SessionManager.getSession().retrieveQuery(CMS_PRIVILEGES);
        Filter f = dq.addFilter("privilege = :privilege");
        f.set("privilege", privilege);

        if ( dq.next() ) {
            result = (String) dq.get(PRETTY_NAME);
            dq.close();
        } else {
            throw new IllegalArgumentException(
                                               "Failed to fetch pretty name for the CMS Privilege: " + privilege);
        }

        return result;
    }

}
