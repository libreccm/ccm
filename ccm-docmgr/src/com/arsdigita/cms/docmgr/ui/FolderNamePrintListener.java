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

package com.arsdigita.cms.docmgr.ui;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.domain.DataObjectNotFoundException;

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
                DocFolder folder = new DocFolder(new BigDecimal(id));
                StringTokenizer st = new StringTokenizer(folder.getPath(), "/");

                StringBuffer sb = new StringBuffer(fixed + " Documents");
                if (st.hasMoreTokens()) {
                    st.nextToken();
                }
                while (st.hasMoreTokens()) {
                    sb.append(" > ").append
                        (URLDecoder.decode(st.nextToken()));
                }
                t.setLabel(sb.toString());
            } catch (DataObjectNotFoundException exc) {}
        } else {
            t.setLabel(fixed);
        }

    }
}
