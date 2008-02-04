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

package com.arsdigita.london.navigation.ui.admin;

import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryTreeModelLite;
import com.arsdigita.web.Web;
import com.arsdigita.util.LockableImpl;

import com.arsdigita.london.navigation.Navigation;


/**
 * Lists category tree.
 *
 * @author Tri Tran (tri@arsdigita.com)
 * @version $Id: CategoryTree.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class CategoryTree extends Tree {

    public static final String versionId = "$Id: CategoryTree.java 755 2005-09-02 13:42:47Z sskracic $ by $Author: sskracic $, $DateTime: 2004/01/21 09:04:39 $";

    /**
     * Construct a new CategoryTree
     */
    public CategoryTree(CategorySelectionModel model) {
        super(new SectionTreeModelBuilder());
        setSelectionModel(model);
    }

    /**
     * A TreeModelBuilder that loads the tree from the current category
     */
    private static class SectionTreeModelBuilder extends LockableImpl
        implements TreeModelBuilder {

        public SectionTreeModelBuilder() {
            super();
        }

        public TreeModel makeModel(Tree t, PageState s) {
            Navigation app = (Navigation)Web.getContext().getApplication();
            Category root = Category.getRootForObject(app);
            return new CategoryTreeModelLite(root);
        }
    }
}
