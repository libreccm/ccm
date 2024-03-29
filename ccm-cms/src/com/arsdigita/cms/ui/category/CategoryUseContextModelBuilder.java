/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import org.apache.log4j.Logger;

/**
 * Builds a list of category use contexts for the current
 * content section.
 *
 * @author Scott Seago 
 * @version $Id: CategoryUseContextModelBuilder.java 2090 2010-04-17 08:04:14Z pboy $
 */
class CategoryUseContextModelBuilder extends AbstractListModelBuilder {

    public static String DEFAULT_USE_CONTEXT = "<default>";

    private static final Logger s_log = Logger.getLogger
        (CategoryUseContextModelBuilder.class);

    public final ListModel makeModel(final List list, final PageState state) {
        return new Model();
    }

    private class Model implements ListModel {
        private final RootCategoryCollection m_roots;

        public Model() {
            final ContentSection section =
                CMS.getContext().getContentSection();

            m_roots = Category.getRootCategories(section);
            m_roots.addOrder(Category.USE_CONTEXT);
        }

        public boolean next() {
            return m_roots.next();
        }

        public Object getElement() {
            String useContext = m_roots.getUseContext();
            return useContext == null ? DEFAULT_USE_CONTEXT : useContext;
        }

        public String getKey() {
            String useContext = m_roots.getUseContext();
            return useContext == null ? DEFAULT_USE_CONTEXT : useContext;
        }
    }
}
