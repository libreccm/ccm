/*
 * Copyright (c) 2010 Jens Pelzetter,
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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;
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
 * <p> The
 * <code>SciPublicationsServlet</code> processes the
 * {@link HttpServletRequest} and calls the requested actions. The available
 * actions are: </p> <dl> <dt><code>export</code></dt> <dd> <p> The
 * <code>export</code> action exports content items of the type
 * {@link Publication} in several formats, like <em>BibTeX</em> or <em>RIS</em>.
 * The export action has the following query parameters: </p> <dl> <dt><code>format</code></dt>
 * <dd>Specifies the format which is used to export the publications.</dd> <dt><code>publication</code></dt>
 * <dd>Specifies the publication(s) to export using the ID(s) of the
 * publications. This parameter can occur more than one time. In this case, all
 * publications specified by the parameters will be exported as a single file in
 * specified format</dd> <dt><code>category</code></dt> <dd>Specifies a category
 * using the OID of the category. If this parameter is present, all publications
 * in the category and the subcategories of the category will exported as a
 * single file in the specified format.</dd> </dl> <p> The
 * <code>format</code> argument is mandatory. Also their must be either one or
 * more
 * <code>publication</code> parameters or a
 * <code>category</code> parameter. If the URL is not valid, the Servlet will
 * respond with the BAD_REQUEST (400) HTTP status code. If one of the specified
 * publications or the specified category can't be found, the Servlet will
 * respond with the NOT_FOUND (404) HTTP status code. </p> </dd> </dl>
 *
 * @author Jens Pelzetter
 */
