/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.workflow.simple;

import com.arsdigita.db.Sequences;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.TestHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Feb 11, 2004
 * Time: 6:23:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowTestCase extends BaseTestCase {
    public WorkflowTestCase(String name) {
        super(name);
    }

    protected void tearDown() throws Exception {
        TestHelper.setCurrentSystemParty(Kernel.getSystemParty());
        super.tearDown();
    }

    protected User makeNewUser() {
        final String email =  "tristan(" +
                        nextValue() +
                        ")@example.com";

        new KernelExcursion() {
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                User user = new User();
                user.setPrimaryEmail( new EmailAddress(email) );
                user.getPersonName().setGivenName( "Mega Toucus" );
                user.getPersonName().setFamilyName( "Jehosophat" );
                user.save();

            }
        }.run();

        UserCollection users = User.retrieveAll();
        users.addEqualsFilter("primaryEmail", email);
        try {
            if(users.next()) {
                return users.getUser();
            }
        } finally {
            users.close();
        }
        throw new IllegalStateException("Couldn't fetch user " + email);
    }

    protected Group makeNewGroup() {
        final BigDecimal groupNum = nextValue();
        final String email = "group(" +
                        groupNum +
                        ")@arsdigita.com";

      new KernelExcursion() {
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                Group user = new Group();
                user.setPrimaryEmail( new EmailAddress(email) );
                user.setName("Group_" + groupNum);
                user.save();

            }
        }.run();

        GroupCollection groups = Group.retrieveAll();
        groups.addEqualsFilter("primaryEmail", email);
        try {
            if(groups.next()) {
                return groups.getGroup();
            }
        } finally {
            groups.close();
        }
        throw new IllegalStateException("Couldn't fetch user " + email);

    }

    private BigDecimal nextValue() {
        try {
            return Sequences.getNextValue();
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e.getMessage(), e);
        }
    }
}
