package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.publicpersonalprofile.ContentGenerator;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfileConfig;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfileXmlGenerator;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfiles;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletException;

/**
 * Generates the extra XML output for a profile for the embedded view.
 *
 * @author Jens Pelzetter
 * @version $Id: PublicPersonalProfileExtraXmlGenerator.java 1466 2012-01-23
 * 12:59:16Z jensp $
 */
public class PublicPersonalProfileExtraXmlGenerator implements ExtraXMLGenerator {

    private static final PublicPersonalProfileConfig config =
                                                     PublicPersonalProfiles.
            getConfig();
    public static final String SHOW_ITEM_PARAM = "showItem";

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof PublicPersonalProfile)) {
            throw new IllegalArgumentException(
                    "PublicPersonalProfileExtraXMLGenerator can only process PublicPersonalProfile Items");
        }

        final PublicPersonalProfile profile = (PublicPersonalProfile) item;
        final String showItem = state.getRequest().getParameter(SHOW_ITEM_PARAM);

        if (!config.getEmbedded() && state.getRequestURI().contains(profile.
                getName())) {
            /*
             * try {
             * DispatcherHelper.forwardRequestByPath(getProfileUrl(profile),
             * state.getRequest(), state.getResponse()); return; } catch
             * (IOException ex) { throw new UncheckedWrapperException(ex); }
             * catch (ServletException ex) { throw new
             * UncheckedWrapperException(ex); }
             */
            throw new RedirectSignal(getProfileUrl(profile), true);
        }

        if (config.getEmbedded()) {
            final Element navigation = element.newChildElement(
                    "profileNavigation");
            final PublicPersonalProfileXmlUtil util =
                                               new PublicPersonalProfileXmlUtil();
            String prefix = DispatcherHelper.getDispatcherPrefix(state.
                    getRequest());
            if (prefix == null) {
                prefix = "";
            }
            util.createNavigation(profile, navigation, showItem, prefix, "",
                                  false);
        }

        if ((showItem == null) || showItem.trim().isEmpty()) {
            final Element profileOwner = element.newChildElement("profileOwner");

            final GenericPerson owner = profile.getOwner();

            final PublicPersonalProfileXmlGenerator generator =
                                                    new PublicPersonalProfileXmlGenerator(
                    owner);
            generator.setItemElemName("owner", "");
            generator.generateXML(state, profileOwner, "");

            final Element contactsElem =
                          profileOwner.newChildElement("contacts");
            final GenericPersonContactCollection contacts = owner.getContacts();
            while (contacts.next()) {
                PublicPersonalProfileXmlGenerator cGenerator =
                                                  new PublicPersonalProfileXmlGenerator(
                        contacts.getContact());
                cGenerator.setItemElemName("contact", "");
                cGenerator.addItemAttribute("contactType", 
                                            contacts.getContactType());
                cGenerator.generateXML(state, contactsElem, "");
            }

        } else {
            final Element profileContent = element.newChildElement(
                    "profileContent");

            final DataCollection links =
                                 RelatedLink.getRelatedLinks(profile,
                                                             PublicPersonalProfile.LINK_LIST_NAME);
            links.addFilter(String.format("linkTitle = '%s'",
                                          showItem));

            if (links.size() == 0) {
                profileContent.newChildElement(
                        "notFound");
            } else {
                PublicPersonalProfileNavItemCollection navItems =
                                                       new PublicPersonalProfileNavItemCollection();
                navItems.addLanguageFilter(GlobalizationHelper.
                        getNegotiatedLocale().
                        getLanguage());
                navItems.addKeyFilter(showItem);
                navItems.next();

                if (navItems.getNavItem().getGeneratorClass()
                    != null) {
                    try {
                        Object generatorObj =
                               Class.forName(navItems.getNavItem().
                                getGeneratorClass()).getConstructor().
                                newInstance();

                        if (generatorObj instanceof ContentGenerator) {
                            final ContentGenerator generator =
                                                   (ContentGenerator) generatorObj;

                            generator.generateContent(profileContent,
                                                      profile.getOwner(),
                                                      state,
                                                      profile.getLanguage());

                        } else {
                            throw new UncheckedWrapperException(String.format(
                                    "Class '%s' is not a ContentGenerator.",
                                    navItems.getNavItem().
                                    getGeneratorClass()));
                        }

                    } catch (InstantiationException ex) {
                        throw new UncheckedWrapperException(
                                "Failed to create generator", ex);
                    } catch (IllegalAccessException ex) {
                        throw new UncheckedWrapperException(
                                "Failed to create generator", ex);
                    } catch (IllegalArgumentException ex) {
                        throw new UncheckedWrapperException(
                                "Failed to create generator", ex);
                    } catch (InvocationTargetException ex) {
                        throw new UncheckedWrapperException(
                                "Failed to create generator", ex);
                    } catch (ClassNotFoundException ex) {
                        throw new UncheckedWrapperException(
                                "Failed to create generator", ex);
                    } catch (NoSuchMethodException ex) {
                        throw new UncheckedWrapperException(
                                "Failed to create generator", ex);
                    }
                } else {

                    links.next();
                    final RelatedLink link =
                                      (RelatedLink) DomainObjectFactory.
                            newInstance(links.getDataObject());
                    final ContentItem targetItem = link.getTargetItem();
                    final PublicPersonalProfileXmlGenerator generator =
                                                            new PublicPersonalProfileXmlGenerator(
                            targetItem);
                    generator.generateXML(state,
                                          profileContent,
                                          "");
                }

                navItems.close();
            }
        }
    }

    public void addGlobalStateParams(final Page p) {
        //Nothing yet
    }

    @Override
    public void setListMode(final boolean listMode) {
        //nothing
    }
    
    private String getProfileUrl(final PublicPersonalProfile profile) {
        final GenericPerson owner = profile.getOwner();
        final GenericPersonContactCollection contacts = owner.getContacts();

        String homepage = null;
        while (contacts.next() && (homepage == null)) {
            homepage = getHomepageContactEntry(contacts.getContact());
        }

        contacts.close();
        return homepage;
    }

    private String getHomepageContactEntry(final GenericContact contact) {
        final GenericContactEntryCollection entries =
                                            contact.getContactEntries();

        String homepage = null;
        while (entries.next()) {
            if ("homepage".equals(entries.getKey())) {
                homepage = entries.getValue();
                break;
            }
        }

        entries.close();
        return homepage;
    }
}
