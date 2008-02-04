package com.arsdigita.docmgr;

/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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


/**
 * QueryTest
 *
 */

import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.docmgr.ui.DMUtils;
import com.arsdigita.docmgr.ui.DMConstants;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class QueryTest extends BaseTestCase implements DMConstants {
    private static Logger s_log = Logger.getLogger(QueryTest.class);

    public QueryTest(String name) {
        super(name);
    }


    public void testGetChildren() {
        Session session = SessionManager.getSession();
        Folder root = new Folder("root", "root");
        root.save();

        DataQuery query = session.retrieveQuery(GET_CHILDREN);
        query.setParameter(FOLDER_ID, root.getID());

        assertEquals("Empty folder shouldn't have children", 0, query.size());
        File file = new File("ChildFile", "Child File", root);
        file.save();

        assertEquals("Folder should have one child!", 1, query.size());

        query.addEqualsFilter(IS_FOLDER, Boolean.TRUE);
        assertEquals("Folder shouldn't have folder children", 0, query.size());
        Folder subfolder = new Folder("sub", "sub", root);
        subfolder.save();

        assertEquals("Folder should have 1 folder child", 1, query.size());


    }

    public void testGetFiles() {
        Repository r = Repository.create("repository", "repository", null);
        Folder root = r.getRoot();

        File file = new File("ChildFile", "Child File", root);
        file.save();

        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery("com.arsdigita.docs.getFiles");
        query.setParameter("rootPath", root.getPath());
        //        query.addOrder(LAST_MODIFIED+" desc");
        assertEquals("Should return files", 1, query.size());

    }
}
