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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeCellRenderer;
import com.arsdigita.bebop.tree.TreeModel;

/**
 * Customized Bebop Tree that shows the accessible Repositories
 * at the root level and the inidividual File trees beneath it.
 * The root level is constructed expanded.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class SuperTree extends Tree {

    SuperTree(TreeModel tm) {
        super(tm);
        setCellRenderer(new RepositoryTreeCellRenderer());
    }
}


class RepositoryTreeCellRenderer implements TreeCellRenderer {

    public Component getComponent(Tree tree,
                                  PageState state,
                                  Object value,
                                  boolean isSelected,
                                  boolean isExpanded,
                                  boolean isLeaf,
                                  Object key) {

        String m_key = (String) key;

        if("0".equals(m_key)) {
            return new Label(value.toString());
        } else {
            return new ControlLink(value.toString());
        }

    }


}
