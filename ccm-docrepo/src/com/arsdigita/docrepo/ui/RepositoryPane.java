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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import java.math.BigDecimal;

/**
 * This class is the component in the Doc Repository UI that shows all
 * mounted repositories and a form where to sign up or sign off
 * repositories of group members.
 * The current policy is to allow access to all the repositories
 * of the members of a group.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class RepositoryPane extends BoxPanel
    implements DRConstants, ChangeListener {

    private Tree m_tree;

    private Component m_Repositories;


    /**
     * Default constructor
     */

    public RepositoryPane() {

        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Repositories");

        BoxPanel leftSide = new BoxPanel();
        leftSide.setClassAttr("navbar");
        m_tree = new SuperTree(new RepositoriesSuperTreeModel());
        m_tree.addChangeListener(this);
        leftSide.add(m_tree);
        add(leftSide);

        SegmentedPanel rightSide = new SegmentedPanel();
        rightSide.setClassAttr("main");
        m_Repositories = makeRepositoriesPanel(rightSide);
        add(rightSide);
    }

    @Override
    public void register(Page p) {
        p.addGlobalStateParam(ROOTFOLDER_ID_PARAM);
        p.addGlobalStateParam(SEL_FOLDER_ID_PARAM);
        super.register(p);
    }

    /**
     * Implementation of the change listener.
     * Updates the selected folder globally.
     */
    public void stateChanged(ChangeEvent e) {

        PageState state = e.getPageState();
        String key = (String) m_tree.getSelectedKey(state);

        // first time, set to tree root and expand tree level
        if(key==null) {
            key = DRUtils.getRootFolder(state).getID().toString();
            m_tree.expand(key, state);
        }
        state.setValue(SEL_FOLDER_ID_PARAM, new BigDecimal(key));
    }

    /**
     * Make the Repositories Table before the form.
     */
    private Component  makeRepositoriesPanel(SegmentedPanel main) {

        RepositoriesTable table = new RepositoriesTable();

        return main.addSegment(REPOSITORIES_INFORMATION_HEADER,
                               new RepositoriesSelectionForm(table));
    }
}
