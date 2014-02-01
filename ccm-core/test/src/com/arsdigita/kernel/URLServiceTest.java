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
package com.arsdigita.kernel;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDL;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Tests basic functionality of kernel classes
 *
 *
 * @author Oumi Mehrotra
 * @version 1.0
 * @see com.arsdigita.kernel
 */


public class URLServiceTest extends BaseTestCase {


    public final static String id = "$Id: URLServiceTest.java 1940 2009-05-29 07:15:05Z terry $";

    private static Logger s_log =
        Logger.getLogger( URLServiceTest.class.getName() );

    private static boolean s_dataLoaded = false;

    private static ExampleForum s_forum;
    private static SiteNode s_bboardNode;
    private static Session m_ssn;


    // NOTE: s_loadedPDLResources and load() should be moved into a seperate
    // class (e.g. KernelTestCase) if other kernel tests need to load
    // PDL files on the fly.  These were copied from PersistenceTestCase.java.

    // Prevent loading the same PDL file twice
    private static Set s_loadedPDLResources = new HashSet();
    protected static void load(String resource) {
        if (s_loadedPDLResources.contains(resource)) {
            return;
        }
        try {
            PDL m = new PDL();
            m.loadResource(resource);
            m.generateMetadata(MetadataRoot.getMetadataRoot());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
        s_loadedPDLResources.add(resource);
    }

    protected void setUp() throws Exception {
        load("com/arsdigita/kernel/examples/URLServiceExample.pdl");
        URLService.registerFinder("examples.Forum",
                                  new GenericURLFinder("index?forum_id=:id"));
        URLService.registerFinder("examples.Message",
                                  new GenericURLFinder("message?message_id=:id"));
        URLService.registerFinder("PackageInstance.BASE_DATA_OBJECT_TYPE",
                                  new GenericURLFinder(""));
        m_ssn = SessionManager.getSession();
        if (!s_dataLoaded) {
            loadData();
            s_dataLoaded=true;
        }
        super.setUp();
    }

    void loadData() throws Exception {
        PackageType bboardApp = new PackageType();
        bboardApp.setKey("__test-bboard");
        bboardApp.setPrettyName("__Test Bboard Application");
        bboardApp.setPrettyName("__Test Bboard Applications");
        bboardApp.setURI("http://www.junit.org/");
        bboardApp.save();

        PackageInstance bboardInstance =
            bboardApp.createInstance("Test Bboard Instance");

        s_bboardNode = SiteNode.createSiteNode("test-bboard");
        s_bboardNode.mountPackage(bboardInstance);
        s_bboardNode.save();

        s_forum = createForum(bboardInstance, "Test Forum");
    }

    private ExampleForum createForum(PackageInstance pkgInst, String name)
        throws Exception
    {
        ExampleForum forum = new ExampleForum();
        forum.setPackageInstance(pkgInst);
        forum.setDisplayName(name);
        forum.save();
        return forum;
    }

    private ExampleMessage createMessage(ExampleForum forum, String subject)
        throws Exception
    {
        ExampleMessage message = new ExampleMessage();
        message.setForum(forum);
        message.setSubject(subject);
        message.setBody(subject);
        message.save();
        return message;
    }

    /**
     * Constructs a UserTest with the specified name.
     *
     * @param name Test case name.
     **/
    public URLServiceTest( String name ) {
        super( name );
    }

    private String getBboardURL() {
        return s_bboardNode.getURL();
    }

    public void testGenericFinder() throws Exception {

        ExampleMessage message = createMessage(s_forum, "test subject 1");

        String url = URLService.locate(message.getOID());
        String targetURL = getBboardURL() +
            "message?message_id=" + message.getID();
        assertEquals("Incorrect URL from URLService", targetURL, url);
        s_log.info(url);

        url = URLService.locate(s_forum.getOID());
        targetURL = getBboardURL() + "index?forum_id=" + s_forum.getID();
        assertEquals("Incorrect URL from URLService", targetURL, url);
        s_log.info(url);

        url = URLService.locate(s_bboardNode.getPackageInstance().getOID());
        targetURL = getBboardURL();
        assertEquals("Incorrect URL from URLService", targetURL, url);
        s_log.info(url);
    }

}
