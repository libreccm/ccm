package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.IOException;
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
    private static final String PREVIEW = "preview";

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
            boolean preview = false;;
            String profileOwner = "";
            String navPath = null;

            Page page;
            /*Form form;
            Label label;*/

            page = PageFactory.buildPage("PublicPersonalProfile",
                                         "Hello World from profiles");
            /*form = new Form("HelloWorld");*/

            if (pathTokens.length < 1) {
                //ToDo: Fehlerbehandlung?
            } else {
                if ((pathTokens.length > 1)
                    && PREVIEW.equals(pathTokens[0])) {
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

            /*form.add(new Label(String.format("Member: %s", member)));
            
            if (pathTokens.length > 1) {
            for(int i = 1; i < pathTokens.length; i++) {
            form.add(new Label(String.format("%d: %s", i, pathTokens[i])));
            }
            }
            
            label = new Label(String.format(
            "Hello World! From profiles, path = %s", path));
            
            form.add(label);
            page.add(form);*/

            page.lock();

            Document document = page.buildDocument(request, response);
            Element root = document.getRootElement();
            Element test = root.newChildElement("test");
            test.setText("test");

            final Session session = SessionManager.getSession();

            DataCollection profiles =
                           session.retrieve(
                    com.arsdigita.cms.contenttypes.PublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
            profiles.addFilter(String.format("profileUrl = '%s'", profileOwner));
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
                profiles.next();
                PublicPersonalProfile profile =
                                      (PublicPersonalProfile) DomainObjectFactory.
                        newInstance(profiles.getDataObject());
                Element profileElem = root.newChildElement("profile");
                GenericPerson owner = profile.getOwner();
                if (owner == null) {
                    throw new IllegalStateException(
                            "Failed to get owner of profile.");
                }
                profileElem.setText(owner.getFullName());

                createNavigation(profile, root);
            }




            PresentationManager presentationManager = Templating.
                    getPresentationManager();
            presentationManager.servePage(document, request, response);
        }

    }

    private void createNavigation(final PublicPersonalProfile profile,
                                  final Element root) {
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
        navHome.addAttribute("isSelected", "true");
        navHome.addAttribute("sortKey", "");
        navHome.addAttribute("title", "Allgemein");
        navHome.addAttribute("url", String.format("/ccm/%s",
                                                  profile.getProfileUrl()));

    }
}
