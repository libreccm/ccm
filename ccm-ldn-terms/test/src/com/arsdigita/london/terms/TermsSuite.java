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
 */

package com.arsdigita.london.terms;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * CategorizationSuite
 *
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public class TermsSuite extends PackageTestSuite {
    public static final String versionId = "$Id: TermsSuite.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/01/07 13:43:31 $";
    public TermsSuite() {
        super();
    }

    public TermsSuite(Class theClass) {
        super(theClass);
    }

    public TermsSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TermsSuite suite = new TermsSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new TermsTestSetup(suite);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );
    }
}
