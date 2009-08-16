/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.indexing.kea;

import junit.framework.Test;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class KEAIndexingSuite extends PackageTestSuite {
    public KEAIndexingSuite() {
        super();
    }

    public KEAIndexingSuite(Class theClass) {
        super(theClass);
    }

    public KEAIndexingSuite(String name) {
        super(name);
    }

    public static Test suite() {
        KEAIndexingSuite suite = new KEAIndexingSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new KEAIndexingTestSetup(suite);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }
}
