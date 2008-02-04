/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.user;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;


public class BundleList extends List {
    public BundleList() {
        super(new BundleListModelBuilder());
        
        setCellRenderer(new CellRenderer());
    }

    public final boolean isVisible(final PageState state) {
        return CMS.getContext().hasContentItem() && super.isVisible(state);
    }

    private static class BundleListModelBuilder
            extends AbstractListModelBuilder {
        public final ListModel makeModel(final List list,
                                         final PageState state) {
            final Folder parent = (Folder) CMS.getContext().getContentItem();

            final DataCollection bundles =
                SessionManager.getSession().retrieve(ContentBundle.BASE_DATA_OBJECT_TYPE);

            bundles.addEqualsFilter(ContentItem.PARENT, parent.getID());
            bundles.addOrder("lower(" + ACSObject.DISPLAY_NAME + ")");

            return new Model(new ItemCollection(bundles));
        }

        private class Model implements ListModel {
            private final ItemCollection m_items;

            Model(final ItemCollection items) {
                m_items = items;
            }

            public final boolean next() {
                if (m_items.next()) {
                    return true;
                } else {
                    m_items.close();

                    return false;
                }
            }

            public final Object getElement() {
                return m_items.getDisplayName();
            }

            public final String getKey() {
                return m_items.getName() + ".jsp";
            }
        }
    }

    private class CellRenderer implements ListCellRenderer {
        public final Component getComponent(final List list,
                                            final PageState state,
                                            final Object value,
                                            final String key,
                                            final int index,
                                            final boolean isSelected) {
            return new Link((String) value, key);
        }
    }
}
