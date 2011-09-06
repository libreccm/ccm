package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.cms.publicpersonalprofile.ui.PublicPersonalProfileNavItemsTable;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericContactEntryCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItem;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItemCollection;
import com.arsdigita.cms.publicpersonalprofile.ui.PublicPersonalProfileNavItemsAddForm;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.toolbox.ui.ApplicationAuthenticationListener;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfilesServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -1495852395804455609L;
    private static final Logger logger =
                                Logger.getLogger(
            PublicPersonalProfilesServlet.class);
    private static final String ADMIN = "admin";
    private static final String PREVIEW = "preview";
    private static final String PPP_NS =
                                "http://www.arsdigita.com/PublicPersonalProfile/1.0";
    public static final String SELECTED_NAV_ITEM = "selectedNavItem";
    private final PublicPersonalProfileConfig config = PublicPersonalProfiles.
            getConfig();

    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Application app) throws ServletException,
                                                           IOException {
        String path = "";

        logger.debug("PublicPersonalProfileServlet is starting...");
        logger.debug(String.format("pathInfo = '%s'", request.getPathInfo()));

        logger.debug("Extracting path from pathInfo by removing leading and "
                     + "trailing slashes...");
        if (request.getPathInfo() != null) {
            if ("/".equals(request.getPathInfo())) {
                path = "";
            } else if (request.getPathInfo().startsWith("/")
                       && request.getPathInfo().endsWith("/")) {
                path = request.getPathInfo().substring(1, request.getPathInfo().
                        length() - 1);
            } else if (request.getPathInfo().startsWith("/")) {
                path = request.getPathInfo().substring(1);
            } else if (request.getPathInfo().endsWith("/")) {
                path = request.getPathInfo().substring(0, request.getPathInfo().
                        length() - 1);
            } else {
                path = request.getPathInfo();
            }
        }

        logger.debug(String.format("path = %s", path));

        //Displays a text/plain page with a message.
        if (path.isEmpty()) {
            logger.debug("pathInfo is null, responding with default...");

            response.setContentType("text/plain");
            response.getWriter().append("Please choose an application.");
        } else {
            final String[] pathTokens = path.split("/");
            boolean preview = false;
            String profileOwner = "";
            String navPath = null;

            Page page;


            page = PageFactory.buildPage("PublicPersonalProfile",
                                         "");

            if (pathTokens.length < 1) {
                //ToDo: Fehlerbehandlung?
            } else {
                if (ADMIN.equals(pathTokens[0])) {
                    showAdminPage(page, request, response);
                    return;
                }

                if (pathTokens.length >= 1) {
                    if (PREVIEW.equals(pathTokens[0])) {
                        preview = true;
                        profileOwner = pathTokens[1];
                        if (pathTokens.length > 2) {
                            navPath = pathTokens[2];
                        }
                    } else {
                        profileOwner = pathTokens[0];
                        if (pathTokens.length > 1) {
                            navPath = pathTokens[1];
                        }
                    }
                }

                page.lock();

                Document document = page.buildDocument(request, response);
                Element root = document.getRootElement();

                final Session session = SessionManager.getSession();

                DataCollection profiles =
                               session.retrieve(
                        com.arsdigita.cms.contenttypes.PublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
                profiles.addFilter(String.format("profileUrl = '%s'",
                                                 profileOwner));
                if (preview) {
                    profiles.addFilter(String.format("version = '%s'",
                                                     ContentItem.DRAFT));
                } else {
                    profiles.addFilter(String.format("version = '%s'",
                                                     ContentItem.LIVE));
                }

                if (profiles.size() == 0) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                } else if (profiles.size() > 1) {
                    throw new IllegalStateException(
                            "More than one matching members found...");
                } else {
                    final PageState state = new PageState(page,
                                                          request,
                                                          response);

                    profiles.next();
                    PublicPersonalProfile profile =
                                          (PublicPersonalProfile) DomainObjectFactory.
                            newInstance(profiles.getDataObject());
                    Element profileElem =
                            root.newChildElement("ppp:profile", PPP_NS);
                    GenericPerson owner = profile.getOwner();
                    if (owner == null) {
                        throw new IllegalStateException(
                                "Failed to get owner of profile.");
                    }
                    Element profileOwnerName = profileElem.newChildElement(
                            "ppp:ownerName", PPP_NS);
                    profileOwnerName.setText(owner.getFullName());

                    createNavigation(profile, root, navPath);

                    if (navPath == null) {
                        final PublicPersonalProfileXmlGenerator generator =
                                                                new PublicPersonalProfileXmlGenerator(
                                profile);
                        generator.generateXML(state, root, "");

                        //generateProfileOwnerXml(profileElem, owner, state);                        

                        /*DataCollection relatedLinks = RelatedLink.getRelatedLinks(profile, "NONE");                       
                        if (relatedLinks.size() > 0) {
                        
                        }
                        
                        DataCollection notes = Note.getNotes(profile);
                        if (notes.size() > 0) {
                        
                        } */
                    } else {
                        final DataCollection links =
                                             RelatedLink.getRelatedLinks(profile,
                                                                         PublicPersonalProfile.LINK_LIST_NAME);
                        links.addFilter(String.format("linkTitle = '%s'",
                                                      navPath));

                        if (links.size() == 0) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            return;
                        } else {
                            if (config.getShowPersonInfoEverywhere()) {
                                generateProfileOwnerXml(profileElem, owner,
                                                        state);
                            }

                            PublicPersonalProfileNavItemCollection navItems =
                                                                   new PublicPersonalProfileNavItemCollection();
                            navItems.addLanguageFilter(DispatcherHelper.
                                    getNegotiatedLocale().
                                    getLanguage());
                            navItems.addKeyFilter(navPath);
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

                                        generator.generateContent(root, owner);

                                    } else {
                                        throw new ServerException(String.format(
                                                "Class '%s' is not a ContentGenerator.",
                                                navItems.getNavItem().
                                                getGeneratorClass()));
                                    }

                                } catch (InstantiationException ex) {
                                    throw new ServletException(
                                            "Failed to create generator", ex);
                                } catch (IllegalAccessException ex) {
                                    throw new ServletException(
                                            "Failed to create generator", ex);
                                } catch (IllegalArgumentException ex) {
                                    throw new ServletException(
                                            "Failed to create generator", ex);
                                } catch (InvocationTargetException ex) {
                                    throw new ServletException(
                                            "Failed to create generator", ex);
                                } catch (ClassNotFoundException ex) {
                                    throw new ServletException(
                                            "Failed to create generator", ex);
                                } catch (NoSuchMethodException ex) {
                                    throw new ServletException(
                                            "Failed to create generator", ex);
                                }
                            } else {

                                links.next();
                                final RelatedLink link =
                                                  (RelatedLink) DomainObjectFactory.
                                        newInstance(links.getDataObject());
                                final ContentItem item = link.getTargetItem();
                                final PublicPersonalProfileXmlGenerator generator =
                                                                        new PublicPersonalProfileXmlGenerator(
                                        item);
                                generator.generateXML(state,
                                                      root,
                                                      "");
                            }

                            navItems.close();
                        }
                    }
                }

                PresentationManager presentationManager = Templating.
                        getPresentationManager();
                presentationManager.servePage(document, request, response);
            }

        }

    }

    private void createNavigation(final PublicPersonalProfile profile,
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

        }
    }

    private void generateProfileOwnerXml(final Element profileElem,
                                         final GenericPerson owner,
                                         final PageState state) {
        Element profileOwnerElem = profileElem.newChildElement(
                "profileOwner");
        if ((owner.getSurname() != null)
            && !owner.getSurname().trim().isEmpty()) {
            Element surname =
                    profileOwnerElem.newChildElement("surname");
            surname.setText(owner.getSurname());
        }
        if ((owner.getGivenName() != null)
            && !owner.getGivenName().trim().isEmpty()) {
            Element givenName = profileOwnerElem.newChildElement(
                    "givenName");
            givenName.setText(owner.getGivenName());
        }
        if ((owner.getTitlePre() != null)
            && !owner.getTitlePre().trim().isEmpty()) {
            Element titlePre = profileOwnerElem.newChildElement("titlePre");
            titlePre.setText(owner.getTitlePre());
        }
        if ((owner.getTitlePost() != null)
            && !owner.getTitlePost().trim().isEmpty()) {
            Element titlePost = profileOwnerElem.newChildElement(
                    "titlePost");
            titlePost.setText(owner.getTitlePost());
        }

        if (owner.hasContacts()) {
            final GenericPersonContactCollection contacts = owner.getContacts();
            //final String contactType = config.getContactType();

            /*contacts.addFilter(String.format("link.link_key = '%s'",
            contactType));*/

            if (contacts.size() > 0) {
                contacts.next();
                generateContactXml(profileOwnerElem,
                                   contacts.getContact(),
                                   state);
            }
        }

        DataCollection imgAttachments = (DataCollection) owner.get(
                "imageAttachments");
        if (imgAttachments.size() > 0) {
            imgAttachments.next();
            final DataObject imgAttachment = imgAttachments.getDataObject();
            final DataObject image = (DataObject) imgAttachment.get("image");

            final BigDecimal imageId = (BigDecimal) image.get("id");

            Element imageElem = profileOwnerElem.newChildElement("image");
            imageElem.addAttribute("id", imageId.toString());
        }
    }

    private void generateContactXml(final Element profileOwnerElem,
                                    final GenericContact contact,
                                    final PageState state) {
        final Element contactElem = profileOwnerElem.newChildElement("contact");
        final Element entriesElem = contactElem.newChildElement("entries");

        final GenericContactEntryCollection entries =
                                            contact.getContactEntries();
        Element entryElem;
        GenericContactEntry entry;
        while (entries.next()) {
            entry = entries.getContactEntry();

            entryElem = entriesElem.newChildElement("entry");
            entryElem.addAttribute("key", entry.getKey());
            entryElem.setText(entry.getValue());
        }

        if (contact.hasAddress()) {
            final Element addressElem = contactElem.newChildElement("address");
            final GenericAddress address = contact.getAddress();

            final Element addressTxtElem = addressElem.newChildElement(
                    "addressTxt");
            addressTxtElem.setText(address.getAddress());

            final Element postalCodeElem = addressElem.newChildElement(
                    "postalCode");
            postalCodeElem.setText(address.getPostalCode());

            final Element cityElem = addressElem.newChildElement("city");
            cityElem.setText(address.getCity());

            final Element stateElem = addressElem.newChildElement("state");
            stateElem.setText(address.getState());

            final Element isoCodeElem = addressElem.newChildElement(
                    "isoCountryCode");
            isoCodeElem.setText(address.getIsoCountryCode());
        }
    }

    private void showAdminPage(final Page page,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ServletException {

        page.addRequestListener(new ApplicationAuthenticationListener());

        final Form form = new Form("PublicPersonalProfileAdmin");

        //form.add(new Label("Admin"));

        //final Label label = new Label("for PublicPersonalProfile");

        //form.add(label);

        page.setClassAttr("adminPage");

        final StringParameter navItemKeyParam = new StringParameter(
                "selectedNavItem");
        final ParameterSingleSelectionModel navItemSelect =
                                            new ParameterSingleSelectionModel(
                navItemKeyParam);

        page.addGlobalStateParam(navItemKeyParam);

        final BoxPanel box = new BoxPanel(BoxPanel.VERTICAL);
        final FormSection tableSection = new FormSection(box);

        final PublicPersonalProfileNavItemsAddForm addForm =
                                                   new PublicPersonalProfileNavItemsAddForm(
                navItemSelect);
        final PublicPersonalProfileNavItemsTable table =
                                                 new PublicPersonalProfileNavItemsTable(
                navItemSelect);

        box.add(table);
        form.add(tableSection);

        box.add(addForm);

        page.add(form);
        page.lock();

        final Document document = page.buildDocument(request, response);

        final PresentationManager presentationManager = Templating.
                getPresentationManager();
        presentationManager.servePage(document, request, response);

    }
}
