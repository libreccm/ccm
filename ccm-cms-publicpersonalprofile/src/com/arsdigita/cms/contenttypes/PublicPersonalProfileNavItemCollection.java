/*
 * Copyright (c) 2011 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavItemCollection extends DomainCollection {

    private Filter keyFilter = null;
    private Filter languageFilter = null;

    public PublicPersonalProfileNavItemCollection() {
        this(SessionManager.getSession().retrieve(
                PublicPersonalProfileNavItem.BASE_DATA_OBJECT_TYPE));
    }

    public PublicPersonalProfileNavItemCollection(
            final DataCollection dataCollection) {
        super(dataCollection);

        addOrder("navItemOrder, key, lang");
    }

    public PublicPersonalProfileNavItem getNavItem() {
        return new PublicPersonalProfileNavItem(m_dataCollection.getDataObject());
    }

    public final void addKeyFilter(final String key) {
        keyFilter = this.addEqualsFilter(PublicPersonalProfileNavItem.KEY,
                                         key);
    }

    public boolean removeKeyFilter(final String key) {
        boolean retVal = false;

        retVal = this.removeFilter(keyFilter);
        if (retVal == true) {
            keyFilter = null;
        }
        return retVal;
    }

    public final void addLanguageFilter(final String language) {
        languageFilter = this.addEqualsFilter(PublicPersonalProfileNavItem.LANG,
                                              language);
    }

    public boolean removeLanguageFilter(final String language) {
        boolean retVal = false;

        retVal = this.removeFilter(languageFilter);
        if (retVal == true) {
            languageFilter = null;
        }
        return retVal;
    }

    public void removeAllFilters() {
        this.removeAllFilters();
    }

    public final String getKey() {
        if (this.isBeforeFirst()) {
            this.next();
        }

        return (String) get(PublicPersonalProfileNavItem.KEY);
    }

    public final String getLanguage() {
        if (this.isBeforeFirst()) {
            this.next();
        }

        return (String) get(PublicPersonalProfileNavItem.LANG);
    }

    public PublicPersonalProfileNavItem getNavItem(final String key,
                                                   final String language) {
        if (!(this.isBeforeFirst()) && key.equals(this.getKey()) && language.
                equals(this.getLanguage())) {
            return this.getNavItem();
        } else {
            this.rewind();

            while (this.next()) {
                if (key.equals(this.getKey()) && language.equals(this.
                        getLanguage())) {
                    return this.getNavItem();
                }
            }
        }

        return null;
    }

    public void swapWithNext(final PublicPersonalProfileNavItem navItem) {
        final PublicPersonalProfileNavItemCollection collection =
                                                     new PublicPersonalProfileNavItemCollection();
        collection.addOrder("navItemOrder, key, lang, label");

        final List<List<PublicPersonalProfileNavItem>> navItems =
                                                       generateNavItemList(
                new PublicPersonalProfileNavItemCollection());

        int i = 0;
        for (i = 0; i < navItems.size(); i++) {
            if (navItem.getKey().equals(navItems.get(i).get(0).getKey())) {
                break;
            }
        }

        if (i == navItems.size() - 1) {
            throw new IllegalArgumentException(
                    "Provided navItem instance is the last one, therefore there is no next item to switch with.");
        }

        final int navItemOrder = navItem.getOrder();
        final int nextOrder = navItems.get(i + 1).get(0).getOrder();
        
        for(PublicPersonalProfileNavItem item : navItems.get(i)) {
            item.setOrder(nextOrder);
        }
        
        for(PublicPersonalProfileNavItem item : navItems.get(i + 1)) {
            item.setOrder(navItemOrder);            
        }      
    }

    public void swapWithPrevious(final PublicPersonalProfileNavItem navItem) {
         final PublicPersonalProfileNavItemCollection collection =
                                                     new PublicPersonalProfileNavItemCollection();
        collection.addOrder("key");

        final List<List<PublicPersonalProfileNavItem>> navItems =
                                                       generateNavItemList(
                new PublicPersonalProfileNavItemCollection());

        int i = 0;
        for (i = 0; i < navItems.size(); i++) {
            if (navItem.getKey().equals(navItems.get(i).get(0).getKey())) {
                break;
            }
        }

        if (i == 0) {
            throw new IllegalArgumentException(
                    "Provided navItem instance is the first one, therefore there is no previous item to switch with.");
        }

        final int navItemOrder = navItem.getOrder();
        final int nextOrder = navItems.get(i - 1).get(0).getOrder();
        
        for(PublicPersonalProfileNavItem item : navItems.get(i)) {
            item.setOrder(nextOrder);
        }
        
        for(PublicPersonalProfileNavItem item : navItems.get(i - 1)) {
            item.setOrder(navItemOrder);            
        }               
    }

    private List<List<PublicPersonalProfileNavItem>> generateNavItemList(
            final PublicPersonalProfileNavItemCollection collection) {
        final List<List<PublicPersonalProfileNavItem>> list =
                                                       new ArrayList<List<PublicPersonalProfileNavItem>>();

        String lastKey = "";
        PublicPersonalProfileNavItem current;
        List<PublicPersonalProfileNavItem> currentSubList = null;

        collection.rewind();
        while (collection.next()) {
            current = collection.getNavItem();

            if (!current.getKey().equals(lastKey)) {
                currentSubList = new ArrayList<PublicPersonalProfileNavItem>();
                list.add(currentSubList);
            }

            currentSubList.add(current);
            lastKey = current.getKey();
        }

        return list;
    }
}
