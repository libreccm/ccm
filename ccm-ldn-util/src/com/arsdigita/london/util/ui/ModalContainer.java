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

package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;

import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.util.Assert;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class ModalContainer extends SimpleContainer implements Resettable {

    private Map m_modes;
    private String m_defaultMode;
    
    public ModalContainer(String name,
                          String xmlns) {
        super(name, xmlns);
        
        m_modes = new HashMap();
    }

    protected void registerMode(String name,
                                Component[] show) {
        Assert.exists(name, String.class);
        Assert.exists(show, Component[].class);

        m_modes.put(name, show);
    }

    public void setDefaultMode(String name) {
        Assert.exists(name, String.class);
        Assert.truth(m_modes.containsKey(name), 
                     "Mode " + name + " has been registered");

        m_defaultMode = name;
    }
    
    public void reset(PageState state) {
        setMode(state, m_defaultMode);
    }

    public void setMode(PageState state,
                        String name) {
        Assert.exists(name, String.class);

        Component[] show = (Component[])m_modes.get(name);
        Assert.exists(show, Component[].class);
        
        Iterator cs = children();
        while (cs.hasNext()) {
            Component c = (Component)cs.next();
            c.setVisible(state, false);
        }
        
        for (int i = 0 ; i < show.length ; i++) {
            show[i].setVisible(state, true);
        }
    }
    
    public void register(Page p) {
        super.register(p);

        Iterator cs = children();
        while (cs.hasNext()) {
            Component c = (Component)cs.next();
            p.setVisibleDefault(c, false);
        }
        
        if (m_defaultMode != null) {
            Component[] show = (Component[])m_modes.get(m_defaultMode);
            Assert.exists(show, Component[].class);
            
            for (int i = 0 ; i < show.length ; i++) {
                p.setVisibleDefault(show[i], true);
            }
        }
    }
    
    protected class ModeChangeListener implements ActionListener, 
                                                  ChangeListener, 
                                                  DomainObjectActionListener {
        private String m_mode;
        private ModalContainer m_component;
        
        public ModeChangeListener(String mode) {
            this(ModalContainer.this, mode);
        }

        public ModeChangeListener(ModalContainer component,
                                  String mode) {
            m_mode = mode;
            m_component = component;
        }

        public void actionPerformed(ActionEvent e) {
            m_component.setMode(e.getPageState(), m_mode);
        }

        public void actionPerformed(DomainObjectActionEvent e) {
            m_component.setMode(e.getPageState(), m_mode);
        }

        public void stateChanged(ChangeEvent e) {
            m_component.setMode(e.getPageState(), m_mode);
        }
    }


    protected class ModeResetListener implements ActionListener, 
                                                 ChangeListener, 
                                                 DomainObjectActionListener {
        private Resettable m_component;
        
        public ModeResetListener() {
            this(ModalContainer.this);
        }

        public ModeResetListener(Resettable component) {
            m_component = component;
        }

        public void actionPerformed(ActionEvent e) {
            m_component.reset(e.getPageState());
        }

        public void actionPerformed(DomainObjectActionEvent e) {
            m_component.reset(e.getPageState());
        }

        public void stateChanged(ChangeEvent e) {
            m_component.reset(e.getPageState());
        }
    }
}
