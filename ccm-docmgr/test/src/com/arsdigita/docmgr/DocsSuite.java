/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr;

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
 * @version $Id: //apps/docmgr/dev/test/src/com/arsdigita/docmgr/DocsSuite.java#3 $
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
