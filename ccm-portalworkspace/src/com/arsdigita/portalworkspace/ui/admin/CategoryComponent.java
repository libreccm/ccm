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
 */
package com.arsdigita.portalworkspace.ui.admin;

import com.arsdigita.portalworkspace.ui.sitemap.ApplicationSelectionModel;
import org.apache.log4j.Logger;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryTreeModelLite;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import java.util.Iterator;

/**
 * CategoryComponent.
 *
 * @version $Id: CategoryComponent.java 1174 2006-06-14 14:14:15Z fabrice $
 */
public class CategoryComponent extends SimpleContainer {

    private static final Logger s_log = Logger.getLogger(CategoryComponent.class);
    private ACSObjectSelectionModel m_catModel;
    private CategoryTree m_tree;
    private ApplicationSelectionModel m_appModel;
    private Label m_error;
    private static final Application m_app = Web.getWebContext().getApplication();
    private static final Category m_root = Category.getRootForObject(m_app);

    /**
     *
     * @param appModel
     */
    public CategoryComponent(ApplicationSelectionModel appModel) {
        setNamespace(WorkspacePage.PORTAL_XML_NS);
        setTag("portal:categoryPanel");

        m_appModel = appModel;

        m_catModel = new ACSObjectSelectionModel("category");
        m_tree = new CategoryTree(m_catModel);

        add(m_tree);
        add(new CategoryTable(appModel, m_catModel));

        m_error = new Label("");
        add(m_error);
    }

    /**
     *
     */
    private class CategoryTree extends Tree {

        public CategoryTree(ACSObjectSelectionModel categoryModel) {
            super(new SectionTreeModelBuilder());
            setSelectionModel(categoryModel);
            addActionListener(new CategoryTreeActionListener(categoryModel));
        }

    }

    /**
     *
     */
    private class CategoryTreeActionListener implements ActionListener {

        private ACSObjectSelectionModel m_catModel;

        public CategoryTreeActionListener(ACSObjectSelectionModel catModel) {
            m_catModel = catModel;
        }

        public void actionPerformed(ActionEvent event) {
            PageState state = event.getPageState();
            // categorize the Application
            s_log.debug("action performed");
            if (m_catModel.isSelected(state)) {
                Category category = (Category) m_catModel
                    .getSelectedObject(state);

                // Make sure that other workspaces aren't already categorized
                DataCollection workspaces = SessionManager.getSession()
                    .retrieve(Workspace.BASE_DATA_OBJECT_TYPE);

                Filter f = workspaces.addInSubqueryFilter("id",
                                                          "com.arsdigita.categorization.immediateChildObjectIDs");
                f.set("categoryID", category.getID().toString());

                if (workspaces.isEmpty()) {
                    s_log.debug("About to categorize");

                    Workspace workspace = (Workspace) m_appModel
                        .getSelectedObject(state);
                    category.addChild(workspace);
                    category.save();
                } else {
                    // print an error
                    while (workspaces.next()) {
                        Workspace wk = (Workspace) DomainObjectFactory
                            .newInstance(workspaces.getDataObject());
                        m_error.setLabel(
                            "This category already has a workspace "
                                + wk.getTitle(), state);
                    }
                }
            }
        }

    }

    /**
     * A TreeModelBuilder that loads the tree from the current category
     */
    private static class SectionTreeModelBuilder extends LockableImpl implements TreeModelBuilder {

        /**
         * A RequestLocal wrapper for the model. It is necessary to wrap the model into a 
         * {@link RequestLocal} wrapper because the contents of the model may change. Before this
         * was introduced the system had be restarted if a new category system was mapped to 
         * a PortalWorkspace.
         */
        final RequestLocal treeModel = new TreeModelRequestLocal();

        public SectionTreeModelBuilder() {
            super();
        }

        @Override
        public TreeModel makeModel(Tree t, PageState s) {
            return (TreeModel) treeModel.get(s);

        }

    }

    /**
     * The RequestLocal used by the {@link SectionTreeModelBuilder}. 
     */
    private static class TreeModelRequestLocal extends RequestLocal {

        public TreeModelRequestLocal() {
            //Nothing
        }
        
        /**
         * Provides the initial value for this {@code {RequestLocal}. If a category system
         * is mapped to the current PortalWorkspace (the current Application instance), the method 
         * returns a {@link CategoryTreeModelLite} instance. The {link CategoryTreeModelLite} is 
         * created using the root category of the mapped category system.
         * If there is no mapping the method returns an instance of {@link EmptyTreeModel}.
         * 
         * @param state
         * @return 
         */
        @Override
        protected Object initialValue(final PageState state) {
            Application app = Web.getWebContext().getApplication();
            Category root = null;
            while (app != null && root == null) {
                root = Category.getRootForObject(app);
                app = (Application) app.getParentResource();
            }

            if (root == null) {
                return new EmptyTreeModel();

            } else {
                Assert.exists(root, Category.class);
                return new CategoryTreeModelLite(root);
            }

        }

    }

    /**
     * A simple helper class representing an empty tree.
     */
    private static class EmptyTreeModel implements TreeModel {

        public EmptyTreeModel() {
            //Nothing
        }
        
        @Override
        public TreeNode getRoot(final PageState data) {
            return new EmptyTreeRootNode();
        }

        @Override
        public boolean hasChildren(final TreeNode node,
                                   final PageState state) {
            return false;
        }

        @Override
        public Iterator getChildren(final TreeNode node,
                                    final PageState data) {
            return null;
        }

    }

    /**
     * The root node of an empty tree.
     */
    private static class EmptyTreeRootNode implements TreeNode {

        public EmptyTreeRootNode() {
            //Nothing
        }
        
        @Override
        public Object getKey() {
            return "empty";
        }

        @Override
        public Object getElement() {
            return "";
        }

    }

}
