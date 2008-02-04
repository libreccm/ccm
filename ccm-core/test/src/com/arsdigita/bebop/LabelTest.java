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
 * Regression tests for the Label component.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class LabelTest extends XMLComponentRegressionBase {

    public static final String versionId = "$Id: LabelTest.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public LabelTest (String id) {
        super(id);
    }

    /**
     *  Test a Label with text "TestLabel"
     */
    public void testLabelWithText() {
        testComponent(new Label("TestLabel"),"testlabel");
    }

    /**
     *  Test a Label with no text
     */
    public void testEmptyLabel() {
        testComponent(new Label(),"empty");
    }

    /**
     *  Test a Label with text "TestLabel" and escaping
     */
    public void testLabelWithEscape() {
        testComponent(new Label("TestLabel",true),"escape");
    }

    /**
     *  Test a Label with text "TestLabel" and escaping, using method
     */
    public void testLabelWithEscapeMethod() {
        Label testLabel = new Label("TestLabel");
        testLabel.setOutputEscaping(true);
        testComponent(testLabel,"escape");
    }

    /**
     *  Test a Label's setFontWeight and setLabel method.
     */
    public void testLabelFWsetLabel() {
        Label testLabel = new Label("TestLabel");
        testLabel.setFontWeight("50");
        testLabel.setLabel("The New Label");
        testComponent(testLabel,"fw-setLabel");
    }

    /**
     *  Test a Label's setFontWeight. Compare the setLabel with
     *  setting the Label in the constructor.
     */
    public void testLabelFW() {
        Label testLabel = new Label("The New Label");
        testLabel.setFontWeight("50");
        testComponent(testLabel,"fw-setLabel");
    }

}
