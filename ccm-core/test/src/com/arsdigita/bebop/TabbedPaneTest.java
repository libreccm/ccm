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
 * Regression tests for the TabbedPanel component.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class TabbedPaneTest extends XMLComponentRegressionBase {

    public static final String versionId = "$Id: TabbedPaneTest.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public TabbedPaneTest(String id) {
        super(id);
    }

    /**
     *  Test a TabbedPane with a Label.
     */
    public void testTabbedPane() {
        Label mylabel = new Label("This is a Label");
        TabbedPane tp = new TabbedPane();
        tp.addTab("Tab A", mylabel);
        testComponent(tp,"one-label");
    }

    /**
     *
     * Test a TabbedPane. Make sure that the contents of the first tab
     * are fully visible on the first request.
     *
     */
    public void testTabbedPaneVisible() {
        Label labelA = new Label("first tab");
        Label labelB = new Label("second tab");
        TabbedPane tp = new TabbedPane();

        tp.add(labelA);
        tp.addTab("tab2",labelB);
        testComponent(tp,"first-visible");
    }


    /**
     * Test an empty TabbedPane.  */
    //      public void testEmptyTabbedPane() {
    //          testComponent(new TabbedPane(), "empty");
    //      }

}
