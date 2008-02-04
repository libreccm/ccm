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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.util.ArrayList;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test that roles work well with the permissions system
 *
 *
 * @see com.arsdigita.kernel.Role
 * @see com.arsdigita.kernel.Permission
 *
 * @author Michael Bryzek
 * @version 1.0
 **/

public class RolePermissionTest extends BaseTestCase {

    public static final String versionId = "$Id: RolePermissionTest.java 744 2005-09-02 10:43:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Group m_group;
    private ArrayList m_roles;
    private String[] m_roleNames =
        new String[] { "Author", "Editor", "Administrator" };

    /**
     * Constructs a RolePermissionTest with the specified name.
     *
     * @param name Test case name.
     **/
    public RolePermissionTest( String name ) {
        super( name );
    }

    public void setUp() {
        m_group = GroupTest._createGroup("RolePermissionTest Group");
        m_group.save();

        m_roles = new ArrayList();
        for (int i=0; i<m_roleNames.length; i++) {
            Role role = RoleTest.createRole(m_group, m_roleNames[i]);
            role.addMember(RoleTest.createUser("User" + i));
            role.save();
            try {
                m_roles.add(new Role(role.getOID()));
            } catch (DataObjectNotFoundException e) {
                e.printStackTrace();
                fail("Could not retrieve role after creation");
            }

        }
    }

    public void tearDown() {
        m_group = null;
        m_roles = null;
    }


    /**
     * Tests that we can add members to various roles in a group and
     * retrieve them correctly.
     **/
    public void testAssigningPermissions() {
        // Let's grant people write permissions on a user
        ACSObject target = RoleTest.createUser("testAssigningPermissionsGrantee");
        PrivilegeDescriptor priv = PrivilegeDescriptor.WRITE;

        Iterator iter = m_roles.iterator();
        while (iter.hasNext()) {
            Role role = (Role) iter.next();
            role.grantPermission(target, priv);
            if (! role.checkPermission(target, priv) ) {
                fail("Grant of permission failed");
            }

            role.revokePermission(target, priv);
            if (role.checkPermission(target, priv) ) {
                fail("Revoke of permission failed");
            }
        }
    }


    public static Test suite() {
        return new TestSuite(RolePermissionTest.class);
    }

}
