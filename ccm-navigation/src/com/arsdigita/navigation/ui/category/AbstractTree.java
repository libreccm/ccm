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

import com.arsdigita.navigation.ui.CategoryComponent;
import com.arsdigita.navigation.Navigation;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public abstract class AbstractTree extends CategoryComponent {

    private static Logger s_log = Logger.getLogger(AbstractTree.class);

    protected Element generateTreeXML(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Category cat,
                                      CategoryCollection cats,
                                      BigDecimal[] selected) {
        Map children = new HashMap();
        Category currentCat = new Category(selected[selected.length - 1]);
        while (cats.next()) {
            Category category = cats.getCategory();

            // enable implementations to fettle with the path without breaking the
            // tree that is created, by setting the parent to ensure it is in the
            // tree (this can be used eg to skip categories in some circumstances
            // without breaking the chain from root to the selected category
            BigDecimal parentID = getParentID(cats, currentCat, category, selected);

            Boolean isDefault = (Boolean) cats.get("parents@link.isDefault");

            if (isDefault == null) {
                isDefault = new Boolean(false);
            }
            if (s_log.isDebugEnabled()) {
                Category parent = new Category(parentID);
                s_log.debug("Parent is " + parent.getName());
            }

            Set childList = (Set) children.get(parentID);
            if (childList == null) {
                childList = new TreeSet();
                s_log.debug("Adding new list for this parent");
                children.put(parentID, childList);
            }

            childList.add(new CategorySortKeyPair(category,
                                                  (BigDecimal) cats.get("parents.link.sortKey"),
                                                  isDefault.booleanValue()));

        }

        String path = URLService.locate(cat.getOID());

        return generateNodeXML(request,
                               response,
                               cat,
                               null,
                               selected,
                               children,
                               path,
                               Collections.EMPTY_LIST);

    }

    /**
     * implementations may override this method to change the parent associated with the current  category
     * enabling categories to be skipped without breaking the tree
     */
    protected BigDecimal getParentID(CategoryCollection cats,
                                     Category currentCat,
                                     Category currentChild,
                                     BigDecimal[] selected) {
        return (BigDecimal) cats.get("parents.id");
    }

    protected Element generateNodeXML(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Category cat,
                                      BigDecimal sortKey,
                                      BigDecimal[] selected,
                                      Map children,
                                      String path,
                                      List idPath) {
        if (!cat.isEnabled()) {
            return null;
        }


        s_log.debug("generating node XML for category " + cat.getName());

        // replace idPath list with one containing cat.id appended
        idPath = new ArrayList(idPath);
        idPath.add(cat.getID());
        if (s_log.isDebugEnabled()) {
            StringBuffer buff = new StringBuffer("ID Path is ");
            Iterator it = idPath.iterator();
            while (it.hasNext()) {
                BigDecimal id = (BigDecimal) it.next();
                Category thisCat = new Category(id);
                buff.append(thisCat.getName() + " - ");
            }
            s_log.debug(buff.toString());
        }

        boolean isSelected = false;
        if (selected.length >= idPath.size()) {
            isSelected = true;
            for (int x = 0; x < idPath.size(); x++) {
                if (!idPath.get(x).equals(selected[x])) {
                    isSelected = false;
                }
            }
        } else {
            isSelected = false;
        }

        //if (!isSelected && !cat.isVisible()) {
        if (!cat.isVisible()) {
            return null;
        }

        // We will concatenate category URLs only if all ancestors have
        // their URLs set correctly.  We recognize that is the case if
        // path ends with slash.  Otherwise resort to generic (ie. redirect)
        // URLs.
        boolean concatURLs = (path != null && path.endsWith("/"));

        Element el = generateCategoryXML(request,
                                         response,
                                         cat.getID(),
                                         cat.getName(),
                                         cat.getDescription(),
                                         concatURLs ? path : Navigation.redirectURL(cat.getOID()));

        el.addAttribute("AbstractTree", "AbstractTree");

        el.addAttribute("sortKey", XML.format(sortKey));

        if (isSelected) {
            el.addAttribute("isSelected", "true");
        }

        // compare idPath with the start of selected
//        if (selected.length >= idPath.size()) {
//            boolean isSelected = true;
//            for (int x = 0; x < idPath.size(); x++) {
//                if (!idPath.get(x).equals(selected[x])) {
//                    isSelected = false;
//                }
//            }
//            if (isSelected) {
//                el.addAttribute("isSelected", "true");
//            }
//        }

        // Quasimodo: Begin
        Set c = (Set) children.get(cat.getID());
        if (c != null) {
            Iterator i = c.iterator();

            // Lokale Hilfsvariablen
            long childCounter = 0;
            long maxChildCounter = calcMaxChildCounter(cat, selected);

            while (i.hasNext() && maxChildCounter > 0) {

                CategorySortKeyPair pair = (CategorySortKeyPair) i.next();
                Category child = pair.getCategory();
                BigDecimal childSortKey = pair.getSortKey();

                Element childEl = generateNodeXML(request,
                                                  response,
                                                  child,
                                                  childSortKey,
                                                  selected,
                                                  children,
                                                  concatURLs && child.getURL() != null
                                                  ? path + child.getURL() + "/"
                                                  : null,
                                                  idPath);

                if (childEl != null) {

                    // Respect the calculated maxChildCounter
                    if (childCounter < maxChildCounter) {

                        boolean isDefault = pair.isDefault();
                        childEl.addAttribute("isDefault", String.valueOf(isDefault));

                        el.addContent(childEl);
                        childCounter++;

                    } else {

                        // add showMore attribute
                        el.addAttribute(Menu.MORE_ATTRIB, "true");
                        break;

                    }

                }

            }
        }
        // Quasimodo: End

        return el;
    }

    // Quasimodo: Begin
    // Java ist mal wieder zu blöd richtig zu vergleichen. Es gibt wieder keine Methode, die den Inhalt
    // der Collections vergleicht - equals vergleicht nur auf Objectbasis, also ob es sich um das selbe
    // Object handelt. Was für eine sinnlose Implementierung.
    // Compare the CONTENTS of CategoryCollections
    protected boolean compareCategoryCollection(CategoryCollection a, CategoryCollection b) {

        // Not equal, if sizes don't match
        if (a.size() != b.size()) {
            return false;
        }

        // Access every Object in the Collections
        while (a.next() && b.next()) {

            // If they don't match, they ain't equal
            if (!a.getCategory().equals(b.getCategory())) {
                return false;
            }
        }

        // Hurray, they are equal
        return true;
    }
    // Quasimodo: End

    // Quasimodo: Begin
    // Calculating the maxChildCounter for generateNodeXML
    // This is needed for the adaptive mode of the Menu class. Calculating the maximum shown children per menu.
    // Will be overloaded in Menu class.
    // This is a placeholder to ensure correct operation for AbtractTree depending Classes without overloading 
    // this method, like Hierarchy.
    protected long calcMaxChildCounter(Category cat, BigDecimal[] selected) {
        return new Long(Long.MAX_VALUE).longValue();
    }
    // Quasimodo: End

    private class CategorySortKeyPair implements Comparable {

        private Category m_category;
        private BigDecimal m_sortKey;
        private boolean m_isDefault;

        public CategorySortKeyPair(Category category, BigDecimal sortKey, boolean isDefault) {
            m_category = category;
            m_sortKey = sortKey;
            m_isDefault = isDefault;
        }

        public Category getCategory() {
            return m_category;
        }

        public BigDecimal getSortKey() {
            return m_sortKey;
        }

        public boolean isDefault() {
            return m_isDefault;
        }

        public int compareTo(Object o) {
            return m_sortKey.compareTo(((CategorySortKeyPair) o).m_sortKey);
        }

    }
}
