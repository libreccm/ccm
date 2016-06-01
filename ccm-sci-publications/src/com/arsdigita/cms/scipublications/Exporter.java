/*
 * Copyright (c) 2010 Jens Pelzetter
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
 *
 */
package com.arsdigita.cms.scipublications;

import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Code to call the SciPublications logic. Parses the parameters from the HttpServletRequest and calls to appropriate
 * methods of the {@link SciPublicationsExporters} class.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class Exporter {

    private static final Logger LOGGER = Logger.getLogger(Exporter.class);
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_PUBLICATION = "publication";
    private final Map<String, String[]> parameters;

    protected Exporter(final Map<String, String[]> parameters) {
        super();
        this.parameters = parameters;
    }

    public void doExport(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final String format;

        try {
            format = getExportFormat();
        } catch (BadExportParametersException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }

        if (parameters.containsKey(PARAM_CATEGORY)) {
            exportCategory(format, response);
        } else if (parameters.containsKey(PARAM_PUBLICATION)) {
        }


        throw new UnsupportedOperationException();
    }

    private void exportCategory(final String format,
                                final HttpServletResponse response)
            throws ServletException, IOException {
        /*
         * If the category parameter is present, retrieve the specified
         * category and exports all publications in it.
         */
        Publication publication;
        SciPublicationsExporter exporter;
        String categoryIdStr;
        BigDecimal categoryId;
        Category category;
        CategorizedCollection objects;

        LOGGER.debug("Found parameter 'category'...");
        if (parameters.get(PARAM_CATEGORY).length != 1) {
            LOGGER.error("The parameter 'category' is expected to appear only once.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               "The parameter 'category' is expected to appear only once.");
            return;
        }

        categoryIdStr = parameters.get(PARAM_CATEGORY)[0];
        try {
            categoryId = new BigDecimal(categoryIdStr);
        } catch (NumberFormatException ex) {
            LOGGER.error("The category id could not be converted to an BigDecimal value.", ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               "The category id could not be converted to an BigDecimal value.");
            return;
        }

        try {
            category = new Category(categoryId);
        } catch (DataObjectNotFoundException ex) {
            LOGGER.error(String.format("No category with the provided "
                                       + "id '%s' found.",
                                       categoryIdStr),
                         ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               String.format("No category with the provided "
                                             + "id '%s' found.",
                                             categoryIdStr));
            return;
        }

        LOGGER.debug(String.format("Category: %s", category.getName()));

        //Get the exporter for the specified format.
        exporter = SciPublicationsExporters.getInstance().getExporterForFormat(format);
        if (exporter == null) {
            LOGGER.warn(String.format("The requested exportUsers format '%s' is not supported yet.",
                                      format));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               String.format("The requested exportUsers format '%s' is not supported yet.",
                                             format));
            return;
        }

        //Get the category.
        objects = category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
        LOGGER.debug(String.format("Category contains %d objects.", objects.size()));
        ContentBundle bundle;
        ACSObject object;
        while (objects.next()) {

            //Get the bundle
            bundle = (ContentBundle) objects.getACSObject();
            //Get the default instance of the bundle
            object = bundle.getInstance(bundle.getDefaultLanguage());

            //Ignore object if it is not an publication
            if (object instanceof Publication) {
                publication = (Publication) object;
            } else {
                LOGGER.debug("Object is not a publication, ignoring it.");
                continue;
            }

            //Ignore none live versions.
            if (!publication.isLiveVersion()) {
                LOGGER.debug("Object is no a published version, ignoring it.");
                continue;
            }

            //Write the exported publication to the response.
            response.getWriter().append(exporter.exportPublication(publication));
            //response.getWriter().append('\n');
        }

        //Set the MimeType of the response
        response.setContentType(exporter.getSupportedFormat().getMimeType().getBaseType());
        //Force the browser to display an download dialog, and set
        //the filename for the downloaded file to the name of the
        //selected category.
        response.setHeader("Content-Disposition",
                           String.format("attachment; filename=%s.%s",
                                         category.getName(),
                                         exporter.getSupportedFormat().getFileExtension()));

    }

    private String getExportFormat() throws BadExportParametersException {
        final String format;

        if (parameters.containsKey(PARAM_FORMAT)) {
            if (parameters.get(PARAM_FORMAT).length == 1) {
                format = parameters.get(PARAM_FORMAT)[0];
            } else {
                LOGGER.warn("Query parameter 'format' contains no value"
                            + "or more than one value. It is expected that "
                            + "'format' contains excactly one value. Responding"
                            + "with BAD_REQUEST status.");
                throw new BadExportParametersException("Query parameter 'format' contains no value or more"
                                                       + "than one value. It is expected that "
                                                       + "'format' contains excactly one value.");
            }
        } else {
            LOGGER.warn("Missing query parameter 'format'. Responsding with BAD_REQUEST status code.");
            throw new BadExportParametersException("Missing query parameter 'format'.");
        }

        return format;
    }

    private class BadExportParametersException extends Exception {

        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance of <code>BadExportParametersException</code> without detail message.
         */
        public BadExportParametersException() {
            super();
        }

        /**
         * Constructs an instance of <code>BadExportParametersException</code> with the specified detail message.
         *
         * @param msg The detail message.
         */
        public BadExportParametersException(final String msg) {
            super(msg);
        }

        /**
         * Constructs an instance of <code>BadExportParametersException</code> which wraps the 
         * specified exception.
         *
         * @param exception The exception to wrap.
         */
        public BadExportParametersException(final Exception exception) {
            super(exception);
        }

        /**
         * Constructs an instance of <code>BadExportParametersException</code> with the specified message which also wraps the 
         * specified exception.
         *
         * @param msg The detail message.
         * @param exception The exception to wrap.
         */
        public BadExportParametersException(final String msg, final Exception exception) {
            super(msg, exception);
        }

    }
}
