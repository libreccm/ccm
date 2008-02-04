/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr.ui;

import java.math.BigDecimal;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;

/**
 * This class is the component in the Doc Manager UI that shows all
 * mounted repositories and a form where to sign up or sign off
 * repositories of group members.
 * The current policy is to allow access to all the repositories
 * of the members of a group.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class RepositoryPane extends BoxPanel
    implements DMConstants, ChangeListener {

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
            key = DMUtils.getRootFolder(state).getID().toString();
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
