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
package com.arsdigita.formbuilder.test;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.extensions.PermissionDecorator;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * This class is responsible for adding all Form Builder unit tests
 * to a test suite so that they can all be run. The class also makes
 * sure that the Form Builder initializer (and all initializers before
 * it in enterprise.init) is run.
 *
 * @author Peter Marklund
 * @version $Id: FormbuilderSuite.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class FormbuilderSuite extends PackageTestSuite {


    public FormbuilderSuite(String name) {
        super(name);
    }

    public void addTest(Test test) {
        PermissionDecorator perm = new PermissionDecorator(test);
        super.addTest(perm);
    }


    public static Test suite() {

        FormbuilderSuite suite = new FormbuilderSuite("Form Builder Test Suite");
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);

        return wrapper;
    }
}
