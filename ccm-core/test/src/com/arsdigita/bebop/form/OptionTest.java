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
package com.arsdigita.bebop.form;

import junit.framework.TestCase;

public class OptionTest extends TestCase {

    private Option _option;

    /**
       Constructs a test with the specified name.

       @param name Name of the test
    */

    public OptionTest(String id) {
        super(id);
    }

    /**
       Sets up the test fixture.
    */
    protected void setUp() {
        _option = new Option("Value","Label");
    }

    /**
       Tears down the text fixture.
       Called after every test case method.
    */

    protected void tearDown() {
        _option = null;
    }

    public void testSetLabel() {
        String label = "New Label";
        _option.setLabel(label);
        assertTrue( label == _option.getLabel() );
    }


    public void testSetValue() {
        String value = "New Value";
        _option.setValue(value);
        assertTrue( value == _option.getValue() );
    }

    // We have changed the way that this works. This test needs to be
    // modified, or moved to OptionGroupTest.java (which doesn't exist
    // yet).

    //    public void testSetSelectedYes() {
    //      _option.setAttribute("selected","yes");
    //      assert( _option.isSelected() );
    //    }

    //    public void testSetSelectedTrue() {
    //      _option.setAttribute("selected","TRUE");
    //      assert( _option.isSelected() );
    //    }

    //    public void testSetSelectedFalse() {
    //      _option.setAttribute("selected","foo");
    //      assert( !(_option.isSelected()) );
    //    }
}
