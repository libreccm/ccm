package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfiles;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;
import java.util.HashMap;
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
                                    final String navPath) {
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

        Element navRoot =
                root.newChildElement("nav:categoryMenu",
                                     "http://ccm.redhat.com/london/navigation");
        navRoot.addAttribute("id", "categoryMenu");

        Element navList =
                navRoot.newChildElement("nav:category",
                                        "http://ccm.redhat.com/london/navigation");
        navList.addAttribute("AbstractTree", "AbstractTree");
        navList.addAttribute("description", "");
        navList.addAttribute("id", "");
        navList.addAttribute("isSelected", "true");
        navList.addAttribute("sortKey", "");
        navList.addAttribute("title", "publicPersonalProfileNavList");
        navList.addAttribute("url", String.format("/ccm/%s",
                                                  profile.getProfileUrl()));

        Element navHome =
                navList.newChildElement("nav:category",
                                        "http://ccm.redhat.com/london/navigation");
        navHome.addAttribute("AbstractTree", "AbstractTree");
        navHome.addAttribute("description", "");
        navHome.addAttribute("id", profile.getID().toString());
        if (navPath == null) {
            navHome.addAttribute("isSelected", "true");
        } else {
            navHome.addAttribute("isSelected", "false");
        }
        navHome.addAttribute("sortKey", "");
        String homeLabel = homeLabels.get(DispatcherHelper.getNegotiatedLocale().
                getLanguage());
        if (homeLabel == null) {
            navHome.addAttribute("title", "Home");
        } else {
            navHome.addAttribute("title", homeLabel);
        }
        navHome.addAttribute("url", String.format("/ccm/profiles/%s",
                                                  profile.getProfileUrl()));

        //Get the available Navigation items
        PublicPersonalProfileNavItemCollection navItems =
                                               new PublicPersonalProfileNavItemCollection();
        navItems.addLanguageFilter(DispatcherHelper.getNegotiatedLocale().
                getLanguage());
        final Map<String, PublicPersonalProfileNavItem> navItemMap =
                                                        new HashMap<String, PublicPersonalProfileNavItem>();
        PublicPersonalProfileNavItem navItem;
        while (navItems.next()) {
            navItem = navItems.getNavItem();
            navItemMap.put(navItem.getKey(), navItem);
        }

        //Get the related links of the profiles
        DataCollection links =
                       RelatedLink.getRelatedLinks(profile,
                                                   PublicPersonalProfile.LINK_LIST_NAME);
        links.addOrder(Link.ORDER);
        RelatedLink link;
        String navLinkKey;
        Element navElem;
        while (links.next()) {
            link = (RelatedLink) DomainObjectFactory.newInstance(links.
                    getDataObject());

            navLinkKey = link.getTitle();
            navItem = navItemMap.get(navLinkKey);

            if (navItem == null) {
                //ToDo
            }

            navElem =
            navList.newChildElement("nav:category",
                                    "http://ccm.redhat.com/london/navigation");
            navElem.addAttribute("AbstractTree", "AbstractTree");
            navElem.addAttribute("description", "");
            //navHome.addAttribute("id", "");
            if ((navPath != null) && navPath.equals(navLinkKey)) {
                navElem.addAttribute("isSelected", "true");
            } else {
                navElem.addAttribute("isSelected", "false");
            }
            navElem.addAttribute("sortKey", "");
            if (navItem == null) {
                navElem.addAttribute("title", navLinkKey);
            } else {
                navElem.addAttribute("title", navItem.getLabel());
            }
            navElem.addAttribute("url", String.format("/ccm/profiles/%s/%s",
                                                      profile.getProfileUrl(),
                                                      navLinkKey));
            
            navElem.addAttribute("navItem", navLinkKey);

        }
    }
}
