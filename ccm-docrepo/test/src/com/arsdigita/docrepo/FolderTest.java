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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.UniqueConstraintException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.versioning.TransactionCollection;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Vector;

/**
 * Test cases for folders;
 *
 * @author ron@arsdigita.com
 * @author sdeusch@arsdigita.com
 * @version $Id: //apps/docmgr/dev/test/src/com/arsdigita/docmgr/FolderTest.java#6 $
 */

public class FolderTest extends BaseTestCase {

    final static String TEST_FILE_NAME = "file.html";
    final static String TEST_IMAGE_NAME = "file.gif";

    final static String TEST_FILE_PATH =
            System.getProperty("test.base.dir") + "/com/arsdigita/docmgr/" + TEST_FILE_NAME;
    final static String TEST_IMAGE_PATH =
            System.getProperty("test.base.dir") + "/com/arsdigita/docmgr/" + TEST_FILE_NAME;

    private static final Logger s_log = Logger.getLogger(FolderTest.class);
    public FolderTest(String name) {
        super(name);
    }

    /**
     * Tests to make sure uniqueness constraints work as intended
     */
    public void testConstraints() throws Exception {

        Vector folderNames = new Vector();
        folderNames.addElement("textxx/subfolder1");
        folderNames.addElement("textxx/subfolder2");

        Folder root = new Folder("ROOT", "root");
        root.save();

        Vector folders = new Vector();
        for (int j = 0; j < folderNames.size(); j++) {
            String folderName = (String) folderNames.elementAt(j);
            try {
                Folder currentFolder = root.retrieveFolder(folderName);
                folders.addElement(currentFolder);
            } catch (DataObjectNotFoundException notFound) {
                Folder currentFolder = root.createFolders(folderName);
                folders.addElement(currentFolder);
            }
        }

        assertEquals(2, folders.size());
    }

    /**
     * Create a folder.
     */

    public void testFolderCreate() throws Exception {
        String name = "f001";
        String desc = "f001 folder";

        Folder f = new Folder(name, desc);
        f.save();
        assertEquals(f.getName(), name);
        assertEquals(f.getDescription(), desc);
    }

    /**
     * Create subfolders and verify that a parent cannot contain two
     * folders with the same name.
     */

    public void testFolderUniquenes() throws Exception {
        String name = "f002";
        String desc = "f002 folder";

        Folder f0 = null;
        Folder f1 = null;
        f0 = new Folder(name, desc);
        f0.save();

        f1 = new Folder(name, desc, f0);
        f1.save();

        try {
            Folder f2 = new Folder(name, desc, f0);
            f2.save();
            fail("Created identical sub folders");
        } catch (ResourceExistsException ex) {
            // triggered unique contraint violation
        }
    }

    /**
     * Retrieve sub folders by name.
     */

    public void testFolderRetrieveByName() throws Exception {
        String name = "f003";
        String desc = "f003 folder";

        Folder fldr[] = new Folder[3];

        fldr[0] = new Folder(name,desc);
        fldr[0].save();
        for (int i = 1; i < fldr.length; i++) {
            fldr[i] = new Folder(name,desc,fldr[i-1]);
            fldr[i].save();
        }

        // Set up the path elements.  This array will store the
        // relative path from the root folder to each sub folder.

        String path[] = new String[fldr.length];
        path[0] = "";
        path[1] = name;
        for (int i = 2; i < path.length; i++) {
            path[i] = path[i-1] + Resource.SEPARATOR + name;
        }

        // Retrieve each sub folder by path

        for (int i = 1; i < fldr.length; i++) {
            Folder f = null;
            try {
                f = fldr[0].retrieveFolder(path[i]);
            } catch (DataObjectNotFoundException ex) {
                fail(ex.getMessage());
            } catch (InvalidNameException ex) {
                fail(ex.getMessage());
            }

            assertNotNull(f);
            assertEquals(fldr[i].getID(), f.getID());
        }

        // Verify that retrieving a non-existing path returns null

        try {
            fldr[0].retrieveFolder("non-existent-path");
            fail("Retrieved a non-existent path");
        } catch (DataObjectNotFoundException ex) {
            // ignore
        }
    }

