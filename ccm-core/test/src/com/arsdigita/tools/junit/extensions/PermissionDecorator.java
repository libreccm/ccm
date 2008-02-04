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
package com.arsdigita.tools.junit.extensions;

import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import junit.extensions.TestDecorator;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 *
 * @author Jon Orris (jorris@redhat.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class PermissionDecorator extends TestDecorator {
    public PermissionDecorator(Test test) {
        super(test);
    }

    public void run(TestResult testResult) {
        final TestResult finalResult = testResult;
        KernelExcursion ex = new KernelExcursion() {

            protected void excurse() {
                setParty(getAdminUser());
                PermissionDecorator.super.run(finalResult);
            }
        };

        ex.run();
    }

    public static User getAdminUser() {
        UserCollection uc = User.retrieveAll();

        try {
            while(uc.next()) {
                User sysadmin = uc.getUser();
                if (PermissionService.checkPermission(new UniversalPermissionDescriptor
                        (PrivilegeDescriptor.ADMIN, sysadmin))) {
                    System.err.println("Sysadmin: " + sysadmin);
                    return sysadmin;
                }
            }
        } finally {
            uc.close();
        }
        throw new IllegalStateException("No admin found!");
    }

}
