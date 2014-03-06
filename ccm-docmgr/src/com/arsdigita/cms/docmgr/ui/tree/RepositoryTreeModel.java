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
import java.util.ArrayList;
import java.util.Iterator;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Repository;
import com.arsdigita.cms.docmgr.Resource;
import com.arsdigita.cms.docmgr.ui.DMConstants;
import com.arsdigita.cms.docmgr.ui.DMUtils;
import com.arsdigita.web.Web;
/**
 * Tree model that incorporates the views of all subsribed repositories.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

public class RepositoryTreeModel implements TreeModel, DMConstants {

    private static BigDecimal REPOSITORIES_ROOT_ID = new BigDecimal(0);

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(RepositoryTreeModel.class);

    private class RepositoryIterator implements Iterator {

        private Folder.ItemCollection m_collection;

        public RepositoryIterator(Folder.ItemCollection collection ) {
            m_collection  = collection ;
        }

        public boolean hasNext() {
            if(!m_collection.isEmpty()) {

               if(m_collection.next()){
                    return true;
                }
                m_collection.close();
            }
            return false;
        }

        public Object next() {
           //BigDecimal id = m_collection.getID();
           //
           //Folder f = null;
           //try {
           //    f = new Folder(id);
           //} catch(DataObjectNotFoundException e) {
           //    throw new RuntimeException(e.getMessage());
           //}

            DocFolder df = (DocFolder) m_collection.getContentItem();
            return new RepTreeNode(df);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }



    /**
     * Obtain the root folder of the tree
     */

    public TreeNode getRoot(PageState state) {

        /* sdm #204157: Ron requests that we only show the one repository
           of the Application, not a tree of repositories.

           return new RootTreeNode();
        */

        // Return the Root of this Application's repository
        Repository repository = (Repository) Web.getWebContext().getApplication();

        DocFolder root = repository.getRoot();
        if (s_log.isDebugEnabled()) {
        s_log.debug("root folder is "+root.getTitle());
        s_log.debug("root folder id is "+root.getID().toString());
        }

        return new RepTreeNode(root);
    }

    /**
     * Check whether a given node has children
     */

    public boolean hasChildren(TreeNode n, PageState state) {

        // The Root not has always at least 1 child (one's own repository)
        if (n instanceof RootTreeNode) {
            return true;

        }
        if (s_log.isDebugEnabled()) {
        s_log.debug("node key is "+n.getKey());
        s_log.debug("node value is "+n.getElement());
        }
        // otherwise docs.folders

        //Session session = SessionManager.getSession();
        //DataQuery query = session.retrieveQuery(GET_CHILDREN);

        BigDecimal folderID = DMUtils.getSelFolderOrRootID(state, n);
        DocFolder df = new DocFolder(folderID);

        return !df.isEmpty();
        //query.setParameter(FOLDER_ID, folderID);
        //query.addEqualsFilter(IS_FOLDER, Boolean.TRUE);
        //
        //// if this node has any children, return true
        //long s = query.size();
        //query.close();

        //return s > 0;
    }

    /**
     * Get direct children in this node which are folders in this application
     */

    public Iterator getChildren(TreeNode n, PageState state) {

        BigDecimal folderID = DMUtils.getSelFolderOrRootID(state, n);

        //Session ssn = SessionManager.getSession();
        if(folderID.equals(REPOSITORIES_ROOT_ID)) {

            // Get Root folder.
            Repository currentRepository =
                (Repository) Web.getWebContext().getApplication();

            DocFolder root = currentRepository.getRoot();

            ArrayList list = new ArrayList();
            list.add(new RepTreeNode(root));

            return list.iterator();
            //query = ssn.retrieveQuery(GET_REPOSITORIES_ROOTS);
            //query.setParameter("userID", DMUtils.getUser(state).getID());
        } 
        DocFolder df = new DocFolder(folderID);
        Folder.ItemCollection collection = df.getItems();
        collection.addFolderFilter(true);
        collection.clearOrder();
        collection.addOrder("item.name");

        //query = ssn.retrieveQuery(GET_CHILDREN);
        //query.setParameter(FOLDER_ID, folderID);
        //query.addEqualsFilter(IS_FOLDER, "1");

        return new RepositoryIterator(collection);
    }

}

class RepTreeNode implements TreeNode {

    private static final org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(RepTreeNode.class);

    String m_key;
    String m_name;
    DocFolder m_folder;

    public RepTreeNode() {
        // empty default constructor needed by subclass
    }

    public RepTreeNode(DocFolder df) {
        m_key = df.getID().toString();
        s_log.debug("m_key is" + m_key);
        if (df.isRoot()) {
            m_name = "Documents";
        } else {
            m_name = df.getTitle();
        }
        m_folder = df;
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

class RootTreeNode extends RepTreeNode {

    final static String ROOT_ID = "0";

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(RepositoryTreeModel.class);

    public RootTreeNode(Resource o) {
        super((DocFolder) o);
    }

    /**
     * Constructor for repositories root tree node
     */
    public RootTreeNode() {
        s_log.debug("ROOT TREE NODE ID HERE");
        m_name = "My Repositories";
        m_key = ROOT_ID;
    }

}
