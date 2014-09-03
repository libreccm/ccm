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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.NavigationConfig;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * CategoryTree component displays children of all categories in the current path.
 *
 * Updated cg - TreeCatProvider interface introduced to provide the categories to be included in the
 * menu. The TreeCatProvider returns a collection of all categories that should be included in the
 * tree - the AbstractTree organises children under their correct parents. This class provides the
 * default implementation of TreeCatProvider. A different default provider may be set in config and
 * providers may be registered for specific categories.
 *
 * Note - the final tree will only include categories where there is a complete trail from a
 * category in the path to the category output by the provider
 *
 * an alternative provider - PopulatedSubCategoryTreeCatProvider is included in this package - it
 * avoids access denied errors and blank index pages by restricting the categories under the current
 * categories to those that contain at least one item visible to the current user
 *
 * Note 2 - changes do not affect the configurable display of nephews & grandchildren as these are
 * added to the path list
 */
public class Menu extends AbstractTree implements TreeCatProvider {

    /**
     * Text for the additional xml attribute for categorys having more children than supposed to be
     * shown.
     */
    public static final String MORE_ATTRIB = "showMore";

    /**
     * used to log errors/warnings etc. to catalina-stdout
     */
    // public static Logger s_log = Logger.getLogger(Menu.class);  ?? public or private ??
    private static Logger s_log = Logger.getLogger(Menu.class);

    private static Map treeCatProviders = new HashMap();

    public static TreeCatProvider defaultProvider = Navigation.getConfig().
            getDefaultMenuCatProvider();

    private String showGrandChildren;
    private long showGrandChildrenMax;
    private long showGrandChildrenMin;
    private long showGrandChildrenLimit;
    private boolean showNephews;

    public Menu() {
        super();
        final NavigationConfig config = Navigation.getConfig();
        showGrandChildren = config.getCategoryMenuShowGrandChildren();
        showGrandChildrenMin = config.getCategoryMenuShowGrandChildrenMin();
        showGrandChildrenMax = config.getCategoryMenuShowGrandChildrenMax();
        showGrandChildrenLimit = config.getCategoryMenuShowGrandChildrenLimit();
        showNephews = config.getCategoryMenuShowNephews();
    }

    /**
     * enables a different strategy to be used to retrieve categories for a specific category
     */
    public static void registerTreeCatProvider(Category cat, TreeCatProvider provider) {
        s_log.debug("registering "
                            + provider.getClass().getName()
                            + " for category "
                            + cat.getName());
        treeCatProviders.put(cat, provider);
    }

    public String getShowGrandChildren() {
        return showGrandChildren;
    }

    public void setShowGrandChildren(final String showGrandChildren) {
        this.showGrandChildren = showGrandChildren;
    }

    public long getShowGrandChildrenMax() {
        return showGrandChildrenMax;
    }

    public void setShowGrandChildrenMax(final long showGrandChildrenMax) {
        this.showGrandChildrenMax = showGrandChildrenMax;
    }

    public long getShowGrandChildrenMin() {
        return showGrandChildrenMin;
    }

    public void setShowGrandChildrenMin(final long showGrandChildrenMin) {
        this.showGrandChildrenMin = showGrandChildrenMin;
    }

    public long getShowGrandChildrenLimit() {
        return showGrandChildrenLimit;
    }

    public void setShowGrandChildrenLimit(final long showGrandChildrenLimit) {
        this.showGrandChildrenLimit = showGrandChildrenLimit;
    }

    public boolean isShowNephews() {
        return showNephews;
    }

    public void setShowNephews(final boolean showNephews) {
        this.showNephews = showNephews;
    }

