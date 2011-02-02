package com.arsdigita.cms.scipublications;

import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciPublicationsServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -632365939651657874L;
    private static final Logger logger = Logger.getLogger(
            SciPublicationsServlet.class);

    @Override
    protected void doService(HttpServletRequest request,
                             HttpServletResponse response,
                             Application app) throws ServletException,
                                                     IOException {
        String path = "";

        logger.debug("SciPublicationsServlet is starting...");
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

        if (path.isEmpty()) {
            logger.debug("pathInfo is null, responding with default...");
            /*response.setContentType("application/text");
            response.setHeader("Content-Disposition",
                               "attachment; filename=ccm-publication-exporter.txt");
            response.getWriter().append("This is the sci-publication-exporter");*/

            response.setContentType("text/plain");
            response.getWriter().append("Please choose an application.");
            
            //ToDo: Show a menu?
        } else if("export".equals(path)) {
            logger.debug("Export a publication");

            response.setContentType("text/plain");
            response.getWriter().append("Calling exporter...");

            //ToDo: Call the exporter here.
        } else {
            logger.debug("Unknown pathinfo, responding with 404...");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
