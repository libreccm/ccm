/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.SimpleContainer;
/**
 * This class provides a base for all ControlBar forms within the
 * system.  That is, it provides a base class so that the styling of
 * all control bars can be altered.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #9 $ $Date: 2004/08/16 $ */
public class ControlBar extends Form {

    private SimpleContainer m_controlBar;

    /**
     * Constructs a new ControlBar with the specified name.  At the
     * time of creation, instantiates a new form model for the form
     * and instantiates a ColumnPanel as the default to contain the
     * components.
     *
     * @param name the name of the form
     * @param dimensions the number of dimensional sliders you want */
    public ControlBar(String name) {
        super(name, new SimpleContainer());
        m_controlBar = new SimpleContainer("bebop:controlBar", "http://www.arsdigita.com/bebop/1.0");
        add(m_controlBar);
    }

    public ControlBar(String name, Container controlBar) {
        super(name, controlBar);
        initialize();
    }

    /**
     *  This sets up information for this particular class.
     */
    private void initialize() {
        setClassAttr("controlBar");
    }

    /**
     *  Add a dimension bar to the control bar
     */
    public void addDimensionBar(DimensionBar dimbar) {
        // Right now, this does the same as addComponent(c).
        m_controlBar.add(dimbar);
    }

    public void addComponent(Component c) {
        m_controlBar.add(c);
    }
}
