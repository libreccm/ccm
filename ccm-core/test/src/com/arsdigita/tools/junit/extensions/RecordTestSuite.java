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
package com.arsdigita.tools.junit.extensions;

import com.arsdigita.categorization.CategoryTest;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.TimedTestRecord;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * <P>Example of {@link TimedTestRecord} usage </P> 
 *
 * @author <a href="mailto:aahmed@redhat.com">Aizaz Ahmed</a>
 */
public class RecordTestSuite extends PackageTestSuite {


    public static Test suite() {
        RecordTestSuite suite = new RecordTestSuite ();

        Test CategoryTest = new CategoryTest ( "testIsEnabled" );
        Test CategoryTest2 = new CategoryTest ( "testSetGetProperties" );
        Test timedRecordedTest = new TimedTestRecord ( (TestCase) CategoryTest );
        suite.addTest ( timedRecordedTest );
        Test timedRecordedTest2 = new TimedTestRecord ( (TestCase) CategoryTest2 );
        suite.addTest ( timedRecordedTest2 );
        
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        
        return wrapper;
    }
}
