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
package com.arsdigita.navigation.ui.admin;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryTreeModelLite;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Web;

/**
 * Lists category tree.
 *
 * @author Tri Tran (tri@arsdigita.com)
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: CategoryTree.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class CategoryTree extends Tree {

    private final SectionTreeModelBuilder modelBuilder;

    /**
     * Construct a new CategoryTree
     * 
     * @param model The {@link CategorySelectionModel} used for storing the selected category.
     */
    public CategoryTree(final CategorySelectionModel model) {
        super(new SectionTreeModelBuilder());
        modelBuilder = (SectionTreeModelBuilder) getModelBuilder();
        setSelectionModel(model);
    }

    protected void setParent(final ApplicationInstanceAwareContainer parent) {
        modelBuilder.setParent(parent);
    }

    /**
     * A TreeModelBuilder that loads the tree from the current category
     */
    private static class SectionTreeModelBuilder extends LockableImpl implements TreeModelBuilder {

        private ApplicationInstanceAwareContainer parent;

        public SectionTreeModelBuilder() {
            super();
        }

        public void setParent(final ApplicationInstanceAwareContainer parent) {
            this.parent = parent;
        }

        @Override
        public TreeModel makeModel(final Tree tree, final PageState state) {
            final Navigation app;
            if (parent == null) {
                app = (Navigation) Web.getContext().getApplication();
            } else {
                app = (Navigation) parent.getAppInstance();
            }

            final TemplateContext ctx = Navigation.getContext().getTemplateContext();
            final String dispatcherContext = ctx == null ? null : ctx.getContext();
            final Category root = Category.getRootForObject(app, dispatcherContext);
            return new CategoryTreeModelLite(root);
        }

    }
}
