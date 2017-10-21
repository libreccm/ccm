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

package com.arsdigita.navigation.ui.category;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>A widget to output a category hierarchy and optionally items in part of
 * the hierarchy. The widget indicates the current category in its output.</p>
 *
 */
public class Hierarchy extends AbstractTree {
    private static final Logger s_log =
        Logger.getLogger( Hierarchy.class );

    private boolean m_showItems = false;
    private Map m_itemStore;
    private int m_depth = -1;
    private BigDecimal m_currentCategoryID;

    /**
     * If set to true, the widget will output items as well as categories. It
     * will only output items in the category returned by getItemsRootCategory()
     * and its subcategories. Set to false by default.
     * @param showItems
     */
    public void setShowItems( boolean showItems ) {
        m_showItems = showItems;
    }

    @Override
    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Category rootCategory = getModel().getRootCategory();
        Category currentCategory = getModel().getCategory();

        if (null != currentCategory) {
            m_currentCategoryID = currentCategory.getID();
        }

        // First deal with items if we're to show them.
        // Store them in HashMap, the key being the categoryID.
        // Each entry in the map is a List.  Each entry in the List is a Map
        // returned by DataQuery.getPropertyValues().
        m_itemStore = new HashMap();
        s_log.debug("m_showItems: " + m_showItems);
        if (m_showItems) {
            if (null != rootCategory) {
                DataQuery items = SessionManager.getSession().retrieveQuery
                    ( "com.arsdigita.categorization.categorizedItemsInSubtree" );
                items.addOrder( "ancestors" );
                items.setParameter( "id", rootCategory.getID() );

                String childQuery =
                    "com.arsdigita.categorization.liveIndexItemsInSubtree";
                Filter children = items.addNotInSubqueryFilter( "id", childQuery );
                children.set( "categoryID", rootCategory.getID() );

                while (items.next()) {
                    BigDecimal itemCategoryID = (BigDecimal) items.get("categoryID");
                    List categorizedItems = (List) m_itemStore.get(itemCategoryID);
                    if (categorizedItems == null) {
                        categorizedItems = new ArrayList();
                        m_itemStore.put(itemCategoryID, categorizedItems);
                    }
                    Map properties = new HashMap();
                    properties.put("objectType", (String) items.get( "objectType" ));
                    properties.put("title",  (String) items.get( "title" ));
                    properties.put("id", (BigDecimal) items.get( "id" ));
                    categorizedItems.add(properties);
                    s_log.debug("retrieved catID: " + itemCategoryID + " item: " + properties);
                }
            }
        }

        Category[] path = getModel().getCategoryPath();
        //Set selectedIDs = new HashSet();
        List selectedIDsList = new ArrayList();
        for (int i = 0 ; i < path.length ; i++) {
            selectedIDsList.add(path[i].getID());
        }
        BigDecimal[] selectedIDs = (BigDecimal[])
            selectedIDsList.toArray(new BigDecimal[selectedIDsList.size()]);

        CategoryCollection treeCats = rootCategory.getDescendants();
        treeCats.addPath("parents.link.sortKey");
        treeCats.addPath("parents.id");

        Element categoryHierarchy = Navigation.newElement("categoryHierarchy");

        m_depth = -1;

        Element rootEl = generateTreeXML(request,
                                         response,
                                         path[0],
                                         treeCats,
                                         selectedIDs);
        if (rootEl != null) {
            // Don't generate XML for Root Aplaws Navigation category
            List rootCats = rootEl.getChildren();
            for (Iterator it=rootCats.iterator(); it.hasNext(); ) {
                categoryHierarchy.addContent( (Element) it.next());
            }
        }

        return categoryHierarchy;

    }


    protected Element generateNodeXML(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Category cat,
                                      BigDecimal sortKey,
                                      BigDecimal[] selected,
                                      Map children,
                                      String path,
                                      List idPath) {

        m_depth++;

        BigDecimal categoryID = cat.getID();

        s_log.debug("Generating node for: " + cat);

        Element content = super.generateNodeXML(request,
                                                response,
                                                cat,
                                                sortKey,
                                                selected,
                                                children,
                                                null,
                                                idPath);

        if (content == null) {
            m_depth--;
            return null;
        }

        // provide depth attribute
        content.addAttribute("depth", String.valueOf(m_depth-1));

        List items = (List) m_itemStore.get(categoryID);
        if (items != null) {
            s_log.debug("Found a categorized item for category ID: " + categoryID);
            Iterator it = items.iterator();
            while (it.hasNext()) {
                Map properties = (Map) it.next();
                Element item = Navigation.newElement("categoryItem");
                content.addContent(item);
                BigDecimal itemID = (BigDecimal) properties.get( "id" );
                String itemObjectType = (String) properties.get( "objectType" );
                String itemTitle = (String) properties.get( "title" );

                OID itemOID = new OID( itemObjectType, itemID );

                item.addAttribute( "oid", itemOID.toString() );
                item.addAttribute( "objectType", itemObjectType );
                item.addAttribute( "title", itemTitle );
            }
        }
        m_depth--;
        return content;
    }

}
