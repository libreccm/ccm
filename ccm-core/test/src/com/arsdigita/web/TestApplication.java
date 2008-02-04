/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

/*
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Oct 20, 2002
 * Time: 8:48:49 PM
 * To change this template use Options | File Templates.
 */
public class TestApplication extends Application {
   public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.web.TestApplication";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public TestApplication(DataObject dataObject) {
        super(dataObject);
    }

    public TestApplication(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public String getServletPath() {
        return URL.SERVLET_DIR + "/testapp";
    }

    public static TestApplication createTestApp(String title) {
        final TestApplication app = (TestApplication) 
            Application.createApplication
                (BASE_DATA_OBJECT_TYPE, "testapp", title, null);
       
        app.save();
       
        return app;
    }
}
