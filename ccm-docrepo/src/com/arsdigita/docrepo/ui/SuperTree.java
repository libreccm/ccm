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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeCellRenderer;

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
