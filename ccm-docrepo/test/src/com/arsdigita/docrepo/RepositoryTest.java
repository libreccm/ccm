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
package com.arsdigita.docmgr;

import com.arsdigita.tools.junit.framework.BaseTestCase;


/**
 * Test cases for repositories.
 *
 * @author ron@arsdigita.com
 * @author ddao@arsdigita.com
 * @author gavin@arsdigita.com
 * @version $Id: //apps/docmgr/dev/test/src/com/arsdigita/docmgr/RepositoryTest.java#4 $
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