    /**
     * Create sub folders using relative path names.
     */

    public void testFolderCreateByPath() throws Exception {
        String name = "f004";
        String desc = "f004 folder";

        // Create the root folder

        Folder root = null;
        root = new Folder(name,desc);
        root.save();

        // Create all path elements (relative to root)

        String path[] = new String[4];
        path[0] = root.getName();
        for (int i = 1; i < path.length; i++) {
            path[i] = path[i-1] + Folder.SEPARATOR + name;
        }

        // Create the first and last sub folders

        root.createFolders(path[1]);
        root.createFolders(path[path.length-1]);

        // Verify that all intermediate folders were also created

        for (int i = 1; i < path.length; i++) {
            Folder f = null;
            f = root.retrieveFolder(path[i]);
            assertNotNull(f);
        }

        // Verify that trying to create an existing folder fails

        try {
            root.createFolders(path[path.length-1]);
            fail("Created duplicate folder");
        } catch (ResourceExistsException ex) {
            // caught the unique constraint violation
        }
    }


    /**
     * Retrieve a file in a folder
     */

    public void testFolderRetrieveFile() throws Exception {
        // create parent folder with one file
        String name = "f006";
        String desc = "f006 folder";
        Folder parent = null;
        File f1 = null;
        parent = new Folder(name,desc);
        parent.save();
        s_log.warn("");
        final String mimeType = Util.guessContentType(TEST_FILE_NAME, null);
        f1 = new File(parent);
        f1.setContent(new java.io.File(TEST_FILE_PATH),TEST_FILE_NAME,
                "test file",
                mimeType);
        f1.save();
        s_log.warn("");
        s_log.warn("File name:" + f1.getDisplayName());

        s_log.warn("Folder path:" + parent.getPath());
        s_log.warn("File path: " + f1.getPath());
        // retrieve file by name
        File f2 = null;
        f2 = parent.retrieveFile(TEST_FILE_NAME);

        assertNotNull(f2);
        assertEquals(f1.getID(), f2.getID());
        assertEquals(f1.getPath(), f2.getPath());

        // load a non-existing file
        f2 = null;
        try {
            f2 = parent.retrieveFile("nonExistingFileName");
            fail("Loaded non-existing File");
        } catch(DataObjectNotFoundException e) {
            // correctly caught the exception
        }
    }


    /**
     * Verify that a folder cannot be deleted if it contains files or
     * other folders.
     */

    public void testFolderDelete() throws Exception {
        String name = "root";
        String desc = "root folder";

        Folder root = new Folder(name,desc);
        root.save();

        // add a subfolder
        Folder sub = new Folder(name,desc,root);
        sub.save();

        // add a file
        File file = new File(name+".txt",desc,root);
        file.save();

        root.delete();
    }

    /**
     * Test moving a folder to a new location.
     */

    public void testFolderMove() throws Exception {
        Folder root = buildFolderTree();

        // Create one additional subfolder inside the folder we'll be
        // moving.

        Folder child = null;
        // pre  /root/f0/f0
        // post /root/f0/f0/f0
        child = root.createFolders("f0/f0/f0");
        child.save();
        assertEquals("/root/f0/f0/f0", child.getPath());
        assertEquals("f0", child.getName());
        // Try moving /root/f0/f0 to /root/f2

        Folder target = null;
        target = root.retrieveFolder("f0/f0");

        assertEquals(target.getID(), child.getParentResourceID());
        assertEquals("/root/f0/f0", target.getPath());
        target.setName("f2");
        target.save();
        assertEquals("/root/f0/f2", target.getPath());
        assertEquals("f2", target.getName());

        target.setParent(root);
        target.save();

        assertEquals("/root/f2", target.getPath());
        assertEquals(target.getID(), child.getParentResourceID());
        try {
            root.retrieveFolder("f0/f0");
            fail("Folder should no longer be in this location");
        } catch (DataObjectNotFoundException ex) {
            // correct
        }

        // Try retrieving the original subfolder f0/f0 using its new
        // path.

        Folder sub = null;
        sub = root.retrieveFolder("f2");

        assertEquals(sub.getID(), target.getID());
        assertEquals("/root/f2",  sub.getPath());

        // Try retrieving the subfolder from the new location
        sub = new Folder(child.getID());
        System.out.println("EXPECT: ");
        System.out.println("/root/f2/f0");
        System.out.println("Actual:");
        System.out.println(sub.getPath());

        // Needed because of session caching. The DataOperation that updates the child
        // paths happens behind the scenes of the cache. The retrieve operation
        // will flush the cache for the type.
        DataCollection folders = SessionManager.getSession().retrieve(Folder.BASE_DATA_OBJECT_TYPE);
        folders.addEqualsFilter("id", child.getID());
        folders.next();

        sub = new Folder(folders.getDataObject());
        System.out.println("EXPECT: ");
        System.out.println("/root/f2/f0");
        System.out.println("Actual:");
        System.out.println(sub.getPath());

        assertEquals("/root/f2/f0", sub.getPath());

    }

