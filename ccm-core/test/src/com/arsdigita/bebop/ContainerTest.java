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

import junit.framework.TestCase;

/**
 * Regression tests for the Container interface.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class ContainerTest extends TestCase {


    ContainerImpl m_containerImpl;

    public ContainerTest (String id) {
        super(id);
        m_containerImpl = new ContainerImpl();
    }

    /**
     *
     * Tests to make sure the add(Component) method exists.
     */
    public void testAddWithComponent() {
        Label l = new Label("Hello");
        m_containerImpl.add(l);
    }

    /**
     *
     * Tests to make sure the add(Component, int) method exists.
     */
    public void testAddWithComponentConstraints() {
        Label l = new Label("Hello");
        m_containerImpl.add(l,1);
    }

    public void testContains() {
        Object o = new Object();
        boolean b = m_containerImpl.contains(o);
        assertTrue(b);
    }

    public void testGet() {
        Component c = m_containerImpl.get(0);
    }

    public void testIndexOf() {
        Label l = new Label("Hello");
        int i = m_containerImpl.indexOf(l);
    }

    public void testIsEmpty() {
        boolean b = m_containerImpl.isEmpty();
        assertTrue(b);
    }

    public void testSize() {
        int i = m_containerImpl.size();
    }
}
