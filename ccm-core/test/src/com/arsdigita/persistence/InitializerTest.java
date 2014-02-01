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

import com.arsdigita.persistence.metadata.MetadataRoot;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p> This class ensures that we can load the persistence metadata
 * XML file at initialization
 *
 * </p>
 *
 *
 * @author Michael Bryzek
 * @date $Date: 2004/08/16 $
 * @version $Revision: #9 $
 *
 * @see com.arsdigita.persistence.Initializer
 **/

public class InitializerTest extends TestCase
{


    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     **/
    public InitializerTest(String name) {
        super(name);
    }

    /**
     * Test method: void addFile(String)
     **/
    public void testStartupXML() {
        MetadataRoot root = SessionManager.getMetadataRoot();
        if (root == null) {
            fail("Metadata root not loaded");
        }
        // Make sure we have at least one schema or model
        if (!root.getModels().hasNext()) {
            fail("Metadata root has no schema or model. Check that you have " +
                 "correctly specified the file paths in the xmlFiles parameter " +
                 "in the init script you are using.");
        }
    }


    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     **/
    public static void main(String[] args) {
        junit.textui.TestRunner.run( new TestSuite(InitializerTest.class) );
    }
}
