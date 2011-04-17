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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.docrepo.Folder;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.math.BigDecimal;
import java.util.StringTokenizer;

/**
 * Add a Print Listener to components showing the selected Folder
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class FolderNamePrintListener implements PrintListener {

    private Tree m_tree;

    public FolderNamePrintListener(Tree tree) {
        m_tree = tree;
    }

    public void prepare(PrintEvent e) {
        Label t= (Label) e.getTarget();
        String fixed = t.getLabel(e.getPageState());
        PageState state = e.getPageState();
        //t.setLabel(fixed + " " + DMUtils.getFolderName(e.getPageState(), m_tree));

        String id = (String) m_tree.getSelectedKey(state);

        if (id != null) {
            try {
                Folder folder = new Folder(new BigDecimal(id));
                StringTokenizer st = new StringTokenizer(folder.getPath(), "/");

                StringBuffer sb = new StringBuffer(fixed + " Documents");
                if (st.hasMoreTokens()) {
                    st.nextToken();
                }
                while (st.hasMoreTokens()) {
                    sb.append(" > ").append(st.nextToken());
                }
                t.setLabel(sb.toString());
            } catch (DataObjectNotFoundException exc) {}
        } else {
            t.setLabel(fixed);
        }

    }
}
