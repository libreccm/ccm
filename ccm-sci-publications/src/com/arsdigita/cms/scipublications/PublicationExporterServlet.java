package com.arsdigita.cms.scipublications;

import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationExporterServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -632365939651657874L;

    @Override
    protected void doService(HttpServletRequest request,
                             HttpServletResponse response,
                             Application app) throws ServletException,
                                                     IOException {
        response.setContentType("application/text");
        response.setHeader("Content-Disposition", "attachment; filename=ccm-publication-exporter.txt");
        response.getWriter().append("This is the sci-publication-exporter");
    }
}
