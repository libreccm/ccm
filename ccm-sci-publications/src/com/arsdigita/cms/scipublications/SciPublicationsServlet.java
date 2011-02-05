package com.arsdigita.cms.scipublications;

import com.arsdigita.categorization.Categorization;
import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        } else if ("export".equals(path)) {
            logger.debug("Export a publication");

            Map<String, String[]> parameters;
            String format;

            parameters = request.getParameterMap();

            if (parameters.containsKey("format")) {
                if (parameters.get("format").length == 1) {
                    format = parameters.get("format")[0];
                } else {
                    logger.warn("Query parameter 'format' contains no value"
                                + "or more than one value. It is expected that "
                                + "'format' contains excactly one value. Responding"
                                + "with BAD_REQUEST status.");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "Query parameter 'format' contains no value or more"
                                       + "than one value. It is expected that "
                                       + "'format' contains excactly one value.");
                    return;
                }
            } else {
                logger.warn("Missing query parameter 'format'. "
                            + "Responsding with BAD_REQUEST status code.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (parameters.containsKey("category")) {
                Publication publication;
                SciPublicationsExporter exporter;
                String categoryIdStr;
                BigDecimal categoryId;
                Category category;
                CategorizedCollection objects;

                logger.debug("Found parameter 'category'...");
                if (parameters.get("category").length != 1) {
                    logger.error("The parameter 'category' is expected to"
                                 + "have exactly one parameter.");
                    response.sendError(response.SC_BAD_REQUEST,
                                       "The parameter 'category' is expected to"
                                       + "have exactly one pareameter.");
                    return;
                }

                categoryIdStr = parameters.get("category")[0];
                try {
                    categoryId = new BigDecimal(categoryIdStr);
                } catch (NumberFormatException ex) {
                    logger.error("The category id could not be converted to"
                                 + "an BigDecimal value.",
                                 ex);
                    response.sendError(response.SC_BAD_REQUEST,
                                       "The category id could not be converted to"
                                       + "an BigDecimal value.");
                    return;
                }

                try {
                    category = new Category(categoryId);
                } catch (DataObjectNotFoundException ex) {
                    logger.error(String.format("No category with the provided "
                                               + "id '%s' found.",
                                               categoryIdStr),
                                 ex);
                    response.sendError(response.SC_BAD_REQUEST,
                                       String.format("No category with the provided "
                                                     + "id '%s' found.",
                                                     categoryIdStr));
                    return;
                }

                logger.debug(String.format("Category: %s", category.getName()));

                exporter = SciPublicationsExporters.getInstance().
                        getExporterForFormat(
                        format);
                if (exporter == null) {
                    logger.warn(String.format(
                            "The requested export format '%s' is not supported yet.",
                            format));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       String.format(
                            "The requested export format '%s' is not supported yet.",
                            format));
                    return;
                }

                objects = category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
                logger.debug(String.format("Category contains %d objects.",
                                           objects.size()));
                ContentBundle bundle;
                ACSObject object;
                while (objects.next()) {

                    bundle = (ContentBundle) objects.getACSObject();
                    object = bundle.getInstance(bundle.getDefaultLanguage());

                    if (object instanceof Publication) {
                        publication = (Publication) object;
                    } else {
                        logger.debug("Object is not a publication, ignoring it.");
                        continue;
                    }

                    if (!publication.isLiveVersion()) {
                        logger.debug("Object is no a published version, "
                                     + "ignoring it.");
                        continue;
                    }

                    response.getWriter().append(exporter.exportPublication(
                            publication));
                    response.getWriter().append('\n');
                }

                response.setContentType(exporter.getSupportedFormat().
                        getMimeType().getBaseType());
                response.setHeader("Content-Disposition",
                                   String.format("attachment; filename=%s.bib",
                                                 category.getName()));

                return;
            } else if (parameters.containsKey("publication")) {
                String[] publications;
                List<BigDecimal> publicationIds;

                publications = parameters.get("publication");

                if (publications.length < 1) {
                    logger.warn("Parameter 'publications' has no value(s). "
                                + "Responding with status BAD_REQUEST.");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "Parameter 'publication' has no "
                                       + "value(s).");
                    return;
                }

                BigDecimal publicationId;
                publicationIds = new ArrayList<BigDecimal>();
                for (int i = 0; i < publications.length; i++) {
                    try {
                        publicationId = new BigDecimal(publications[i]);
                    } catch (NumberFormatException ex) {
                        logger.warn(String.format(
                                "Can't convert publication id "
                                + "'%s' on index %d to a BigDecimal.",
                                publications[i], i));
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                           String.format(
                                "Can't convert the publication id"
                                + "on index %d to a number.", i));
                        return;
                    }
                    publicationIds.add(publicationId);
                }

                exportPublications(format, publicationIds, response);

            } else {
                logger.warn("Export action needs either a publication id or a "
                            + "term id. Neither was found in the query parameters."
                            + "Responding with BAD_REQUEST status.");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                   "The export action needs either a publication id or "
                                   + "a term id. Neither was found in the query parameters.");
                return;
            }

        } else {
            logger.warn(String.format("Unknown pathinfo '%s', "
                                      + "responding with 404...",
                                      path));
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                               String.format("The path '%s' is not known.", path));
        }
    }

    private void exportPublications(final String format,
                                    final List<BigDecimal> publicationIds,
                                    final HttpServletResponse response)
            throws IOException {
        SciPublicationsExporter exporter;

        exporter = SciPublicationsExporters.getInstance().getExporterForFormat(
                format);


        if (exporter == null) {
            logger.warn(String.format(
                    "The requested export format '%s' is not supported yet.",
                    format));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               String.format(
                    "The requested export format '%s' is not supported yet.",
                    format));


            return;


        }

        response.setContentType(exporter.getSupportedFormat().getMimeType().
                getBaseType());

        Publication publication = null;
        String publicationName = "publication";


        for (BigDecimal publicationId : publicationIds) {
            try {
                publication = new Publication(publicationId);
                logger.debug(String.format("OID of publication: %s",
                                           publication.getOID()));
                publication = (Publication) DomainObjectFactory.newInstance(publication.
                        getOID());


            } catch (DataObjectNotFoundException ex) {
                logger.warn(String.format("No publication found for id '%s'.",
                                          publicationId.toPlainString()), ex);
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                   String.format(
                        "No publication found for id '%s'.",
                        publicationId.toPlainString()));


            }

            logger.debug(String.format("Publication is of type: %s",
                                       publication.getClass().getName()));

            response.getWriter().append(exporter.exportPublication(publication));
            response.getWriter().append('\n');
            publicationName = publication.getName();


        }


        if (publicationIds.size() == 1) {
            response.setHeader("Content-Disposition",
                               String.format("attachment; filename=%s.bib",
                                             publicationName));


        } else {
            response.setHeader("Content-Disposition",
                               "attachment; filename=publications.bib");

        }
    }
}
