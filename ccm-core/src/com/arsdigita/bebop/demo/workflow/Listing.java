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
package com.arsdigita.bebop.demo.workflow;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import java.util.NoSuchElementException;

public class Listing extends BoxPanel
    implements ChangeListener, ActionListener {

    public static final String versionId = "$Id: Listing.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private List m_list;

    private ToggleLink m_addLink;

    public Listing () {
        super(BoxPanel.VERTICAL, false);

        m_list = new List(new ProcessesListModelBuilder());
        add(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.h4workflow_templatesh4"),  false));
        add(m_list);
        m_list.addChangeListener(this);

        m_addLink = new ToggleLink(new Label(GlobalizationUtil.globalize("bebop.demo.workflow.add_template")));
        add(m_addLink);
        Label l = new Label(GlobalizationUtil.globalize("bebop.demo.workflow.add_template"));
        l.setFontWeight(Label.BOLD);
        m_addLink.setSelectedComponent(l);
        m_addLink.addActionListener(this);

    }

    public final List getList() {
        return m_list;
    }

    public final ToggleLink getAddLink() {
        return m_addLink;
    }

    // List selection has changed
    public void stateChanged(ChangeEvent e) {
        PageState s = e.getPageState();
        if ( m_list.isSelected(s) ) {
            m_addLink.setSelected(s, false);
        }
    }

    // Toggle link has been clicked
    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();
        if ( m_addLink.isSelected(s) ) {
            m_list.clearSelection(s);
        }
    }

    private class ProcessesListModelBuilder implements ListModelBuilder {
        private boolean m_locked;

        public ListModel makeModel(List l, PageState s) {
            return new ListModel() {
                    private int i = -1;
                    private final SampleProcesses p = SampleProcesses.getInstance();
                    private final int n = p.size();

                    public boolean next() throws NoSuchElementException {
                        i += 1;
                        return ( i < n );
                    }

                    public Object getElement() {
                        return p.get(i).getName();
                    }

                    public String getKey() {
                        return p.get(i).getKey();
                    }

                };
        }

        public void lock() {
            m_locked = true;
        }

        public final boolean isLocked() {
            return m_locked;
        }

    }
}
