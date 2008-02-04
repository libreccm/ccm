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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.CompoundComponent;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DimensionBar
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public class DimensionBar extends CompoundComponent {

    public final static String versionId = "$Id: DimensionBar.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Component m_title;
    private SimpleContainer m_links = new SimpleContainer();
    private StringParameter m_state = new StringParameter("dim");
    private Map m_active = new HashMap();
    private Map m_selectable = new HashMap();

    public DimensionBar(Component title) {
        super(new SimpleContainer("bebop:dimensionBar", "http://www.arsdigita.com/bebop/1.0"));
        //        ((SimpleContainer) getContainer()).setAttribute("selectName", m_state.getName());
        m_title = title;

        m_title.setClassAttr("dimensionBarTitle");
        m_links.setClassAttr("dimensionBarOptions");
        add(m_title, BlockStylable.CENTER);
        add(m_links, BlockStylable.CENTER);
    }

    /**
     * Add a dimension to the slider bar.
     */
    public void add(String key, Component c) {
        SimpleContainer active = new SimpleContainer();
        c.setClassAttr("dimensionBarOption");
        active.add(c);
        add(key, c, active);
    }

    /**
     * Add a dimension to the slider bar.
     */
    public void add(final String key, Component selectable,
                    Component active) {
        SimpleContainer cont = new SimpleContainer();
        selectable.setClassAttr("dimensionBarOption");

        ActionLink link = new ActionLink(selectable);
        link.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    dimClicked(evt.getPageState(), key);
                }
            });

        cont.add(link);
        cont.add(active);

        m_selectable.put(key, link);
        m_active.put(key, active);

        if (m_links.isEmpty()) {
            setDefaultKey(key);
        }

        m_links.add(cont);
    }

    private void dimClicked(PageState ps, String key) {
        ps.setValue(m_state, key);

        for (Iterator it = m_selectable.values().iterator(); it.hasNext(); ) {
            Component c = (Component) it.next();
            c.setVisible(ps, true);
        }

        for (Iterator it = m_active.values().iterator(); it.hasNext(); ) {
            Component c = (Component) it.next();
            c.setVisible(ps, false);
        }

        Component c = (Component) m_selectable.get(key);
        c.setVisible(ps, false);
        c = (Component) m_active.get(key);
        c.setVisible(ps, true);
    }

    public void setDefaultKey(String key) {
        m_state.setDefaultValue(key);
    }

    public String getDefaultKey() {
        return (String) m_state.getDefaultValue();
    }

    public String getSelectedKey(PageState ps) {
        return (String) ps.getValue(m_state);
    }

    public void setSelectedKey(PageState ps, String key) {
        ps.setValue(m_state, key);
        dimClicked(ps, key);
    }

    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_state);

        for (Iterator it = m_active.values().iterator(); it.hasNext(); ) {
            Component c = (Component) it.next();
            p.setVisibleDefault(c, false);
        }

        String key = getDefaultKey();
        Component c = (Component) m_active.get(key);
        p.setVisibleDefault(c, true);
        c = (Component) m_selectable.get(key);
        p.setVisibleDefault(c, false);
    }

}
