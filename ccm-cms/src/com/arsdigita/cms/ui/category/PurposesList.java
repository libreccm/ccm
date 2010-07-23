/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;

import java.util.Collection;


/**
 * A List of all purposes for the current category.
 *
 * @author Scott Seago (scott@arsdigita.com)
 * @version $Revision: #11 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class PurposesList extends List {

    public final static String PURPOSES_LIST = "pl";

    private final CategoryRequestLocal m_category;

    public PurposesList(final CategoryRequestLocal category) {
        super(new ParameterSingleSelectionModel
              (new BigDecimalParameter(PURPOSES_LIST)));
        setIdAttr("purposes_list");

        m_category = category;

        setModelBuilder(new CategoryPurposesModelBuilder());
        setCellRenderer(new ListCellRenderer() {

                public Component getComponent(List list, PageState state, Object value,
                                              String key, int index, boolean isSelected)
                {
                    return new Label(value.toString());
                }
            });

        Label l = new Label(GlobalizationUtil.globalize
                            ("cms.ui.category.purpose_none"));
        l.setFontWeight(Label.ITALIC);
        setEmptyView(l);
    }

    private class CategoryPurposesModelBuilder extends LockableImpl
        implements ListModelBuilder {

        public CategoryPurposesModelBuilder() {
            super();
        }

        public ListModel makeModel(List l, PageState state) {
            Category category = m_category.getCategory(state);
            if ( category != null && !category.isRoot() ) {

                Collection purposes = category.getPurposes();
                return new CategoryPurposeIteratorListModel(purposes.iterator());

            } else {
                return List.EMPTY_MODEL;
            }
        }
    }

}
