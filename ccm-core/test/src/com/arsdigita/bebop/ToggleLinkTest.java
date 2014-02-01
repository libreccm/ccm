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

import org.apache.log4j.Logger;

/**
 * Regression tests for the ToggleLink component.
 *
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 */

public class ToggleLinkTest extends XMLComponentRegressionBase {


    private static Logger s_log =
        Logger.getLogger(ToggleLinkTest.class);

    public ToggleLinkTest (String id) throws Exception {
        super(id);
    }

    /**
     *  Test a ToggleLink
     */
    public void testStringToggleLink() {
        testComponent(new ToggleLink("toggley toggle"),"string");
    }

    /**
     *  Test a Componentized ToggleLink
     */
    public void testComponentizedToggleLink() {
        Label toggleLabel = new Label("Toggles");
        ToggleLink toggleMe = new ToggleLink(toggleLabel);
        testComponent(toggleMe,"component");
    }

    /**
     * Test getSelectedComponent()
     */
    public void testGetSelectedComponent() {
        PageState m_pageState = getPageState();

        Label toggleLabel = new Label("Toggles");
        ToggleLink toggleMe = new ToggleLink(toggleLabel);
        Label returnedLabel = (Label) toggleMe.getSelectedComponent();

        String expected = "Toggles";
        String actual = returnedLabel.getLabel(m_pageState);

        assertEquals(expected,actual);
    }

    /**
     * Test setSelectedComponent()
     */
    public void testSetSelectedComponent() {

        PageState m_pageState = getPageState();
        Label toggleLabel = new Label("Toggles");
        Label fakeLabel = new Label("no toggles");
        ToggleLink tl = new ToggleLink(fakeLabel);
        tl.setSelectedComponent(toggleLabel);

        // We don't use testComponent here because the
        // setSelectedComponent method doesn't make the component
        // selected, while the constructor does make the component
        // selected.
        Label returnedLabel = (Label) tl.getSelectedComponent();
        String expected = "Toggles";
        String actual = returnedLabel.getLabel(m_pageState);
        assertEquals(expected,actual);

    }

    private PageState getPageState () {
        Page p = new Page();
        p.lock();
        ComponentTestHarness harness = new ComponentTestHarness(p);
        return harness.getPageState();
    }

}
