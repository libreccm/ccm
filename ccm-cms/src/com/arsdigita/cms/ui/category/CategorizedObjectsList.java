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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.util.LockableImpl;

import java.math.BigDecimal;
import javax.servlet.ServletException;


/**
 * A List of all objects currently categorized under this category
 *
 * @author Randy Graebner (randyg@redhat.com)
 * @version $Revision: #18 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Revision: #18 $Id: CategorizedObjectsList.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class CategorizedObjectsList extends SortableCategoryList {

    public final static String CATEGORIZED_OBJECTS = "co";

    public CategorizedObjectsList(final CategoryRequestLocal category) {
        super(category);

        setModelBuilder(new CategorizedObjectsModelBuilder());

        Label label = new Label(GlobalizationUtil.globalize
                            ("cms.ui.category.item.none"));
        label.setFontWeight(Label.ITALIC);
        setEmptyView(label);
    }

    /**
     *  This actually performs the sorting
     */
    public void respond(PageState ps) throws ServletException {
        String event = ps.getControlEventName();

        if (NEXT_EVENT.equals(event) || PREV_EVENT.equals(event)) {
            BigDecimal selectedID = new BigDecimal(ps.getControlEventValue());
            Category parent = getCategory(ps);
            
            if (CMS.getContext().getSecurityManager().canAccess
                (SecurityManager.CATEGORY_ADMIN)) {
                parent.swapSortKeys(selectedID, getSwapID(parent,selectedID,event));
            }
        } else {
            super.respond(ps);
        }
    }

    protected BigDecimal getSwapID(Category category, BigDecimal selectedID, String event) {
        BigDecimal priorID = null;
        BigDecimal swapID = null;
        boolean foundSelectedID = false;

        if (category != null && category.hasChildObjects()) {
            CategorizedCollection items = category.getObjects
                (ContentItem.BASE_DATA_OBJECT_TYPE);
            items.addEqualsFilter(ContentItem.VERSION,ContentItem.LIVE);
            items.sort(true);
            while (items.next()) {
                BigDecimal thisID = items.getACSObject().getID();
                if (foundSelectedID == true && NEXT_EVENT.equals(event)) {
                    swapID = thisID;
                    break;
                }

                if (thisID.equals(selectedID)) {
                    foundSelectedID = true;
                    if (PREV_EVENT.equals(event)) {
                        swapID = priorID;
                        break;
                    }
                }

                priorID = thisID;
            }
            items.close();
        }
        return swapID;
    }

    private class CategorizedObjectsModelBuilder extends LockableImpl
            implements ListModelBuilder {
        public final ListModel makeModel(final List list,
                                         final PageState state) {
            final Category category = getCategory(state);

            if (category != null && category.hasChildObjects()) {
                CategorizedCollection items = category.getObjects
                    (ContentItem.BASE_DATA_OBJECT_TYPE);
                items.addEqualsFilter(ContentItem.VERSION,ContentItem.LIVE);
                items.sort(true);
                return new CategorizedCollectionListModel(items);
            } else {
                return List.EMPTY_MODEL;
            }
        }
    }

    /**
     * A {@link ListModel} that iterates over categorized objects via an iterator
     */
    private static class CategorizedCollectionListModel implements ListModel {
        private CategorizedCollection m_objs;
        private ACSObject m_object;

        public CategorizedCollectionListModel(CategorizedCollection coll) {
            m_objs = coll;
            m_object = null;
        }

        public boolean next() {
            if ( m_objs.next() ) {
                m_object = (ACSObject) m_objs.getDomainObject();
                return true;
            } else {
                return false;
            }
        }

        public Object getElement() {
            return m_object.getDisplayName();
        }

        public String getKey() {
            return m_object.getID().toString();
        }
    }
}
