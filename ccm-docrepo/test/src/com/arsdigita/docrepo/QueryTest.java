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

/*
 * Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
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


}
