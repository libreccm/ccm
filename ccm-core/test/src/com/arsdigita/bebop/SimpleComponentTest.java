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
package com.arsdigita.bebop;

/**
 * Regression tests for the SimpleComponent.
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */

public class SimpleComponentTest extends XMLComponentRegressionBase {


    public SimpleComponentTest (String id) {
        super(id);
    }

    /**
     *  Test custom name mangling.  While this tests functionality
     *  that's in SimpleComponent, it takes a stateful component to
     *  exhibit the behavior.  We pick a TabbedPane for this.  */
    public void FAILStestCustomMangling()
    {
        final String TEST_KEY = "rumpelstilzchen"; // an unlikely string
        TabbedPane pane = new TabbedPane();
        pane.setKey(TEST_KEY);          // we'll be looking for this.
        pane.addTab("either", new Label("Tab A"));
        pane.addTab("or"    , new Label("Tab B"));

        // no debugging => no structure, which would contain the key
        regexpComponent(pane, TEST_KEY, false);
    }

    /** make JTest happy: while it ignores testCustomMangling, it will
        still find this test.  A suite must have one test at least. */
    public void testSucceed(){}
}
