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
package com.arsdigita.xmlutil;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.util.UncheckedWrapperException;
import org.jdom.Namespace;

public abstract class PermissionsAction extends TestAction {
    public PermissionsAction(String name) {
        super(name);
    }

    public PermissionsAction(String name, Namespace ns) {
        super(name, ns);
    }

    public Party getUser() {
        String email = getAttributeValue("user");

        if ("swa@redhat.com".equals(email)) {
            return Kernel.getSystemParty();
        }

        PartyCollection coll = Party.retrieveAllParties();
        coll.addEqualsFilter("primaryEmail", email);
        if (coll.next()) {
            Party user = coll.getParty();
            return user;
        } else {
            throw new RuntimeException("No such user: " + email);
        }
    }

    public void execute() throws Exception {

        KernelExcursion ex = new KernelExcursion() {
            protected void excurse() {
                setEffectiveParty(getUser());
                try {
                    doPermissionTest();
                } catch(Exception e) {
                    throw new UncheckedWrapperException("Permissions Action failure", e);
                }

            }
        };

        ex.run();
    }

    public abstract void doPermissionTest() throws Exception;
}
