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
import junit.framework.TestSuite;

import com.arsdigita.tools.junit.extensions.CoreTestSetup;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class KEAIndexingTestSetup extends CoreTestSetup {
    public KEAIndexingTestSetup(Test test, TestSuite suite) {
        super(test, suite);
    }

    public KEAIndexingTestSetup(TestSuite suite) {
        super(suite);
    }

    protected void setUp() throws Exception {
        addRequiredInitializer("com.arsdigita.cms.Initializer");
        addRequiredInitializer("com.arsdigita.london.util.Initializer");
        addRequiredInitializer("com.arsdigita.london.atoz.Initializer");
        addRequiredInitializer("com.arsdigita.london.terms.Initializer");
        addRequiredInitializer("com.arsdigita.cms.contenttypes.ArticleInitializer");
        super.setUp();
    }
}
