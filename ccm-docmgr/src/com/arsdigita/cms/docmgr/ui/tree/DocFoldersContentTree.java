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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.ui.DMConstants;
/**
 * Tree model that incorporates the views of all subsribed repositories.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

public class DocFoldersContentTree extends Tree implements DMConstants {

    private static final org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(DocFoldersContentTree.class);

    public DocFoldersContentTree(TreeModel m) {
        super(m);
    }
    
    //public TreeNode getRoot(PageState state) {
    //
    //    return new DocFolderTreeNode
    //        (DocFolder.getRootFolder(getFolder()));
    //}
    //
    //private DocFolder getFolder() {
    //    return (DocFolder) CMS.getContext().getContentItem();
    //}

    public void expandPath(PageState state) {
        s_log.debug("fire expansion");
        DocFolder df = (DocFolder) CMS.getContext().getContentItem();

        while (!df.isRoot()) {
            df = (DocFolder) df.getParentResource();
            String nodeKey = df.getPath();
            s_log.debug("nodeKey: "+nodeKey);
            expand(nodeKey, state);
        }
        String nodeKey = df.getPath();
        expand(nodeKey, state);
    }
}
