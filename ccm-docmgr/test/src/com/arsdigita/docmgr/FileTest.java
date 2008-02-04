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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mail.ByteArrayDataSource;
import com.arsdigita.persistence.OID;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.versioning.TransactionCollection;

import javax.activation.DataHandler;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Test cases for files.
 *
 * @author ron@arsdigita.com
 * @author stefan@arsdigita.com
 * @version $Id: //apps/docmgr/dev/test/src/com/arsdigita/docmgr/FileTest.java#4 $
 */

public class FileTest extends BaseTestCase {

    private Folder m_parent;

    final static String TEST_IMAGE =
            System.getProperty("test.base.dir") + "/com/arsdigita/docmgr/file.gif";
    final static String TEST_FILE =
            System.getProperty("test.base.dir") + "/com/arsdigita/docmgr/file.html";
    final static String TEST_FILE_NOEXT =
            System.getProperty("test.base.dir") + "/com/arsdigita/docmgr/file";

    private static final org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(FileTest.class);

    public FileTest(String name) {
        super(name);
    }

    /**
     * Create a parent folder to store files in.
     */

    protected void setUp() throws Exception {
        super.setUp();
        String name = "file-test-folder";
        String desc = null;

        m_parent = new Folder(name,desc);
        m_parent.save();

        s_log.error("Starting test " + getName());
    }


    protected void tearDown() throws Exception {
        s_log.error("Ending test " + getName());
        super.tearDown();
    }

    /**
     * Create a File.
     */
    public void testFileCreate001() throws Exception {
        String name = "f001";
        String desc = "f001 description";

        File f = new File(name,desc,m_parent);
        f.save();
        assertEquals(f.getName(), name);
        assertEquals(f.getDescription(), desc);
    }

    /**
     * Create a File and use it to store an HTML document.  This is
     * the common operation used when transferring an uploaded file
     * into the document manager.
     */

    public void testFileCreate002()  throws Exception {
        String name = "f002";
        String desc = "f002 description";

        java.io.File src = new java.io.File(TEST_FILE);

        File f1 = null;
        f1 = new File(m_parent);
        f1.setContent(src,name,desc);
        f1.save();

        assertEquals(f1.getName(), name);
        assertEquals(f1.getDescription(), desc);
        assertEquals(f1.getContentType(), "text/html");

        File f2 = null;
        f2 = new File(f1.getID());

        assertEquals(f1.getName(), f2.getName());
        assertEquals(f1.getDescription(), f2.getDescription());
        assertEquals(f1.getContentType(), f2.getContentType());
        assertEquals(f1.getSize(), f2.getSize());
        assertNotNull(f1.getRawContent());
        assertNotNull(f2.getRawContent());
        assertEquals(new String(f1.getRawContent()),
                new String(f2.getRawContent()));
    }

    /**
     * Create a File and use it to store an image.
     */

    public void testFileCreate003() throws Exception {
        String name = "f003";
        String desc = "f003 description";

        java.io.File src = new java.io.File(TEST_IMAGE);

        File f1 = null;
        f1 = new File(m_parent);
        f1.setContent(src,name,desc);
        f1.save();

        assertEquals(f1.getName(), name);
        assertEquals(f1.getDescription(), desc);
        assertEquals(f1.getContentType(), "image/gif");

        File f2 = null;
        f2 = new File(f1.getID());

        assertEquals(f1.getName(),        f2.getName());
        assertEquals(f1.getDescription(), f2.getDescription());
        assertEquals(f1.getContentType(), f2.getContentType());
    }

    /**
     * Create a File and use it to store data encapsulated by a
     * DataHandler.  This is the method typically used to transfer
     * content from an email attachment into the document manager. 
     */

    public void testFileCreate004() throws Exception {
        String name = "f004.txt";
        String desc = "f004 description";
        String type = "text/plain";
        String text = "hello, world";

        // Store a plain text "document" in the File
        DataHandler dh = new DataHandler
                (new ByteArrayDataSource(text, type, name));

        File f1 = null;
        f1 = new File(m_parent);
        f1.setDataHandler(dh);
        f1.setDescription(desc);
        f1.save();

        assertEquals(name, f1.getName());
        assertEquals(desc, f1.getDescription());
        assertEquals(type, f1.getContentType());
        assertEquals(text, new String(f1.getRawContent()));

        File f2 = null;
        f2 = new File(f1.getID());

        assertEquals(name, f2.getName());
        assertEquals(desc, f2.getDescription());
        assertEquals(type, f2.getContentType());
        assertEquals(text, new String(f2.getRawContent()));
    }