public class SciPublicationsServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -632365939651657874L;
    private static final Logger LOGGER = Logger.getLogger(SciPublicationsServlet.class);

    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Application app) throws ServletException, IOException {
        String path = "";

        LOGGER.debug("SciPublicationsServlet is starting...");
        LOGGER.debug(String.format("pathInfo = '%s'", request.getPathInfo()));

        LOGGER.debug("Extracting path from pathInfo by removing leading and trailing slashes...");
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

        LOGGER.debug(String.format("path = %s", path));

        //Displays a text/plain page with a message.
        if (path.isEmpty()) {
            LOGGER.debug("pathInfo is null, responding with default...");

            response.setContentType("text/plain");
            response.getWriter().append("Please choose an application.");

            //ToDo: Show a menu?
        } else if ("hellobebop".equals(path)) {
            //This is just for testing
            Page page;
            Form form;
            Label label;

            page = PageFactory.buildPage("SciPublications",
                                         "Hello World with Bebop");
            form = new Form("HelloWorld");
            label = new Label("Hello World! Created with Bebop.");

            form.add(label);
            page.add(form);

            page.lock();

            final Document document = page.buildDocument(request, response);
            final PresentationManager presenter = Templating.getPresentationManager();
            presenter.servePage(document, request, response);
        } else if ("export".equals(path)) {
            LOGGER.debug("Export a publication");

            Map<String, String[]> parameters;
            String format;

            parameters = request.getParameterMap();

            //Get the format parameter
            if (parameters.containsKey("format")) {
                if (parameters.get("format").length == 1) {
                    format = parameters.get("format")[0];
                } else {
                    LOGGER.warn("Query parameter 'format' contains no value"
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
                LOGGER.warn("Missing query parameter 'format'. "
                            + "Responsding with BAD_REQUEST status code.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (parameters.containsKey("category")) {
                /*
                 * If the category parameter is present, retrieve the specified
                 * category and exports all publications in it.
                 */
                Publication publication;
                SciPublicationsExporter exporter;
                String categoryIdStr;
                BigDecimal categoryId;
                Category category;
                //CategorizedCollection objects;
                DataCollection objects;

                LOGGER.debug("Found parameter 'category'...");
                if (parameters.get("category").length != 1) {
                    LOGGER.error("The parameter 'category' is expected to appear only once.");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "The parameter 'category' is expected to appear only once.");
                    return;
                }

                categoryIdStr = parameters.get("category")[0];
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
                    LOGGER.warn(String.format("The requested export format '%s' is not supported yet.",
                                              format));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       String.format("The requested export format '%s' is not supported yet.",
                                                     format));
                    return;
                }

                //Get the category.
                //objects = category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
                objects = SessionManager.getSession().retrieve(Publication.BASE_DATA_OBJECT_TYPE);
                boolean descendCategories = false;
                if (parameters.containsKey("descendCategories") 
                    && (parameters.get("descendCategories").length >= 1))  {
                    descendCategories = "true".equals(parameters.get("descendCategories")[0]);
                }
                
                if (descendCategories) {
                    final Filter filter = objects.addInSubqueryFilter(
                            "parent.id", "com.arsdigita.categorization.objectIDsInSubtree");
                    filter.set("categoryID", category.getID());
                    
                } else {
                    objects.addEqualsFilter("parent.categories.id", category.getID());
                }
                
                if (parameters.containsKey("filter") && (parameters.get("filter").length >= 1)) {
                    final String filter = parameters.get("filter")[0];

                    if ((filter != null) && !filter.isEmpty()) {
                        objects.addFilter(filter);
                    }
                }

                LOGGER.debug(String.format("Category contains %d objects.", objects.size()));
//                ContentBundle bundle;
//                ACSObject object;
                while (objects.next()) {

                    //Get the bundle
                    //bundle = (ContentBundle) objects.getACSObject();
                    final DataObject dobj = objects.getDataObject();
                    //bundle = (ContentBundle) DomainObjectFactory.newInstance(dobj);
                    //Get the default instance of the bundle
                    //object = bundle.getInstance(bundle.getDefaultLanguage());
                    publication = (Publication) DomainObjectFactory.newInstance(dobj);

                    //Ignore object if it is not an publication
//                    if (object instanceof Publication) {
//                        publication = (Publication) object;
//                    } else {
//                        LOGGER.debug("Object is not a publication, ignoring it.");
//                        continue;
//                    }

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
                                                 exporter.getSupportedFormat().
                        getFileExtension()));

                return;
            } else if (parameters.containsKey("publication")) {
                String[] publications;
                List<BigDecimal> publicationIds;

                publications = parameters.get("publication");

                if (publications.length < 1) {
                    LOGGER.warn("Parameter 'publications' has no value(s). "
                                + "Responding with status BAD_REQUEST.");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "Parameter 'publication' has no "
                                       + "value(s).");
                    return;
                }

                //Retrieve the ids of the publication(s) to export from the
                //request.
                BigDecimal publicationId;
                publicationIds = new ArrayList<BigDecimal>();
                for (int i = 0; i < publications.length; i++) {
                    try {
                        publicationId = new BigDecimal(publications[i]);
                    } catch (NumberFormatException ex) {
                        LOGGER.warn(String.format(
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

                //Export the publictions.
                exportPublications(format, publicationIds, response);

            } else if (parameters.containsKey("orgaunitId")) {

                final BigDecimal orgaunitId = new BigDecimal(parameters.get(
                        "orgaunitId")[0]);

                final GenericOrganizationalUnit orgaunit =
                                                new GenericOrganizationalUnit(
                        orgaunitId);

                final DataQuery publicationsQuery =
                                SessionManager.getSession().
                        retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfPublicationsForOrgaUnitOneRowPerAuthor");

                final List<String> orgaunitIds = new ArrayList<String>();

                if (parameters.containsKey("recursive")
                    && ("false".equals(parameters.get("recursive")[0]))) {
                    orgaunitIds.add(orgaunit.getID().toString());
                } else {
                    final DataQuery subordinateQuery =
                                    SessionManager.getSession().retrieveQuery(
                            "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivly");
                    subordinateQuery.setParameter("orgaunitId", orgaunitId.
                            toString());

                    while (subordinateQuery.next()) {
                        orgaunitIds.add(subordinateQuery.get("orgaunitId").
                                toString());
                    }
                }

                publicationsQuery.setParameter("orgaunitIds", orgaunitIds);

                if (parameters.containsKey("year")) {
                    publicationsQuery.addFilter(String.format("year = %s",
                                                              parameters.get(
                            "year")[0]));
                }

                if (Kernel.getConfig().languageIndependentItems()) {
                    final FilterFactory filterFactory = publicationsQuery.getFilterFactory();
                    final Filter filter = filterFactory.or().
                            addFilter(filterFactory.equals("language", GlobalizationHelper.
                            getNegotiatedLocale().getLanguage())).
                            addFilter(filterFactory.and().
                            addFilter(
                            filterFactory.equals("language",
                                                 GlobalizationHelper.LANG_INDEPENDENT)).
                            addFilter(filterFactory.notIn("parent",
                                                          "com.arsdigita.navigation.getParentIDsOfMatchedItems").
                            set("language", GlobalizationHelper.
                            getNegotiatedLocale().getLanguage())));
                    publicationsQuery.addFilter(filter);

                } else {
                    publicationsQuery.addEqualsFilter("language",
                                                      GlobalizationHelper.
                            getNegotiatedLocale().getLanguage());
                }

                final List<BigDecimal> publicationIds =
                                       new ArrayList<BigDecimal>();
                while (publicationsQuery.next()) {
                    publicationIds.add((BigDecimal) publicationsQuery.get(
                            "publicationId"));
                }

                exportPublications(format, publicationIds, response);

            } else if (parameters.containsKey("authorId")) {
                final DataQuery publicationsQuery =
                                SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getPublicationsForAuthor");

                final BigDecimal authorId = new BigDecimal(parameters.get("authorId")[0]);
                final GenericPerson author = new GenericPerson(authorId);
                final StringBuilder authorFilterBuilder = new StringBuilder();
                authorFilterBuilder.append('(');
                authorFilterBuilder.append(String.format("authorId = %s", authorId.toString()));

                if (author.getAlias() != null) {
                    addAuthorAliasToFilter(authorFilterBuilder,
                                           author.getAlias());
                }

                authorFilterBuilder.append(')');

                publicationsQuery.addFilter(authorFilterBuilder.toString());

                if (parameters.containsKey("year")) {
                    publicationsQuery.addFilter(String.format("yearOfPublication = %s", parameters.get("year")[0]));
                }

                if (Kernel.getConfig().languageIndependentItems()) {
                    final FilterFactory filterFactory = publicationsQuery.getFilterFactory();
                    final Filter filter = filterFactory.or().
                            addFilter(filterFactory.equals("language", GlobalizationHelper.
                            getNegotiatedLocale().getLanguage())).
                            addFilter(filterFactory.and().
                            addFilter(
                            filterFactory.equals("language",
                                                 GlobalizationHelper.LANG_INDEPENDENT)).
                            addFilter(filterFactory.notIn("parent",
                                                          "com.arsdigita.navigation.getParentIDsOfMatchedItems").
                            set("language", GlobalizationHelper.
                            getNegotiatedLocale().getLanguage())));
                    publicationsQuery.addFilter(filter);

                } else {
                    publicationsQuery.addEqualsFilter("language",
                                                      GlobalizationHelper.
                            getNegotiatedLocale().getLanguage());
                }

                final List<BigDecimal> publicationIds =
                                       new ArrayList<BigDecimal>();
                while (publicationsQuery.next()) {
                    publicationIds.add((BigDecimal) publicationsQuery.get(
                            "publicationId"));
                }

                exportPublications(format, publicationIds, response);

            } else {
//                LOGGER.warn("Export action needs either a publication id or a "
//                            + "term id. Neither was found in the query parameters."
//                            + "Responding with BAD_REQUEST status.");
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
//                                   "The export action needs either a publication id or "
//                                   + "a term id. Neither was found in the query parameters.");
                //Otherwise, export all publications
                final DataCollection publications = SessionManager.getSession().retrieve(
                    Publication.BASE_DATA_OBJECT_TYPE);
                
                if (Kernel.getConfig().languageIndependentItems()) {
                    final FilterFactory filterFactory = publications.getFilterFactory();
                    final Filter filter = filterFactory.or().
                            addFilter(filterFactory.equals("language", GlobalizationHelper.
                            getNegotiatedLocale().getLanguage())).
                            addFilter(filterFactory.and().
                            addFilter(
                            filterFactory.equals("language",
                                                 GlobalizationHelper.LANG_INDEPENDENT)).
                            addFilter(filterFactory.notIn("parent",
                                                          "com.arsdigita.navigation.getParentIDsOfMatchedItems").
                            set("language", GlobalizationHelper.
                            getNegotiatedLocale().getLanguage())));
                    publications.addFilter(filter);

                } else {
                    publications.addEqualsFilter("language",
                                                      GlobalizationHelper.
                            getNegotiatedLocale().getLanguage());
                }
                
                if (parameters.containsKey("year")) {
                    publications.addEqualsFilter("yearOfPublication", 
                                                 parameters.get("year")[0]);
                }
                
                publications.addOrder("yearOfPublication desc");
                publications.addOrder("authorsStr");
                publications.addOrder("title");
                
                final List<BigDecimal> publicationIds =
                                       new ArrayList<BigDecimal>();
                while(publications.next()) {
                    publicationIds.add((BigDecimal) publications.get("id"));
                    
                }
                
                exportPublications(format, publicationIds, response);
            }

        } else {
            //Respond with 404 when the requested action is unknown.
            LOGGER.warn(String.format("Unknown pathinfo '%s', "
                                      + "responding with 404...",
                                      path));
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                               String.format("The path '%s' is not known.", path));
        }
    }

    /**
     * Helper method for exporting publications specified by a list of IDs.
     *
     * @param format The format to use.
     * @param publicationIds The IDs of the publications to export
     * @param response The {@link HttpServletResponse} to use
     * @throws IOException Thrown by some methods called by this method.
     */
    private void exportPublications(final String format,
                                    final List<BigDecimal> publicationIds,
                                    final HttpServletResponse response)
            throws IOException {
        SciPublicationsExporter exporter;

        //Get the exporter for the specified format.
        exporter = SciPublicationsExporters.getInstance().getExporterForFormat(
                format);


        if (exporter == null) {
            LOGGER.warn(String.format(
                    "The requested export format '%s' is not supported yet.",
                    format));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                               String.format(
                    "The requested export format '%s' is not supported yet.",
                    format));


            return;
        }

        //Set the MimeType type of the response.
        response.setContentType(exporter.getSupportedFormat().getMimeType().
                getBaseType());

        Publication publication = null;
        String publicationName = "publication";
        final StringBuilder result = new StringBuilder();

        if (exporter.getPreamble() != null) {
            result.append(exporter.getPreamble());
        }
        
        for (BigDecimal publicationId : publicationIds) {
            try {
                //Get  the publication
                publication = new Publication(publicationId);
                LOGGER.debug(String.format("OID of publication: %s",
                                           publication.getOID()));
                //Specialize the publication
                publication =
                (Publication) DomainObjectFactory.newInstance(
                        publication.getOID());
            } catch (DataObjectNotFoundException ex) {
                LOGGER.warn(String.format("No publication found for id '%s'.",
                                          publicationId.toPlainString()), ex);
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                   String.format(
                        "No publication found for id '%s'.",
                        publicationId.toPlainString()));


            }

            LOGGER.debug(String.format("Publication is of type: %s",
                                       publication.getClass().getName()));

            //Write the exported publication data to the response.
            //response.getWriter().append(exporter.exportPublication(publication));
            result.append(exporter.exportPublication(publication));
            //response.getWriter().append('\n');
            publicationName = publication.getName();


        }

        //Force the browser to show a download dialog.
        if (publicationIds.size() == 1) {
            //If only one publication is exported, use the name (URL) of the
            //publication as filename.
            response.setHeader("Content-Disposition",
                               String.format("attachment; filename=%s.%s",
                                             publicationName,
                                             exporter.getSupportedFormat().
                    getFileExtension()));
        } else {
            //If more than one publication is exported, use 'publications' as
            //filename.
            response.setHeader("Content-Disposition",
                               String.format(
                    "attachment; filename=publications.%s",
                    exporter.getSupportedFormat().
                    getFileExtension()));

        }

        response.getWriter().append(result.toString());
    }

    private void addAuthorAliasToFilter(final StringBuilder builder,
                                        final GenericPerson alias) {
        builder.append(String.format("or authorId = %s",
                                     alias.getID().toString()));

        if (alias.getAlias() != null) {
            addAuthorAliasToFilter(builder, alias.getAlias());
        }
    }

}