    /**
     * Verify that a folder can't be moved to one of its own
     * subdirectories
     */

    public void testFolderMoveCycle() throws Exception {
        try {
            Folder root = buildFolderTree();
            Folder destination = root.createFolders("f0/f0/f0/f0/f0");
            Folder source = root.retrieveFolder("f0/f0");

            System.out.println("testFolderMoveCycle sourcepath: "  + source.getPath());
            System.out.println("testFolderMoveCycle destpath: "  + destination.getPath());
            source.setParent(destination);
            source.save();
            fail("moved a folder into one of its sub folders");
        } catch (ResourceException ex) {
            // Correct, this should NOT work!
        }
    }


    /**
     * Verify that a folder and all of its content can be copied.
     */

    public void testFolderCopy() throws Exception {

        Folder root = buildFolderTree();

        Folder orig = null;
        orig = root.retrieveFolder("f0");

        // Place some children into the subfolder

        String desc = "child";
        String child[] = {
            "child-0",
            "child-1"
        };

        for (int i = 0; i < child.length; i++) {
            File f = new File(child[i], desc, orig);
            f.save();
        }

        // Copy the subfolder to a new root-level folder named "f2"

        Folder copy = (Folder) orig.copyTo("f2", root);

        // Verify that the children were copied successfully

        for (int i = 0; i < child.length; i++) {
            BigDecimal id = copy.getResourceID(child[i]);
            File f = new File(id);
            assertEquals(child[i], f.getName());
            assertEquals(desc, f.getDescription());
        }
    }

    /**
     * Test factory method for instantiating folders.
     */

    public void testFolderFactory() throws Exception {

        Folder root = buildFolderTree();
        Folder fldr = null;
        OID oid = new OID(ResourceImpl.BASE_DATA_OBJECT_TYPE,
                root.getID());
        fldr = (Folder) DomainObjectFactory.newInstance(oid);

        assertEquals(root.getID(),          fldr.getID());
        assertEquals(root.getName(),        fldr.getName());
        assertEquals(root.getDescription(), fldr.getDescription());
    }


    /**
     * Build a folder tree for testing, of the following form:
     *
     * root/f0
     * root/f0/f0
     * root/f0/f1
     * root/f1
     * root/f1/f0
     * root/f1/f1
     *
     * @return the root of the folder tree
     */

    protected static Folder buildFolderTree() throws Exception {
        String name = "root";
        String desc = "root folder";
        Folder root = null;

        String folderNames[] = {
            "f0/f0",
            "f0/f1",
            "f1/f0",
            "f1/f1"
        };

        root = new Folder(name,desc);
        root.save();

        for (int i = 0; i < folderNames.length; i++) {
            root.createFolders(folderNames[i]);
        }
        return root;
    }

    /**
     * List the contents of a folder (for debugging)
     */

    private void list(Folder folder) {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
                ("com.arsdigita.docmgr.getResourceTree");
        query.setParameter("startFolderID", folder.getID());

        System.out.println("----- Listing folder " + folder.getName());
        while (query.next()) {
            System.out.println((BigDecimal) query.get("id") + " " +
                    (String) query.get("name") + " " +
                    (String) query.get("path"));
        }
        query.close();
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(FolderTest.class);
    }
}