    /**
     * Verify that a file can be moved from one folder to another. 
     */

    public void testFileMove() throws Exception {
        Folder root = FolderTest.buildFolderTree();

        Folder f0 = null;
        Folder f1 = null;

        f0 = root.retrieveFolder("f0");
        f1 = root.retrieveFolder("f1");

        String name = "file";
        String desc = "file description";
        File file = null;

        file = new File(name,desc,f0);
        file.save();

        assertEquals(file.getID(), f0.getResourceID(name));

        // Move the file from f0 to f1

        file.setParent(f1);
        file.save();

        // Verify the file no longer exists in old location

        try {
            f0.getResourceID(name);
            fail("Retrieved file from old location");
        } catch (DataObjectNotFoundException ex) {
            // correct
        }

        // Retrieve the file from the new location

        BigDecimal id = new BigDecimal(-1);
        id = f1.getResourceID(name);


        assertEquals(file.getID(), id);
        assertEquals(file.getPath(), f1.getPath() + Resource.SEPARATOR + name);

    }


    /**
     * Verify that a file can be copied.
     */

    public void testFileCopy() throws Exception {
        String name = "file";
        String desc = "file desc";

        java.io.File src = new java.io.File(TEST_FILE);

        File orig  = new File(m_parent);
        orig.setContent(src,name,desc);
        orig.save();

        File copy = null;
        copy = (File) orig.copyTo(name + "-copy");

        assertEquals(orig.getDescription(), copy.getDescription());
        assertEquals(orig.getContentType(), copy.getContentType());


    }

    /**
     * Test factory method for instantiating files.
     */

    public void testFileFactory() throws Exception {

        String name = "file";
        String desc = "file desc";

        java.io.File src = new java.io.File(TEST_FILE);

        File f0 = new File(m_parent);
        f0.setContent(src,name,desc);
        f0.save();

        OID oid = new OID(File.BASE_DATA_OBJECT_TYPE,
                f0.getID());
        File f1 = (File) DomainObjectFactory.newInstance(oid);

        assertEquals(f0.getID(),          f1.getID());
        assertEquals(f0.getName(),        f1.getName());
        assertEquals(f0.getDescription(), f1.getDescription());
        assertEquals(f0.getContentType(), f1.getContentType());
    }

    /**
     * Test basic revision history.
     */

    public void testFileRevisions() throws Exception  {

        String name = "file";
        String desc = "file-description";
        File   file = new File(name,desc,m_parent);

        // Save several revisions of this file
        int numRevisions = 10;

        // Store the content and symbolic revision tags in arrays so
        // we can use them below.

        String content[] = new String[numRevisions];
        String tag[] = new String[numRevisions];

        for (int i = 0; i < numRevisions; i++) {
            content[i] = "file-content-" + i;
            file.setText(content[i]);
            file.save();

            // Apply a tag to the last transaction
            tag[i] = file.applyUniqueTag(String.valueOf(i) + "-");
        }

        // Revert each transaction back to the original and verify
        // that the content is correctly recovered.

        TransactionCollection tc = file.getTransactions();
        assertEquals(numRevisions, tc.size());

        for (int i = numRevisions-1; i >= 0; i--) {
            assertTrue(tc.next());
            file.rollBackTo(tc.getTransaction());
            assertEquals(content[i], new String(file.getRawContent()));
            file.save();
        }

        // Rollback to an intermediate revision
        int targetRevision = 5;
        file = new File(file.getID());
        file.rollBackTo(tag[targetRevision]);
        assertEquals(content[targetRevision],
                new String(file.getRawContent()));
    }

