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
import java.util.ArrayList;
import java.util.Iterator;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.docmgr.Folder;
import com.arsdigita.docmgr.Repository;
import com.arsdigita.docmgr.ResourceImpl;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Web;
/**
 * Tree model that incorporates the views of all subsribed repositories.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class RepositoriesSuperTreeModel implements TreeModel, DMConstants {

    private static BigDecimal REPOSITORIES_ROOT_ID = new BigDecimal(0);

    private class RepositoryIterator implements Iterator {

        private DataQuery m_query;

        public RepositoryIterator(DataQuery query) {
            m_query = query;
        }

        public boolean hasNext() {
            if(!m_query.isEmpty()) {

                if(m_query.next()){
                    return true;
                }
                m_query.close();
            }
            return false;
        }

        public Object next() {
            BigDecimal id = (BigDecimal) m_query.get("id");

            Folder f = null;
            try {
                f = new Folder(id);
            } catch(DataObjectNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }

            return new RepositoryTreeNode(f);
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

        Folder root = repository.getRoot();

        return new RepositoryTreeNode(root);
    }

    /**
     * Check whether a given node has children
     */

    public boolean hasChildren(TreeNode n, PageState state) {

        // The Root not has always at least 1 child (one's own repository)
        if (n instanceof RootTreeNode) {
            return true;

        }

        // otherwise docs.folders

        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery(GET_CHILDREN);

        BigDecimal folderID = DMUtils.getSelFolderOrRootID(state, n);
        query.setParameter(FOLDER_ID, folderID);
        query.addEqualsFilter(IS_FOLDER, Boolean.TRUE);

        // if this node has any children, return true
        long s = query.size();
        query.close();

        return s > 0;
    }

    /**
     * Get direct children in this node which are folders in this application
     */

    public Iterator getChildren(TreeNode n, PageState state) {

        BigDecimal folderID = DMUtils.getSelFolderOrRootID(state, n);

        Session ssn = SessionManager.getSession();
        DataQuery query = null;
        if(folderID.equals(REPOSITORIES_ROOT_ID)) {

            // Get Root folder.
            Repository currentRepository =
                (Repository) Web.getWebContext().getApplication();

            Folder root = currentRepository.getRoot();

            ArrayList list = new ArrayList();
            list.add(new RepositoryTreeNode(root));

            return list.iterator();
            //query = ssn.retrieveQuery(GET_REPOSITORIES_ROOTS);
            //query.setParameter("userID", DMUtils.getUser(state).getID());
        } else {
            query = ssn.retrieveQuery(GET_CHILDREN);
            query.setParameter(FOLDER_ID, folderID);
            query.addEqualsFilter(IS_FOLDER, "1");
        }

        return new RepositoryIterator(query);
    }

}

class RepositoryTreeNode implements TreeNode {

    String m_key;
    String m_name;

    public RepositoryTreeNode() {
        // empty default constructor needed by subclass
    }

    public RepositoryTreeNode(ResourceImpl o) {
        m_key = o.getID().toString();
        if (o.isRoot()) {
            m_name = "Documents";
        } else {
            m_name = o.getName();
        }
    }

    public Object getKey() {
        return m_key;
    }

    public Object getElement() {
        return m_name;
    }
}

class RootTreeNode extends RepositoryTreeNode {

    final static String ROOT_ID = "0";

    public RootTreeNode(ResourceImpl o) {
        super(o);
    }

    /**
     * Constructor for repositories root tree node
     */
    public RootTreeNode() {
        m_name = "My Repositories";
        m_key = ROOT_ID;
    }

}

/**
 * Non-persistent helper class that models the repositories root nodes
 *
 */

class RepositoryFolder extends Folder {

    static final BigDecimal s_rootID = new BigDecimal(0);
    User m_user;

    RepositoryFolder(User user) {
        m_user = user;
    }

    // overwrite getID to return magic #  for root folder
    public BigDecimal getID() {
        return s_rootID;
    }
}
