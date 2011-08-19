package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItem;
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
        final int order1 = navItem.getOrder();
        final int order2 = order1 + 1;

        final PublicPersonalProfileNavItemCollection navItems1 =
                                                     new PublicPersonalProfileNavItemCollection();
        navItems1.addFilter(String.format("navItemOrder = %d", order1));

        final PublicPersonalProfileNavItemCollection navItems2 =
                                                     new PublicPersonalProfileNavItemCollection();
        navItems2.addFilter(String.format("navItemOrder = %d", order2));

        final List<PublicPersonalProfileNavItem> navItemsList1 =
                                                 new ArrayList<PublicPersonalProfileNavItem>();
        final List<PublicPersonalProfileNavItem> navItemsList2 =
                                                 new ArrayList<PublicPersonalProfileNavItem>();

        while (navItems1.next()) {
            navItemsList1.add(navItems1.getNavItem());
        }
        navItems1.rewind();

        while (navItems2.next()) {
            navItemsList2.add(navItems2.getNavItem());
        }
        navItems2.rewind();

        navItems1.close();
        navItems2.close();

        for (PublicPersonalProfileNavItem item : navItemsList1) {
            item.setOrder(order2);
        }

        for (PublicPersonalProfileNavItem item : navItemsList2) {
            item.setOrder(order1);
        }
    }

    public void swapWithPrevious(final PublicPersonalProfileNavItem navItem) {
        final int order1 = navItem.getOrder();
        final int order2 = order1 - 1;

        final PublicPersonalProfileNavItemCollection navItems1 =
                                                     new PublicPersonalProfileNavItemCollection();
        navItems1.addFilter(String.format("navItemOrder = %d", order1));

        final PublicPersonalProfileNavItemCollection navItems2 =
                                                     new PublicPersonalProfileNavItemCollection();
        navItems2.addFilter(String.format("navItemOrder = %d", order2));

        final List<PublicPersonalProfileNavItem> navItemsList1 =
                                                 new ArrayList<PublicPersonalProfileNavItem>();
        final List<PublicPersonalProfileNavItem> navItemsList2 =
                                                 new ArrayList<PublicPersonalProfileNavItem>();

        while (navItems1.next()) {
            navItemsList1.add(navItems1.getNavItem());
        }
        navItems1.rewind();

        while (navItems2.next()) {
            navItemsList2.add(navItems2.getNavItem());
        }
        navItems2.rewind();

        navItems1.close();
        navItems2.close();

        for (PublicPersonalProfileNavItem item : navItemsList1) {
            item.setOrder(order2);
        }

        for (PublicPersonalProfileNavItem item : navItemsList2) {
            item.setOrder(order1);
        }
    }
}
