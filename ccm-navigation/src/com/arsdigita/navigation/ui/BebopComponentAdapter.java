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
 */


package com.arsdigita.navigation.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;


public class BebopComponentAdapter extends SimpleComponent {
    
    private Component m_component = null;

    public BebopComponentAdapter() {
    }

    public BebopComponentAdapter(Component component) {
        m_component = component;
    }

    public void setComponent(Component component) {
        Assert.isUnlocked(this);
        m_component = component;
    }

    public void lock() {
        super.lock();
        m_component.lock();
    }

    public void generateXML(PageState state,
                            Element parent) {
        Assert.isLocked(this);

        Element content = m_component.generateXML(state.getRequest(),
                                                  state.getResponse());
        
        if (content != null) {
            parent.addContent(content);
        }
    }
}
