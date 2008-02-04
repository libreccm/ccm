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

import com.arsdigita.tools.junit.framework.BaseTestCase;


/**
 * Test cases for repositories.
 *
 * @author ron@arsdigita.com
 * @author ddao@arsdigita.com
 * @author gavin@arsdigita.com
 * @version $Id: //apps/docmgr/dev/test/src/com/arsdigita/docmgr/RepositoryTest.java#2 $
 */

public class RepositoryTest extends BaseTestCase {

    private static final String REPOSITORY_URL = "Repository-test";
    private static final String REPOSITORY_TITLE = "RepositoryTest";
    private static final String WORKSPACE_URL = "workspace-test";
    private static final String WORKSPACE_TITLE = "WorkspaceTest";

    public RepositoryTest(String name) {
        super(name);
    }


    /**
     * Create a Repository.
     */

    public void testRepositoryCreate001() {
        Repository r1 = Repository.create(REPOSITORY_URL,
                REPOSITORY_TITLE,
                null);

        assertNotNull(r1.getRoot());

    }

    public void testRepositoryRetrieveByUser() {
        /*
        User user = DocsSuite.getRandomUser();
        user.save();


        Repository personalRepository = Repository.create(REPOSITORY_URL,
        REPOSITORY_TITLE, null);

        Repository r0 = Repository.retrieveRepository(user);
        assertNotNull(r0.getRoot());
        assertEquals(personalRepository.getID(), r0.getID());

        User user2 = new User(user.getID());

        Repository r1 = Repository.retrieveRepository(user2);
        assertEquals(r0.getID(), r1.getID());
        */
    }



    /**
     * Test creating folders inside a repository.  This test is meant
     * to cover the basic usage of the inbound document handler.
     */

    public void testRepositoryCreateFolders() throws Exception {

        Repository r = Repository.create(REPOSITORY_URL,
                REPOSITORY_TITLE,
                null);
        r.save();

        // Retrieve the root folder

        Folder root = r.getRoot();

        // Verify that sub folders can be created correctly

        assertNotNull(root.createFolders("test/s0"));
        assertNotNull(root.createFolders("test/s1"));
        assertNotNull(root.createFolders("1"));

        // Test several variants of malformed paths, including leading
        // and trailing slashes, and single-character folder names.

        String malformedPath[] = {
            "/test/s0/",
            "/test/s0",
            "test/s0/",
            "/1",
            "/1/",
            "1/"
        };

        for (int i = 0; i < malformedPath.length; i++) {

            Folder f;

            try {
                f = root.retrieveFolder(malformedPath[i]);
                fail("Retrieved malformed path: " + malformedPath[i]);
            } catch (InvalidNameException ex) {
                // Correctly caught the name excepion.  Recover by
                // trying again with a canonicalized path

                f = root.retrieveFolder
                        (Folder.getCanonicalPath(malformedPath[i]));
            }
        }

        // Verify that canonicalizing a null string simply throws an
        // exception

        try {
            Folder.getCanonicalPath(null);
            fail("Canonicalized a null string");
        } catch (InvalidNameException ex) {
            // correct
        }

    }


    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(RepositoryTest.class);
    }

}



