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
package com.arsdigita.domain;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * DomainTestSuite
 *
 * @author Jon Orris
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 */
public class DomainTestSuite extends PackageTestSuite {
    

    public DomainTestSuite() {
        super();
    }

    public DomainTestSuite(Class theClass) {
        super(theClass);
    }

    public DomainTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        DomainTestSuite suite = new DomainTestSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        wrapper.setSetupSQLScript("/domain/setup.sql");
        wrapper.setTeardownSQLScript("/domain/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );

    }

}
