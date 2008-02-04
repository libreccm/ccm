/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this fileare subject to the CCM Public
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

package com.arsdigita.cms.docmgr.ui.tree;

import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Repository;

public class DocFolderTreeNode implements TreeNode {

    private static final org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(DocFolderTreeNode.class);

    String m_key;
    String m_name;
    DocFolder m_folder;

    public DocFolderTreeNode() {
        // empty default constructor needed by subclass
    }

    public DocFolderTreeNode(DocFolder df) {
        m_key = df.getPath();
        s_log.debug("m_key is" + m_key);
        s_log.debug("folder id is "+df.getID().toString());
        m_folder = df;

        if (df.isRoot()) {
            df = (DocFolder) df.getWorkingVersion();
            Repository rep = DocFolder.getRepository(df);
            if (rep == null) {
                s_log.error("repository is null");
            } else {
                m_name = rep.getDisplayName();
            }
        } else {
            m_name = df.getTitle();
        }
    }

    public Object getKey() {
        return m_key;
    }

    public Object getElement() {
        return m_name;
    }

    public DocFolder getFolder() {
        return m_folder;
    }
}

