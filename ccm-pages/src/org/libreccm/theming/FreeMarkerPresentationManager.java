package org.libreccm.theming;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.subsite.Site;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FreeMarkerPresentationManager implements PresentationManager {

    @Override
    public void servePage(final Document document,
                          final HttpServletRequest request,
                          final HttpServletResponse response) {

        final org.w3c.dom.Document w3cDocument = document.getInternalDocument();

        final Node root = w3cDocument.getDocumentElement();

        final String currentSiteName = Web.getConfig().getSiteName();
        Site subSite;
        try {
            subSite = Site.findByHostname(currentSiteName);
        } catch (DataObjectNotFoundException ex) {
            subSite = null;
        }
        final boolean isSubSite = subSite != null;

        final String defaultTheme;
        if (subSite == null) {
            defaultTheme = ThemeDirector.getThemeDirector().getDefaultTheme()
                .getURL();
        } else {
            defaultTheme = subSite.getStyleDirectory();
        }

        final String selectedTheme;
        if (request.getParameter("theme") == null) {
            selectedTheme = defaultTheme;
        } else {
            selectedTheme = request.getParameter("theme");
        }

        final String previewParam = request.getParameter("preview-theme");
        boolean preview = previewParam != null
                              && ("true".equalsIgnoreCase(previewParam)
                                  || "yes".equalsIgnoreCase(previewParam));

        final StringBuilder themePathBuilder = new StringBuilder(
            "/themes/");
        if (preview) {
            themePathBuilder.append("devel-themedir/");
        } else {
            themePathBuilder.append("published-themedir/");
        }
        themePathBuilder.append(selectedTheme).append("/");
        final String themePath = themePathBuilder.toString();
        final String themeManifestPath = String.format("%stheme-manifest.json",
                                                       themePath);

        final ServletContext servletContext = Web.getServletContext();

//        final String themeManifest = "";
        final String themeManifest = new BufferedReader(
            new InputStreamReader(
                servletContext.getResourceAsStream(themeManifestPath),
                StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining(System.lineSeparator()));

        String name = "???";
        final JsonFactory jsonFactory = new JsonFactory();
        try {
            final JsonParser parser = jsonFactory.createParser(servletContext
                .getResourceAsStream(themeManifestPath));

            while (!parser.isClosed()) {

                final JsonToken token = parser.nextToken();
                if (JsonToken.FIELD_NAME.equals(token)) {
                    final String fieldName = parser.getCurrentName();

                    if ("name".equals(fieldName)) {

                        final JsonToken valueToken = parser.nextToken();
                        final String value = parser.getValueAsString();
                        name = value;
                    }
                }

            }

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }
        // ToDo
        //     Parse theme manifest
        //     Get Freemarker templates by File API or by HTTP?
        //     Or via getResourceAsStream?
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setContentType("text/plain");
        try (PrintWriter writer = response.getWriter()) {
            writer.append("Data:\n");
            writer
                .append("Current Site Name: ")
                .append(currentSiteName).append("\n");
            writer
                .append("isSubSite: ")
                .append(Boolean.toString(isSubSite))
                .append("\n");
            writer
                .append("default theme: ")
                .append(defaultTheme)
                .append("\n");
            writer
                .append("selected theme: ")
                .append(selectedTheme)
                .append("\n");
            writer
                .append("preview theme? ")
                .append(Boolean.toString(preview))
                .append("\n");
            writer
                .append("themePath: ")
                .append(themePath)
                .append("\n");
            writer
                .append("themeManifestPath: ")
                .append(themeManifestPath)
                .append("\n");
            writer
                .append("themeManifest: ")
                .append(themeManifest)
                .append("\n");
            writer
                .append("theme name: ")
                .append(name)
                .append("\n");
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