    public void testFileDisplayName() throws Exception {

        // Create a hash map with file name as a key mapping to
        // expected value

        HashMap fileNames = new HashMap();
        fileNames.put("f001.txt",  "f001");
        fileNames.put("file.html", "file");
        fileNames.put("file.htm", "file");
        fileNames.put("file name", "file name");
        fileNames.put(".file", ".file");
        fileNames.put("file name.doc", "file name");
        fileNames.put(".a.b.c", ".a.b.c");
        fileNames.put("file a.b.c", "file a.b");
        fileNames.put("a.b.c", "a.b");

        File file = new File(m_parent);

        Iterator iter = fileNames.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            String displayName = (String) entry.getValue();

            file.setName(name);
            assertEquals(displayName, file.getDisplayName());
        }
    }

    /**
     * Verify that file MIME types cannot be changed once the file is
     * persistent. 
     */

    public void testFileTypeChange() throws Exception {

        String name = "file.html";
        String desc = "file desc";

        java.io.File src = new java.io.File(TEST_FILE);

        File file = new File(m_parent);
        file.setContent(src,name,desc);
        file.save();

        assertEquals("text/html", file.getContentType());

        // Update properties of the file and verify that changes can
        // be saved.

        file.setName("a new name");
        file.setDescription("a new description");
        file.save();

        // Try setting the content to a different MIME type and saving
        // the file.

        try {
            src  = new java.io.File(TEST_IMAGE);
            name = "file.gif";
            file.setContent(src,name,desc);
            file.save();
            fail("Changed MIME type of existing file");
        } catch (TypeChangeException ex) {
            // correct
        }
    }

    /**
     * Create an HTML file for testing. The file is initialized but
     * not saved.
     */

    private File createTestFile() throws Exception {

        String name = "file.html";
        String desc = "file desc";

        java.io.File src = new java.io.File(TEST_FILE);

        File file = new File(m_parent);
        file.setContent(src,name,desc);
        file.initializeContentType(null);
        return file;
    }

    /**
     * Verify the behavior of rules governing file names and changes
     * to file names.
     */

    public void testFileNameRules() throws Exception {

        File file = createTestFile();
        file.save();

        HashMap fileExtensions = new HashMap();
        fileExtensions.put("file", "file.html");
        fileExtensions.put("file.html", "file.html");
        fileExtensions.put("file.txt", "file.txt");

        // Test the appendExtension utility to verify that it
        // correctly constructs file names with inherited extensions.

        Iterator iter = fileExtensions.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            String expected = (String) entry.getValue();

            assertEquals("Failed generating extension for " + name,
                    expected,
                    file.appendExtension(name));
        }

        // Verify the set of file names that are valid for renaming
        // our test file.

        HashMap fileNames = new HashMap();
        fileNames.put("f001.txt",  Boolean.FALSE);
        fileNames.put("f001.htm",  Boolean.TRUE);
        fileNames.put("f001", Boolean.TRUE);
        fileNames.put("f001 bar", Boolean.TRUE);
        fileNames.put("foo bar .htm", Boolean.TRUE);

        iter = fileNames.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            Boolean isValid = (Boolean) entry.getValue();

            assertEquals("Failed testing the validity of " + name,
                    isValid.booleanValue(),
                    file.isValidNewName(name));
        }
    }


    /**
     * Test the correct initialize of MIME type from an HTTP request
     * with and without an explicit content type header. 
     */
    public void testFileUpload() throws Exception {

        String name = "file";
        String desc = "file desc";

        TestServletRequest request = new TestServletRequest();
        request.setContentType("text/html");

        java.io.File src = new java.io.File(TEST_FILE);

        File file = new File(m_parent);
        file.setContent(src,name,desc);

        file.initializeContentType(request);

        file.save();
        assertEquals("Failed to set the content type from the request",
                "text/html",
                file.getContentType());

        // Verify that we can't reset the content type after the file
        // has been saved

        try {
            file.initializeContentType(request);
            fail("Re-initialized content type");
        } catch (ContentTypeException ex) {
            // correct
        }

        // Verify that the content type is initialized correctly from
        // the file extension if no content type can be determined
        // from the request.  Two cases: no request, and no content
        // type header

        request.setContentType(null);
        file = new File(m_parent);
        file.setContent(src,name + ".html", desc);

        file.initializeContentType(request);
        assertEquals("text/html", file.getContentType());
        file.delete();

        file = new File(m_parent);
        file.setContent(src,name + ".html", desc);

        file.setContentType(null);
        file.initializeContentType(null);
        assertEquals("text/html", file.getContentType());

        // Try a file with no extension, no content-type header, and
        // verify that the default mime type is set

        src = new java.io.File(TEST_FILE_NOEXT);
        file = new File(m_parent);
        file.setContent(src,name,desc);
        file.initializeContentType(null);
        assertEquals(File.DEFAULT_MIME_TYPE,
                file.getContentType());
    }



    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(FileTest.class);
    }
}
