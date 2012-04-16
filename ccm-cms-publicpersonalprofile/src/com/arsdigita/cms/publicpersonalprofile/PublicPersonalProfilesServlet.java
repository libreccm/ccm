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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericContactEntryCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItemCollection;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileXmlUtil;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.publicpersonalprofile.ui.PublicPersonalProfileNavItemsAddForm;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.toolbox.ui.ApplicationAuthenticationListener;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Servlet for the PublicPersonalProfile application.
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
    private final PublicPersonalProfileConfig config =
                                              PublicPersonalProfiles.getConfig();

    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Application app) throws ServletException,
                                                           IOException {

        logger.debug("PublicPersonalProfileServlet is starting...");
        logger.debug(String.format("pathInfo = '%s'", request.getPathInfo()));

        logger.debug("Extracting path from pathInfo by removing leading and "
                     + "trailing slashes...");

        final String pathStr = getPath(request);

        logger.debug(String.format("path = %s", pathStr));

        //Displays a text/plain page with a message.
        if (pathStr.isEmpty()) {
            logger.debug("pathInfo is null, responding with default...");

            response.setContentType("text/plain");
            response.getWriter().append("Please choose an application.");
        } else {
            final Path path;
            try {
                path = new Path(pathStr);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                   ex.getMessage());
                return;
            }

            final Page page = PageFactory.buildPage("PublicPersonalProfile",
                                                    "");

            if (path.getAdmin()) {
                showAdminPage(page, request, response);
                return;
            }

            page.lock();

            final Document document = page.buildDocument(request, response);
            final Element root = document.getRootElement();

            final Session session = SessionManager.getSession();

            /*final DataCollection profiles = getProfiles(session,
             path.getProfileOwner(),
             path.getPreview(),
             GlobalizationHelper.
             getNegotiatedLocale().getLanguage());
            
             if (profiles.isEmpty()) {
             response.sendError(HttpServletResponse.SC_NOT_FOUND);
             return;
             } else if (profiles.size() > 1) {
             throw new IllegalStateException(
             "More than one matching members found.");
             }
            
             profiles.next();
             PublicPersonalProfile profile =
             (PublicPersonalProfile) DomainObjectFactory.
             newInstance(profiles.getDataObject());
             profiles.close();*/

            PublicPersonalProfile profile = getProfile(
                    session,
                    path.getProfileOwner(),
                    path.getPreview(),
                    GlobalizationHelper.getNegotiatedLocale().getLanguage());

            if (profile == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (path.getNavPath() != null) {
                final DataCollection links = RelatedLink.getRelatedLinks(
                        profile,
                        PublicPersonalProfile.LINK_LIST_NAME);
                links.addFilter(String.format("linkTitle = '%s'",
                                              path.getNavPath()));
                if (links.isEmpty()) {
                    profile = getProfile(session,
                                         path.getProfileOwner(),
                                         path.getPreview(),
                                         GlobalizationHelper.LANG_INDEPENDENT,
                                         false);
                } else {
                    links.next();
                    final RelatedLink link = (RelatedLink) DomainObjectFactory.
                            newInstance(
                            links.getDataObject());
                    links.close();

                    ContentItem item = link.getTargetItem();

                    if ((item instanceof ContentPage)
                        && !(item instanceof PublicPersonalProfile)) {
                        ContentPage contentPage =
                                    (ContentPage) item;

                        if (contentPage.getContentBundle().hasInstance(profile.
                                getLanguage(),
                                                                       false)) {
                            contentPage =
                            (ContentPage) contentPage.getContentBundle().
                                    getInstance(profile.getLanguage());
                            item = (ContentItem) contentPage;
                        } else {
                            profile =
                            getProfile(session,
                                       path.getProfileOwner(),
                                       path.getPreview(),
                                       GlobalizationHelper.LANG_INDEPENDENT);
                        }
                    }
                }
            }

            final PageState state = new PageState(page,
                                                  request,
                                                  response);

            if (path.getPreview()) {
                if (Kernel.getContext().getParty() == null) {
                    throw new LoginSignal(request);
                } else {

                    com.arsdigita.cms.SecurityManager securityManager =
                                                      Utilities.
                            getSecurityManager(state);

                    final boolean canEdit = securityManager.canAccess(
                            state.getRequest(),
                            com.arsdigita.cms.SecurityManager.PREVIEW_PAGES,
                            profile);

                    if (!canEdit) {
                        throw new AccessDeniedException("user "
                                                        + Kernel.getContext().
                                getParty().getOID()
                                                        + " doesn't have the "
                                                        + com.arsdigita.cms.SecurityManager.EDIT_ITEM
                                                        + " privilege on "
                                                        + profile.getOID().
                                toString());
                    }
                }
            }

            if (config.getEmbedded()) {
                final ContentSection section =
                                     profile.getContentSection();
                final ItemResolver resolver = section.getItemResolver();

                String context;
                if (path.getPreview()) {
                    context = CMSDispatcher.PREVIEW;
                } else {
                    context = ContentItem.LIVE;
                }

                final String url = String.format("/ccm%s", resolver.
                        generateItemURL(state,
                                        profile,
                                        section,
                                        context));

                throw new RedirectSignal(url, false);
            }

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
            //Add an attribute with the lang of the owner item of debugging.
            profileOwnerName.addAttribute("ownerItemLang", owner.getLanguage());

            final DataCollection images = ItemImageAttachment.
                    getImageAttachments(profile);
            if (!images.isEmpty()) {
                images.next();
                final Element profileImageElem =
                              profileElem.newChildElement("ppp:profileImage",
                                                          PPP_NS);
                final Element attachmentElem = profileImageElem.newChildElement(
                        "imageAttachments");
                final ItemImageAttachment attachment =
                                          new ItemImageAttachment(images.
                        getDataObject());
                attachmentElem.addAttribute("oid", attachment.getOID().
                        toString());
                final Element caption = attachmentElem.newChildElement(
                        "caption");
                caption.setText(attachment.getCaption());
                final ReusableImageAsset image = attachment.getImage();
                final Element imageElem =
                              attachmentElem.newChildElement("image");
                imageElem.addAttribute("oid", image.getOID().toString());
                final Element widthElem = imageElem.newChildElement(
                        "width");
                widthElem.setText(image.getWidth().toString());
                final Element heightElem = imageElem.newChildElement(
                        "height");
                heightElem.setText(image.getHeight().toString());
                final Element descElem = imageElem.newChildElement(
                        "description");
                descElem.setText(image.getDescription());
                final Element nameElem = imageElem.newChildElement(
                        "name");
                nameElem.setText(image.getName());
                final Element idElem = imageElem.newChildElement("id");
                idElem.setText(image.getID().toString());
                final Element displayNameElem = imageElem.newChildElement(
                        "displayName");
                displayNameElem.setText(image.getDisplayName());

                images.close();
            }

            final PublicPersonalProfileXmlUtil util =
                                               new PublicPersonalProfileXmlUtil();
            String prefix =
                   DispatcherHelper.getDispatcherPrefix(request);
            if (prefix == null) {
                prefix = "";
            }
            util.createNavigation(profile,
                                  root,
                                  path.getNavPath(),
                                  prefix,
                                  app.getPath(),
                                  path.getPreview());

            if (path.getNavPath() == null) {
                final PublicPersonalProfileXmlGenerator generator =
                                                        new PublicPersonalProfileXmlGenerator(
                        profile);
                generator.generateXML(state, root, "");
            } else {
                if (path.getItemPath() == null) {
                    showNavItem(response, profile, path, root,
                                profileElem, state);
                } else {
                    showItem(response, profile, path, root,
                             profileElem, state);
                }
            }

            PresentationManager presentationManager = Templating.
                    getPresentationManager();
            presentationManager.servePage(document, request, response);
        }
    }

    //@Override
    protected void olddoService(final HttpServletRequest request,
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
            String itemPath = null;

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
                        if (pathTokens.length > 3) {
                            itemPath = pathTokens[3];
                        }
                    } else {
                        profileOwner = pathTokens[0];
                        if (pathTokens.length > 1) {
                            navPath = pathTokens[1];
                        }
                        if (pathTokens.length > 2) {
                            itemPath = pathTokens[2];
                        }
                    }
                }

                page.lock();

                Document document = page.buildDocument(request, response);
                Element root = document.getRootElement();

                final Session session = SessionManager.getSession();

                /*DataCollection profiles =
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
                 }*/

                DataCollection profiles = getProfiles(session,
                                                      profileOwner,
                                                      preview,
                                                      GlobalizationHelper.
                        getNegotiatedLocale().getLanguage(),
                                                      Kernel.getConfig().
                        languageIndependentItems());

                /*if (profiles.isEmpty()) {
                 profiles = getProfiles(session,
                 profileOwner,
                 preview,
                 GlobalizationHelper.LANG_INDEPENDENT);
                 }*/

                if (profiles.size() == 0) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
                    profiles.close();

                    if (preview) {
                        if (Kernel.getContext().getParty() == null) {
                            throw new LoginSignal(request);
                        } else {

                            com.arsdigita.cms.SecurityManager securityManager =
                                                              Utilities.
                                    getSecurityManager(state);

                            final boolean canEdit = securityManager.canAccess(
                                    state.getRequest(),
                                    com.arsdigita.cms.SecurityManager.PREVIEW_PAGES,
                                    profile);

                            if (!canEdit) {
                                throw new AccessDeniedException("user "
                                                                + Kernel.
                                        getContext().getParty().getOID()
                                                                + " doesn't have the "
                                                                + com.arsdigita.cms.SecurityManager.EDIT_ITEM
                                                                + " privilege on "
                                                                + profile.getOID().
                                        toString());
                            }
                        }
                    }

                    if (config.getEmbedded()) {
                        final ContentSection section =
                                             profile.getContentSection();
                        final ItemResolver resolver = section.getItemResolver();

                        String context;
                        if (preview) {
                            context = CMSDispatcher.PREVIEW;
                        } else {
                            context = ContentItem.LIVE;
                        }

                        final String url = String.format("/ccm%s", resolver.
                                generateItemURL(state,
                                                profile,
                                                section,
                                                context));

                        throw new RedirectSignal(url, false);
                    }

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

                    final DataCollection images = ItemImageAttachment.
                            getImageAttachments(profile);
                    if (!images.isEmpty()) {
                        images.next();
                        final Element profileImageElem = profileElem.
                                newChildElement("ppp:profileImage",
                                                PPP_NS);

                        final Element attachmentElem = profileImageElem.
                                newChildElement("imageAttachments");
                        final ItemImageAttachment attachment =
                                                  new ItemImageAttachment(images.
                                getDataObject());
                        attachmentElem.addAttribute("oid", attachment.getOID().
                                toString());
                        final Element caption = attachmentElem.newChildElement(
                                "caption");
                        caption.setText(attachment.getCaption());
                        final ReusableImageAsset image = attachment.getImage();
                        final Element imageElem =
                                      attachmentElem.newChildElement("image");
                        imageElem.addAttribute("oid", image.getOID().toString());
                        final Element widthElem = imageElem.newChildElement(
                                "width");
                        widthElem.setText(image.getWidth().toString());
                        final Element heightElem = imageElem.newChildElement(
                                "height");
                        heightElem.setText(image.getHeight().toString());
                        final Element descElem = imageElem.newChildElement(
                                "description");
                        descElem.setText(image.getDescription());
                        final Element nameElem = imageElem.newChildElement(
                                "name");
                        nameElem.setText(image.getName());
                        final Element idElem = imageElem.newChildElement("id");
                        idElem.setText(image.getID().toString());
                        final Element displayNameElem = imageElem.
                                newChildElement("displayName");
                        displayNameElem.setText(image.getDisplayName());

                        images.close();
                    }

                    final PublicPersonalProfileXmlUtil util =
                                                       new PublicPersonalProfileXmlUtil();
                    String prefix =
                           DispatcherHelper.getDispatcherPrefix(request);
                    if (prefix == null) {
                        prefix = "";
                    }
                    util.createNavigation(profile,
                                          root,
                                          navPath,
                                          prefix,
                                          app.getPath(),
                                          preview);

                    if (navPath == null) {
                        final PublicPersonalProfileXmlGenerator generator =
                                                                new PublicPersonalProfileXmlGenerator(
                                profile);
                        generator.generateXML(state, root, "");

                    } else {
                        if (itemPath == null) {
                            final DataCollection links =
                                                 RelatedLink.getRelatedLinks(
                                    profile,
                                    PublicPersonalProfile.LINK_LIST_NAME);
                            links.addFilter(String.format("linkTitle = '%s'",
                                                          navPath));

                            if (links.size() == 0) {
                                response.sendError(
                                        HttpServletResponse.SC_NOT_FOUND);
                                return;
                            } else {
                                if (config.getShowPersonInfoEverywhere()) {
                                    generateProfileOwnerXml(profileElem, owner,
                                                            state);
                                }

                                PublicPersonalProfileNavItemCollection navItems =
                                                                       new PublicPersonalProfileNavItemCollection();
                                navItems.addLanguageFilter(GlobalizationHelper.
                                        getNegotiatedLocale().
                                        getLanguage());
                                navItems.addKeyFilter(navPath);
                                navItems.next();

                                if (navItems.getNavItem().getGeneratorClass()
                                    != null) {
                                    try {
                                        Object generatorObj =
                                               Class.forName(navItems.getNavItem().
                                                getGeneratorClass()).
                                                getConstructor().
                                                newInstance();

                                        if (generatorObj instanceof ContentGenerator) {
                                            final ContentGenerator generator =
                                                                   (ContentGenerator) generatorObj;

                                            generator.generateContent(
                                                    profileElem,
                                                    owner,
                                                    state,
                                                    profile.getLanguage());

                                        } else {
                                            throw new ServletException(String.
                                                    format(
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
                                    links.close();
                                    ContentItem item =
                                                link.getTargetItem();

                                    if (item instanceof ContentPage) {
                                        ContentPage contentPage =
                                                    (ContentPage) item;
                                        logger.error("contentPage.getContentBundle().hasInstance(GlobalizationHelper.getNegotiatedLocale().getLanguage()) = "
                                                     + contentPage.
                                                getContentBundle().
                                                hasInstance(GlobalizationHelper.
                                                getNegotiatedLocale().
                                                getLanguage()));
                                        if (contentPage.getContentBundle().
                                                hasInstance(GlobalizationHelper.
                                                getNegotiatedLocale().
                                                getLanguage())) {
                                            contentPage =
                                            (ContentPage) contentPage.
                                                    getContentBundle().
                                                    getInstance(GlobalizationHelper.
                                                    getNegotiatedLocale().
                                                    getLanguage());
                                            item = (ContentItem) contentPage;
                                        } else {
                                            logger.error(
                                                    String.format(
                                                    "Item '%s' not found in a suitable language variant. Negotiated langauge: %s, langugage independent items allowed is %s, language independent code is %s ",
                                                    itemPath,
                                                    GlobalizationHelper.
                                                    getNegotiatedLocale().
                                                    getLanguage(),
                                                    Kernel.getConfig().
                                                    languageIndependentItems(),
                                                    GlobalizationHelper.LANG_INDEPENDENT));
                                            response.sendError(
                                                    HttpServletResponse.SC_NOT_FOUND);
                                            return;
                                        }
                                    }


                                    final Element contentPanelElem = root.
                                            newChildElement("cms:contentPanel",
                                                            CMS.CMS_XML_NS);
                                    final PublicPersonalProfileXmlGenerator generator =
                                                                            new PublicPersonalProfileXmlGenerator(
                                            item);
                                    generator.generateXML(state,
                                                          contentPanelElem,
                                                          "");
                                }

                                navItems.close();
                            }
                        } else {
                            if (config.getShowPersonInfoEverywhere()) {
                                generateProfileOwnerXml(profileElem, owner,
                                                        state);
                            }

                            final OID itemOid = OID.valueOf(itemPath);
                            try {
                                ContentItem item =
                                            (ContentItem) DomainObjectFactory.
                                        newInstance(itemOid);

                                if (item instanceof ContentPage) {
                                    ContentPage contentPage = (ContentPage) item;
                                    logger.error("contentPage.getContentBundle().hasInstance(GlobalizationHelper.getNegotiatedLocale().getLanguage()) = "
                                                 + contentPage.getContentBundle().
                                            hasInstance(GlobalizationHelper.
                                            getNegotiatedLocale().getLanguage()));
                                    if (contentPage.getContentBundle().
                                            hasInstance(GlobalizationHelper.
                                            getNegotiatedLocale().getLanguage())) {
                                        contentPage = (ContentPage) contentPage.
                                                getContentBundle().getInstance(GlobalizationHelper.
                                                getNegotiatedLocale().
                                                getLanguage());
                                        item = (ContentItem) contentPage;
                                    } else {
                                        logger.error(
                                                String.format(
                                                "Item '%s' not found in a suitable language variant. Negotiated langauge: %s, langugage independent items allowed is %s, language independent code is %s ",
                                                itemPath,
                                                GlobalizationHelper.
                                                getNegotiatedLocale().
                                                getLanguage(),
                                                Kernel.getConfig().
                                                languageIndependentItems(),
                                                GlobalizationHelper.LANG_INDEPENDENT));
                                        response.sendError(
                                                HttpServletResponse.SC_NOT_FOUND);
                                        return;
                                    }
                                }


                                final Element contentPanelElem = root.
                                        newChildElement("cms:contentPanel",
                                                        CMS.CMS_XML_NS);

                                final PublicPersonalProfileXmlGenerator generator =
                                                                        new PublicPersonalProfileXmlGenerator(
                                        item);
                                generator.generateXML(state,
                                                      contentPanelElem,
                                                      "");

                            } catch (DataObjectNotFoundException ex) {
                                logger.error(String.format(
                                        "Item '%s' not found: ",
                                        itemPath),
                                             ex);
                                response.sendError(
                                        HttpServletResponse.SC_NOT_FOUND);
                                return;
                            }

                        }
                    }
                }

                PresentationManager presentationManager = Templating.
                        getPresentationManager();
                presentationManager.servePage(document, request, response);
            }

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

    /**
     * Generates the contact XML for the person
     * 
     * @param profileOwnerElem
     * @param contact
     * @param state 
     */
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

    /**
     * Renders the admin page.
     * 
     * @param page
     * @param request
     * @param response
     * @throws ServletException 
     */
    private void showAdminPage(final Page page,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ServletException {

        page.addRequestListener(new ApplicationAuthenticationListener());

        final Form form = new Form("PublicPersonalProfileAdmin");

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

    private void showNavItem(final HttpServletResponse response,
                             final PublicPersonalProfile profile,
                             final Path path,
                             final Element root,
                             final Element profileElem,
                             final PageState state) throws IOException,
                                                           ServletException {
        final DataCollection links =
                             RelatedLink.getRelatedLinks(
                profile,
                PublicPersonalProfile.LINK_LIST_NAME);
        links.addFilter(String.format("linkTitle = '%s'",
                                      path.getNavPath()));

        if (links.size() == 0) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (config.getShowPersonInfoEverywhere()) {
            generateProfileOwnerXml(profileElem, profile.getOwner(), state);
        }

        final PublicPersonalProfileNavItemCollection navItems =
                                                     new PublicPersonalProfileNavItemCollection();
        navItems.addLanguageFilter(profile.getLanguage());
        navItems.addKeyFilter(path.getNavPath());
        navItems.next();

        links.next();
        final RelatedLink link = (RelatedLink) DomainObjectFactory.newInstance(
                links.getDataObject());
        links.close();

        ContentItem item = link.getTargetItem();

        if ((item instanceof ContentPage)
            && !(item instanceof PublicPersonalProfile)) {
            ContentPage contentPage =
                        (ContentPage) item;
            /*logger.debug("contentPage.getContentBundle().hasInstance(GlobalizationHelper.getNegotiatedLocale().getLanguage()) = "
             + contentPage.getContentBundle().
             hasInstance(GlobalizationHelper.getNegotiatedLocale().
             getLanguage()));
             if (contentPage.getContentBundle().
             hasInstance(GlobalizationHelper.getNegotiatedLocale().
             getLanguage())) {
             contentPage =
             (ContentPage) contentPage.getContentBundle().
             getInstance(GlobalizationHelper.getNegotiatedLocale().
             getLanguage());
             item = (ContentItem) contentPage;
             } else {
             logger.error(
             String.format(
             "Item '%s' not found in a suitable language variant. Negotiated langauge: %s, langugage independent items allowed is %s, language independent code is %s ",
             path.getNavPath(),
             GlobalizationHelper.getNegotiatedLocale().
             getLanguage(),
             Kernel.getConfig().
             languageIndependentItems(),
             GlobalizationHelper.LANG_INDEPENDENT));
             response.sendError(
             HttpServletResponse.SC_NOT_FOUND);
             return;
             }*/
            if (contentPage.getContentBundle().hasInstance(profile.getLanguage(),
                                                           false)) {
                contentPage =
                (ContentPage) contentPage.getContentBundle().
                        getInstance(profile.getLanguage());
                item = (ContentItem) contentPage;
            } else {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND);
                return;
            }


            final Element contentPanelElem =
                          root.newChildElement("cms:contentPanel",
                                               CMS.CMS_XML_NS);
            final PublicPersonalProfileXmlGenerator generator =
                                                    new PublicPersonalProfileXmlGenerator(
                    item);
            generator.generateXML(state,
                                  contentPanelElem,
                                  "");
        }

        if (navItems.getNavItem().getGeneratorClass() != null) {
            try {
                Object generatorObj =
                       Class.forName(navItems.getNavItem().
                        getGeneratorClass()).
                        getConstructor().
                        newInstance();

                if (generatorObj instanceof ContentGenerator) {
                    final ContentGenerator generator =
                                           (ContentGenerator) generatorObj;

                    generator.generateContent(profileElem,
                                              profile.getOwner(),
                                              state,
                                              profile.getLanguage());

                } else {
                    throw new ServletException(String.format(
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
        }

        navItems.close();
    }

    private void showItem(final HttpServletResponse response,
                          final PublicPersonalProfile profile,
                          final Path path,
                          final Element root,
                          final Element profileElem,
                          final PageState state) throws IOException {
        if (config.getShowPersonInfoEverywhere()) {
            generateProfileOwnerXml(profileElem, profile.getOwner(), state);
        }

        final OID itemOid = OID.valueOf(path.getItemPath());

        try {
            ContentItem item =
                        (ContentItem) DomainObjectFactory.newInstance(
                    itemOid);

            if (item instanceof ContentPage) {
                ContentPage contentPage = (ContentPage) item;
                /*logger.debug("contentPage.getContentBundle().hasInstance(GlobalizationHelper.getNegotiatedLocale().getLanguage()) = "
                 + contentPage.getContentBundle().
                 hasInstance(GlobalizationHelper.getNegotiatedLocale().
                 getLanguage()));
                 if (contentPage.getContentBundle().
                 hasInstance(GlobalizationHelper.getNegotiatedLocale().
                 getLanguage())) {
                 contentPage = (ContentPage) contentPage.getContentBundle().
                 getInstance(GlobalizationHelper.getNegotiatedLocale().
                 getLanguage());
                 item = (ContentItem) contentPage;
                 } else {
                 logger.error(
                 String.format(
                 "Item '%s' not found in a suitable language variant. Negotiated langauge: %s, langugage independent items allowed is %s, language independent code is %s ",
                 path.getItemPath(),
                 GlobalizationHelper.getNegotiatedLocale().
                 getLanguage(),
                 Kernel.getConfig().
                 languageIndependentItems(),
                 GlobalizationHelper.LANG_INDEPENDENT));
                 response.sendError(
                 HttpServletResponse.SC_NOT_FOUND);
                 return;
                 }*/
                if (contentPage.getContentBundle().hasInstance(profile.
                        getLanguage(), false)) {
                    item = (ContentPage) contentPage.getContentBundle().
                            getInstance(profile.getLanguage());
                } else {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }


            final Element contentPanelElem =
                          root.newChildElement("cms:contentPanel",
                                               CMS.CMS_XML_NS);

            final PublicPersonalProfileXmlGenerator generator =
                                                    new PublicPersonalProfileXmlGenerator(
                    item);
            generator.generateXML(state,
                                  contentPanelElem,
                                  "");
        } catch (DataObjectNotFoundException ex) {
            logger.error(String.format(
                    "Item '%s' not found: ",
                    path.getItemPath()),
                         ex);
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    private DataCollection getProfiles(final Session session,
                                       final String profileOwner,
                                       final boolean preview,
                                       final String language,
                                       final boolean allowLangIndependent) {
        final DataCollection profiles =
                             session.retrieve(
                com.arsdigita.cms.contenttypes.PublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
                
        final FilterFactory filterFactory = profiles.getFilterFactory();                
        final Filter urlFilter = filterFactory.simple(String.format(
                "profileUrl = '%s'",
                profileOwner));
        final Filter versionFilter;
        if (preview) {
            versionFilter = filterFactory.simple(String.format("version = '%s'",
                                                             ContentItem.DRAFT));
        } else {
            versionFilter = filterFactory.simple(String.format("version = '%s'",
                                                             ContentItem.LIVE));
        }

        final Filter langFilter = filterFactory.simple(String.format(
                "language = '%s'", language));
        
        profiles.addFilter(urlFilter);
        profiles.addFilter(versionFilter);
        profiles.addFilter(langFilter);
        
        if (profiles.isEmpty()) {
            profiles.reset();
            profiles.addFilter(urlFilter);
            profiles.addFilter(versionFilter);
            profiles.addFilter(String.format("language = '%s'", 
                                             GlobalizationHelper.LANG_INDEPENDENT));
        }

//        if (allowLangIndependent) {
//            FilterFactory ff = profiles.getFilterFactory();
//            Filter filter = ff.or().
//                    addFilter(ff.equals("language", language)).
//                    addFilter(ff.and().
//                    addFilter(ff.equals("language",
//                                        GlobalizationHelper.LANG_INDEPENDENT)).
//                    addFilter(ff.notIn("parent",
//                                       "com.arsdigita.navigation.getParentIDsOfMatchedItems").
//                    set("language", language)));
//            profiles.addFilter(filter);
//        } else {
//            profiles.addFilter(String.format("language = '%s'", language));
//        }

        return profiles;
    }

    private PublicPersonalProfile getProfile(final Session session,
                                             final String profileOwner,
                                             final boolean preview,
                                             final String language) {
        return getProfile(session,
                          profileOwner,
                          preview,
                          language,
                          Kernel.getConfig().languageIndependentItems());
    }

    private PublicPersonalProfile getProfile(final Session session,
                                             final String profileOwner,
                                             final boolean preview,
                                             final String language,
                                             final boolean allowLangIndependent) {
        final DataCollection profiles = getProfiles(session,
                                                    profileOwner,
                                                    preview,
                                                    language,
                                                    allowLangIndependent);

        if (profiles.isEmpty()) {
            return null;
        } else if (profiles.size() > 1) {
            throw new IllegalStateException(
                    "More than one matching members found.");
        } else {
            profiles.next();
            PublicPersonalProfile profile =
                                  (PublicPersonalProfile) DomainObjectFactory.
                    newInstance(profiles.getDataObject());
            profiles.close();

            return profile;
        }
    }

    private String getPath(final HttpServletRequest request) {
        String path = "";

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

        return path;
    }

    private class Path {

        private final boolean admin;
        private final boolean preview;
        private final String profileOwner;
        private final String navPath;
        private final String itemPath;

        public Path(final String path) {
            final String[] pathTokens = path.split("/");
            final int ownerTokenPos;

            if (pathTokens.length < 1) {
                throw new IllegalArgumentException(
                        "Illegal path. Missing profile owner.");
            } else {
                admin = ADMIN.equals(pathTokens[0]);

                preview = PREVIEW.equals(pathTokens[0]);

                if (preview) {
                    ownerTokenPos = 1;
                } else {
                    ownerTokenPos = 0;
                }

                if (pathTokens.length < (ownerTokenPos + 1)) {
                    throw new IllegalArgumentException(
                            "Illegal path. Missing profile owner.");
                } else {
                    profileOwner = pathTokens[ownerTokenPos];
                }

                if (pathTokens.length > (ownerTokenPos + 1)) {
                    navPath = pathTokens[ownerTokenPos + 1];
                } else {
                    navPath = null;
                }

                if (pathTokens.length > (ownerTokenPos + 2)) {
                    itemPath = pathTokens[ownerTokenPos + 2];
                } else {
                    itemPath = null;
                }
            }
        }

        public boolean getAdmin() {
            return admin;
        }

        public boolean getPreview() {
            return preview;
        }

        public String getProfileOwner() {
            return profileOwner;
        }

        public String getNavPath() {
            return navPath;
        }

        public String getItemPath() {
            return itemPath;
        }
    }
}
