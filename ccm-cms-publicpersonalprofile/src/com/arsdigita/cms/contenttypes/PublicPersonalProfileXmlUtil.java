/*
 * Copyright (C) 2011-2004 Jens Pelzetter All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfiles;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.ui.UI;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileXmlUtil {

    private final com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfileConfig config =
                                                                                      PublicPersonalProfiles.
            getConfig();

    public void createNavigation(final PublicPersonalProfile profile,
                                 final Element root,
                                 final String navPath,
                                 final String prefix,
                                 final String appPath,
                                 final boolean previewMode) {
        String homeLabelsStr = config.getHomeNavItemLabels();

        Map<String, String> homeLabels = new HashMap<String, String>();
        String[] homeLabelsArry = homeLabelsStr.split(",");
        String[] homeLabelSplit;
        for (String homeLabelEntry : homeLabelsArry) {
            homeLabelSplit = homeLabelEntry.split(":");
            if (homeLabelSplit.length == 2) {
                homeLabels.put(homeLabelSplit[0].trim(),
                               homeLabelSplit[1].trim());
            } else {
                continue;
            }
        }

        String appUrl = null;
        if (previewMode) {
            appUrl = String.format("%s/ccm%s/preview", prefix, appPath);
        } else {
            appUrl = String.format("%s/ccm%s", prefix, appPath);
        }

        final Element navRoot =
                      root.newChildElement("nav:categoryMenu",
                                           "http://ccm.redhat.com/navigation");
        navRoot.addAttribute("id", "categoryMenu");

        final Element navList =
                      navRoot.newChildElement("nav:category",
                                              "http://ccm.redhat.com/navigation");
        navList.addAttribute("AbstractTree", "AbstractTree");
        navList.addAttribute("description", "");
        navList.addAttribute("id", "");
        navList.addAttribute("isSelected", "true");
        navList.addAttribute("sortKey", "");
        navList.addAttribute("title", "publicPersonalProfileNavList");
        /*navList.addAttribute("url", String.format("%s/%s",
         appUrl,
         profile.getProfileUrl()));*/
        navList.addAttribute("url", String.format("/ccm%s",
                                                  UI.getConfig().getRootPage()));


        if (config.getShowHomeNavEntry()) {
            final Element navHome =
                          navList.newChildElement("nav:category",
                                                  "http://ccm.redhat.com/navigation");
            navHome.addAttribute("AbstractTree", "AbstractTree");
            navHome.addAttribute("description", "");
            navHome.addAttribute("id", profile.getID().toString());
            if (navPath == null) {
                navHome.addAttribute("isSelected", "true");
            } else {
                navHome.addAttribute("isSelected", "false");
            }
            navHome.addAttribute("sortKey", "");

            /*String homeLabel = homeLabels.get(GlobalizationHelper.
             getNegotiatedLocale().getLanguage());*/
            final String homeLabel = homeLabels.get(profile.getLanguage());
            if (homeLabel == null) {
                navHome.addAttribute("title", "Home");
            } else {
                navHome.addAttribute("title", homeLabel);
            }
            navHome.addAttribute("url", String.format("%s/%s",
                                                      appUrl,
                                                      profile.getProfileUrl()));
            //navHome.addAttribute("url", String.format("/ccm/%s",
            //      UI.getConfig().getRootPage()));
        }

        //Get the available Navigation items
        final PublicPersonalProfileNavItemCollection navItems =
                                                     new PublicPersonalProfileNavItemCollection();
        /*navItems.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
         getLanguage());*/
        navItems.addLanguageFilter(profile.getLanguage());
        final Map<String, PublicPersonalProfileNavItem> navItemMap =
                                                        new LinkedHashMap<String, PublicPersonalProfileNavItem>();
        PublicPersonalProfileNavItem navItem;
        while (navItems.next()) {
            navItem = navItems.getNavItem();
            navItemMap.put(navItem.getKey(), navItem);
        }

        final Element pathElem =
                      root.newChildElement("nav:categoryPath",
                                           "http://ccm.redhat.com/navigation");
        final Element homeElem =
                      pathElem.newChildElement("nav:category",
                                               "http://ccm.redhat.com/navigation");
        //homeElem.addAttribute("url", String.format("%s/%s",
        //                                         appUrl,
        //                                       profile.getProfileUrl()));
        homeElem.addAttribute("url", String.format("/ccm%s",
                                                   UI.getConfig().getRootPage()));

        final Element profileElem =
                      pathElem.newChildElement("nav:category",
                                               "http://ccm.redhat.com/navigation");
        profileElem.addAttribute("url", String.format("%s/%s",
                                                      appUrl,
                                                      profile.getProfileUrl()));
        if (profile.getOwner() == null) {
            profileElem.addAttribute("title", String.format("Profile %s", profile.getOID().toString()));
        }  else {
        profileElem.addAttribute("title", profile.getOwner().getFullName());
        }

        //Get the related links of the profile
        final DataCollection links =
                             RelatedLink.getRelatedLinks(profile,
                                                         PublicPersonalProfile.LINK_LIST_NAME);
        links.addOrder(Link.ORDER);
        RelatedLink link;
        String navLinkKey;
        Element navElem;
        final List<NavLink> navLinks = new ArrayList<NavLink>();
        while (links.next()) {
            link = (RelatedLink) DomainObjectFactory.newInstance(links.
                    getDataObject());

            navLinkKey = link.getTitle();
            navItem = navItemMap.get(navLinkKey);

            if (navItem == null) {
                continue;
            }

            final ContentItem targetItem = link.getTargetItem();                                    
            //System.out.printf("targetItem.getClass.getName: %s\n", targetItem.getClass().getName());
            if ((targetItem instanceof PublicPersonalProfile)
                || (targetItem instanceof ContentPage)) {                
                final ContentPage targetPage = (ContentPage) targetItem;

                if (!(targetPage.getContentBundle().hasInstance(profile.getLanguage(),
                                                                false))) {
                    System.out.printf("No suitable language found. Continuing...\n");
                    continue;
                }

                System.out.printf("Creating navigation entry for %s\n", navLinkKey);
                navLinks.add(createNavLink(navItem, navLinkKey, targetItem));
            } else {
                System.out.println("targetItem is not a PublicPersonalProfile and not a content item");
            }
        }

        Collections.sort(navLinks);

        for (NavLink navLink : navLinks) {
            navElem =
            navList.newChildElement("nav:category",
                                    "http://ccm.redhat.com/navigation");
            navElem.addAttribute("AbstractTree", "AbstractTree");
            navElem.addAttribute("description", "");
            //navHome.addAttribute("id", "");
            if ((navPath != null) && navPath.equals(navLink.getKey())) {
                navElem.addAttribute("isSelected", "true");
                final Element currentPathElem =
                              pathElem.newChildElement("nav:category",
                                                       "http://ccm.redhat.com/navigation");
                currentPathElem.addAttribute("title", navLink.getNavItem().getLabel());
                currentPathElem.addAttribute("url",
                                             String.format("%s/%s/%s",
                                                           appUrl,
                                                           profile.getProfileUrl(),
                                                           navLink.getKey()));
            } else {
                navElem.addAttribute("isSelected", "false");
            }
            navElem.addAttribute("sortKey", "");
            if (navLink.getTarget() == null) {
                navElem.addAttribute("title", navLink.getKey());
            } else {
                navElem.addAttribute("title", navLink.getNavItem().getLabel());
            }
            navElem.addAttribute("url", String.format("%s/%s/%s",
                                                      appUrl,
                                                      profile.getProfileUrl(),
                                                      navLink.getKey()));

            navElem.addAttribute("navItem", navLink.getKey());

        }
    }

    private NavLink createNavLink(final PublicPersonalProfileNavItem navItem,
                                  final String key,
                                  final ContentItem target) {
        final NavLink navLink = new NavLink();
        navLink.setNavItem(navItem);
        navLink.setKey(key);
        navLink.setTarget(target);
        return navLink;
    }

    private class NavLink implements Comparable<NavLink> {

        private PublicPersonalProfileNavItem navItem;
        private String key;
        private ContentItem target;

        public NavLink() {
            //Nothing
        }

        public PublicPersonalProfileNavItem getNavItem() {
            return navItem;
        }

        public void setNavItem(final PublicPersonalProfileNavItem navItem) {
            this.navItem = navItem;
        }

        public ContentItem getTarget() {
            return target;
        }

        public String getKey() {
            return key;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public void setTarget(final ContentItem target) {
            this.target = target;
        }

        public int compareTo(final NavLink other) {
            return navItem.getOrder().compareTo(other.getNavItem().getOrder());
        }

    }
}