    public Element generateXML(final HttpServletRequest request,
                               final HttpServletResponse response) {

        //stores how deep we are inside the menu structure
        Category[] path = getModel().getCategoryPath();
        if (path == null || path.length == 0) {
            return null;
        }

        List catIDs = new ArrayList();
        List selectedIDsList = new ArrayList();
        for (int i = 0; i < path.length; i++) {
            catIDs.add(path[i].getID());
            selectedIDsList.add(path[i].getID());
        }

        BigDecimal[] selectedIDs
                     = (BigDecimal[]) selectedIDsList.toArray(
                        new BigDecimal[selectedIDsList.size()]);

        // Quasimodo: Begin
        // If show_grand_children is set to "true" or "adaptive"
        if (showGrandChildren.equals("true") || showGrandChildren.equals("adaptive")) {
            // should add the children categories as potential parents
            if (path.length > 0) {

                // Adaptive Mode
                if (showGrandChildren.equals("adaptive")) {

                    // Show grand children of the first n levels
                    for (int path_it = 0;
                         path_it < path.length && path_it < showGrandChildrenLimit;
                         path_it++) {

                        // add children of all the catgories along the path until limit is reached
                        BigDecimal categoryID = path[path_it].getID();
                        addChildrenOfToList(catIDs, categoryID, categoryID);

                        if (showNephews) {
                            // should add the sibling categories as potential parents
                            if (path_it > 1) {
                                BigDecimal parentID = path[path_it - 1].getID();
                                addChildrenOfToList(catIDs, categoryID, parentID);
                            }
                        }
                    }

                } else {

                    // add children of the current category
                    BigDecimal categoryID = path[path.length - 1].getID();
                    addChildrenOfToList(catIDs, categoryID, categoryID);

                }
            }
        }
        // Quasimodo: End

        if (showNephews) {
            // should add the sibling categories as potential parents
            if (path.length > 1) { //> 1
                BigDecimal categoryID = path[path.length - 1].getID();
                BigDecimal parentID = path[path.length - 2].getID();
                addChildrenOfToList(catIDs, categoryID, parentID);
            }
        }

        DataCollection treeCats = getTreeCatProvider(path[path.length - 1]).getTreeCats(
                catIDs,
                selectedIDs);

        treeCats.addPath("parents.link.sortKey");
        treeCats.addPath("parents.id");

        if (s_log.isDebugEnabled()) {
            while (treeCats.next()) {
                Category cat
                         = (Category) DomainObjectFactory.newInstance(
                                treeCats.getDataObject());
                s_log.debug("treecats - " + cat.getID() + " " + cat.getName());
            }
            treeCats.rewind();

            for (int i = 0; i < selectedIDs.length; i++) {
                Category selected = new Category(selectedIDs[i]);
                s_log.debug("selected - "
                                    + selected.getID()
                                    + " "
                                    + selected.getName());
            }
        }

        Element content = Navigation.newElement("categoryMenu");
        exportAttributes(content);

        Element rootEl = generateTreeXML(request,
                                         response,
                                         path[0],
                                         new CategoryCollection(treeCats),
                                         selectedIDs);

        if (rootEl != null) {
            content.addContent(rootEl);
        }

        return content;
    }

    /**
     * @param category
     */
    protected TreeCatProvider getTreeCatProvider(Category category) {
        TreeCatProvider provider = (TreeCatProvider) treeCatProviders.get(category);
        s_log.debug(treeCatProviders.size() + " providers registered");

        if (provider == null) {
            s_log.debug("No provider registered for this category");
            provider = defaultProvider;
        } else {
            s_log.debug("provider for this category is " + provider.getClass().getName());
        }
        return provider;
    }

    /**
     * retrieve data collection of all categories that are to appear in the menu Default retrieves
     * all children of categories included in catList.
     *
     * @param catIDs
     * @param selectedIDs
     * @return
     */
    public DataCollection getTreeCats(List catIDs, BigDecimal[] selectedIDs) {
        DataCollection treeCats
                       = SessionManager.getSession().retrieve(
                        Category.BASE_DATA_OBJECT_TYPE);
        // Filter to all children of :ids
        treeCats.addFilter("parents.id in :ids").set("ids", catIDs);
        treeCats.addEqualsFilter("parents.link.relationType", Category.CHILD);

        return treeCats;
    }

    /**
     * Adds the IDs for the menu entrys to the menu..
     *
     * @param List catIDs
     * @param BigDecimal categoryID
     * @param BigDecimal parentID ID of the parent menu entry... only those entrys will be added who
     * have this ID as parent id
     */
    protected void addChildrenOfToList(
            List catIDs,
            BigDecimal categoryID,
            BigDecimal parentID) {
        BigDecimal childID;
        DataCollection childCats
                       = SessionManager.getSession().retrieve(
                        Category.BASE_DATA_OBJECT_TYPE);
        childCats.addFilter("parents.id = :id").set("id", parentID);
        childCats.addEqualsFilter("parents.link.relationType", Category.CHILD);
        while (childCats.next()) {
            childID = (new Category(childCats.getDataObject())).getID();
            // Don't add the category a second time
            if (!childID.equals(categoryID)) {
                catIDs.add(childID);
            }
        }
    }

    // Quasimodo: Begin
    // Calculating the maxChildCounter for generateNodeXML
    // This is needed for the adaptive mode of the Menu class. Calculating the maximum shown children per menu
    protected long calcMaxChildCounter(Category cat, BigDecimal[] selected) {

        // Show all children of a category if:
        // com.arsdigita.navigation.category_menu_show_grand_children is not set to adaptive or
        // com.arsdigita.navigation.category_menu_show_nephews is set to true and this category has the same parents as the active category or
        // this category is part of the current path
        if (!(showGrandChildren.equals("adaptive"))
                    || (showNephews && compareCategoryCollection(
                        cat.getParents(), new Category(selected[selected.length - 1]).getParents()))
                    || Arrays.asList(selected).contains(cat.getID())) {

            return new Long(Long.MAX_VALUE).longValue();

        } else {

            // Child of active category
            if ((new Category(selected[selected.length - 1])).isMemberOfSubtree(cat)) {

                // If com.arsdigita.navigation.category_menu_show_grand_children_max = 0
                if (showGrandChildrenMax == 0) {

                    // Show all children
                    return new Long(Long.MAX_VALUE).longValue();

                } else {

                    // Show com.arsdigita.navigation.category_menu_show_grand_children_max children 
                    return showGrandChildrenMax;

                }

            } else {

                // Show com.arsdigita.navigation.category_menu_show_grand_children_min children 
                return showGrandChildrenMin;

            }
        }

    }

}
