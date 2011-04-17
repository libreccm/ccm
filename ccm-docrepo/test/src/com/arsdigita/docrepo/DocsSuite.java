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
package com.arsdigita.docrepo;

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * Document Manager test suite.
 *
 * @author ron@arsdigita.com, sdeusch@arsdigita.com
 * @version $Id: docrepo/DocsSuite.java  pboy $
 */

public class DocsSuite extends PackageTestSuite {

    public static Test suite() {
        DocsSuite suite = new DocsSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget("com.arsdigita.docmgr.installer.Initializer");
        //wrapper.setSetupSQLScript(System.getProperty("test.sql.dir") + "/kernel/setup.sql");
        //wrapper.setTeardownSQLScript(System.getProperty("test.sql.dir") + "/kernel/teardown.sql");
        return wrapper;
    }


    /**
     * Utility method to generate a random user for testing
     */

    static User getRandomUser() {

        String key   = String.valueOf(System.currentTimeMillis());
        String email = key + "-docs-test@arsdigita.com";
        String first = key + "-docs-test-given-name";
        String last  = key + "-docs-test-family-name";

        User user = new User();
        user.setPrimaryEmail(new EmailAddress(email));

        PersonName name = user.getPersonName();
        name.setGivenName(first);
        name.setFamilyName(last);

        user.save();

        return user;
    }

    /**
     * Utility method to generate a random group for testing
     */

    static Group getRandomGroup() {

        String key   = String.valueOf(System.currentTimeMillis());
        String email = key + "-docs-test@arsdigita.com";
        String name  = key + "-docs-test-group-name";

        Group group = new Group();
        group.setPrimaryEmail(new EmailAddress(email));
        group.setName(name);
        group.save();

        return group;
    }
}
