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
package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * PersistenceSuite
 *
 * @author Jon Orris
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 */
public class PersistenceSuite extends PackageTestSuite {
    public final static String versionId = "$Id: PersistenceSuite.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public PersistenceSuite() {
        super();
    }

    public PersistenceSuite(Class theClass) {
        super(theClass);
    }

    public PersistenceSuite(String name) {
        super(name);
    }

    public static Test suite() {
        PersistenceSuite suite = new PersistenceSuite();
        //suite.addTestSuite(NullTest.class);

        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        //wrapper.setPerformInitialization(false);

        wrapper.addSQLSetupScript("/com/arsdigita/persistence/setup.sql");
        wrapper.addSQLSetupScript("/persistence/setup.sql");
        wrapper.addSQLSetupScript("/com/arsdigita/persistence/static/setup.sql");
        wrapper.addSQLSetupScript("/com/arsdigita/persistence/mdsql/setup.sql");

        wrapper.addSQLTeardownScript("/com/arsdigita/persistence/mdsql/teardown.sql");
        wrapper.addSQLTeardownScript("/com/arsdigita/persistence/static/teardown.sql");
        wrapper.addSQLTeardownScript("/persistence/teardown.sql");
        wrapper.addSQLTeardownScript("/com/arsdigita/persistence/teardown.sql");

//        wrapper.setTeardownSQLScript("/persistence/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );

    }

}
