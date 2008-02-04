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
package com.arsdigita.forum;

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * Forum test suite.
 *
 * @author <a href="mailto:manu.nath@devlogics.com">Manu R Nath</a>
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 * @since ACS 4.6.5
 */

public class BboardSuite extends PackageTestSuite {

    public BboardSuite() {
        super();
    }

    public BboardSuite(Class theClass) {
        super(theClass);
    }

    public BboardSuite(String name) {
        super(name);
    }


    public static Test suite() {
        BboardSuite suite = new BboardSuite();
        populateSuite(suite);

        BaseTestSetup wrapper = new CoreTestSetup(suite);
        return wrapper;
    }


    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static  User createUser() {

        User user = new User();
        user.getPersonName().setGivenName("test-given-name");
        user.getPersonName().setFamilyName("test-family-name");

        String email = "test-user-" + user.getID() + "@localhost";
        user.setPrimaryEmail(new EmailAddress(email));
        user.save();

        return user;
    }

}
