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

package com.arsdigita.london.cms.dublin;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;


/**
 * DublinTestSetup
 *
 * Utility extension of BaseTestSetup that automatically adds the core initializer
 *
 */
public class DublinTestSetup extends CoreTestSetup {
    public DublinTestSetup(Test test, TestSuite suite) {
        super(test, suite);
    }

    public DublinTestSetup(TestSuite suite) {
        super(suite);
    }

    protected void setUp() throws Exception {
        addRequiredInitializer("com.arsdigita.cms.Initializer");
        addRequiredInitializer("com.arsdigita.london.util.Initializer");
        addRequiredInitializer("com.arsdigita.london.atoz.Initializer");
        addRequiredInitializer("com.arsdigita.london.terms.Initializer");
        addRequiredInitializer("com.arsdigita.london.navigation.Initializer");
        addRequiredInitializer("com.arsdigita.london.cms.dublin.Initializer");
        super.setUp();
    }
}
