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

import java.math.BigDecimal;
import java.util.Iterator;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.ui.DMConstants;
/**
 * Tree model that incorporates the views of all subsribed repositories.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

public class DocFoldersContentTreeModel implements TreeModel, DMConstants {

    private static BigDecimal REPOSITORIES_ROOT_ID = new BigDecimal(0);

    private static final org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(DocFoldersContentTreeModel.class);
    /**
     * Obtain the root folder of the tree
     */

    public TreeNode getRoot(PageState state) {

        s_log.debug("getRoot.  current item is: "+
                    CMS.getContext().getContentItem().getID().toString()
                    );
        return new DocFolderTreeNode
            (DocFolder.getRootFolder(getFolder()));
    }

    /**
     * Check whether a given node has children
     */

    public boolean hasChildren(TreeNode n, PageState state) {

        DocFolder df = ((DocFolderTreeNode) n).getFolder();
        Folder.ItemCollection collection = df.getItems();
        collection.addFolderFilter(true);

        boolean c = collection.next();
        collection.close();
        return c;
    }

    /**
     * Get direct children in this node which are folders in this application
     */

    public Iterator getChildren(TreeNode n, PageState state) {

        DocFolder df = ((DocFolderTreeNode) n).getFolder();
        Folder.ItemCollection collection = df.getItems();
        collection.addFolderFilter(true);

        return new DocFolderIterator(collection);
    }

    private DocFolder getFolder() {
        return (DocFolder) CMS.getContext().getContentItem();
    }

}
