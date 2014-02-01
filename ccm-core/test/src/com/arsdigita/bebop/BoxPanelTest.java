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
 * Regression tests for the BoxPanel component.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class BoxPanelTest extends XMLComponentRegressionBase {


    public final static int HORIZONTAL = 1;
    public final static int VERTICAL = 2;

    public BoxPanelTest (String id) {
        super(id);
    }

    /**
     *  Test an empty BoxPanel
     */
    public void testEmptyBoxPanel() {
        testComponent(new BoxPanel(),"empty");
    }

    /**
     *  Test a horizontal BoxPanel
     */
    public void testHorizontalBoxPanel() {
        testComponent(new BoxPanel(HORIZONTAL),"horiz");
    }


    /**
     *  Test a vertical BoxPanel
     */
    public void testVerticalBoxPanel() {
        testComponent(new BoxPanel(VERTICAL),"vertical");
    }


    /**
     *  Test a horizontal BoxPanel with centering
     */
    public void testCenteredHorizontalBoxPanel() {
        testComponent(new BoxPanel(HORIZONTAL, true),"horiz-centered");
    }


    /**
     *  Test a vertical BoxPanel with centering
     */
    public void testCenteredVerticalBoxPanel() {
        testComponent(new BoxPanel(VERTICAL, true),"vertical-centered");
    }


}
