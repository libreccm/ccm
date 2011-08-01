package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;
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
public class PublicPersonalProfileServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -1495852395804455609L;
    private static final Logger logger =
                                Logger.getLogger(
            PublicPersonalProfileServlet.class);

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
            Page page;
            Form form;
            Label label;

            page = PageFactory.buildPage("SciPublications",
                                         "Hello World from profiles");
            form = new Form("HelloWorld");
            label = new Label(String.format("Hello World! From profiles, path = %s", path));

            form.add(label);
            page.add(form);

            page.lock();

            Document document = page.buildDocument(request, response);
            PresentationManager presentationManager = Templating.
                    getPresentationManager();
            presentationManager.servePage(document, request, response);
        }

    }
}
