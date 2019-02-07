package org.libreccm.theming;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.subsite.Site;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            defaultTheme = ThemeDirector
                .getThemeDirector()
                .getDefaultTheme()
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
        final String themeManifestPath = String.format(
            "%s" + ThemeConstants.THEME_MANIFEST_JSON, themePath);

        final ServletContext servletContext = Web.getServletContext();

//        final String themeManifest = "";
//        final String themeManifest = new BufferedReader(
//            new InputStreamReader(
//                servletContext.getResourceAsStream(themeManifestPath),
//                StandardCharsets.UTF_8))
//            .lines()
//            .collect(Collectors.joining(System.lineSeparator()));
//
//        String name = "???";
//        final JsonFactory jsonFactory = new JsonFactory();
//        try {
//            final JsonParser parser = jsonFactory.createParser(servletContext
//                .getResourceAsStream(themeManifestPath));
//
//            while (!parser.isClosed()) {
//
//                final JsonToken token = parser.nextToken();
//                if (JsonToken.FIELD_NAME.equals(token)) {
//                    final String fieldName = parser.getCurrentName();
//
//                    if ("name".equals(fieldName)) {
//
//                        final JsonToken valueToken = parser.nextToken();
//                        final String value = parser.getValueAsString();
//                        name = value;
//                    }
//                }
//
//            }
//
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
        final InputStream manifestInputStream = servletContext
            .getResourceAsStream(themeManifestPath);
        final ThemeManifestUtil manifestUtil = ThemeManifestUtil.getInstance();

        final ThemeManifest manifest = manifestUtil
            .loadManifest(manifestInputStream,
                          themeManifestPath);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JaxbAnnotationModule());
        final Templates templates;
        try {
            templates = objectMapper.readValue(
                servletContext.getResourceAsStream(
                    String.format("%stemplates.json", themePath)),
                Templates.class);
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        // ToDo
        final NamedNodeMap pageAttrs = root.getAttributes();
        final Node applicationNameAttr = pageAttrs.getNamedItem("application");
        final Node applicationClassAttr = pageAttrs.getNamedItem("class");
        final String applicationName = applicationNameAttr.getNodeValue();
        final String applicationClass = applicationClassAttr.getNodeValue();

        final Optional<ApplicationTemplate> applicationTemplate
                                                = findApplicationTemplate(
                templates,
                applicationName,
                applicationClass);
        final String applicationTemplatePath;
        if (applicationTemplate.isPresent()) {
            applicationTemplatePath = applicationTemplate.get().getTemplate();
        } else {
            applicationTemplatePath = templates.getDefaultApplicationTemplate();
        }

        final Configuration configuration = new Configuration(
            Configuration.VERSION_2_3_28);
        configuration.setServletContextForTemplateLoading(servletContext,
                                                          themePath);
        configuration.setDefaultEncoding("UTF-8");

        final Map<String, Object> data = new HashMap<>();
        data.put("ccm", NodeModel.wrap(root));

        final Template template;
        try {
            template = configuration.getTemplate(applicationTemplatePath);
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setContentType("text/html");

        try (PrintWriter writer = response.getWriter()) {

            template.process(data, writer);

//            writer.append("Data:\n");
//            writer
//                .append("Current Site Name: ")
//                .append(currentSiteName).append("\n");
//            writer
//                .append("isSubSite: ")
//                .append(Boolean.toString(isSubSite))
//                .append("\n");
//            writer
//                .append("default theme: ")
//                .append(defaultTheme)
//                .append("\n");
//            writer
//                .append("selected theme: ")
//                .append(selectedTheme)
//                .append("\n");
//            writer
//                .append("preview theme? ")
//                .append(Boolean.toString(preview))
//                .append("\n");
//            writer
//                .append("themePath: ")
//                .append(themePath)
//                .append("\n");
//            writer
//                .append("themeManifestPath: ")
//                .append(themeManifestPath)
//                .append("\n");
//            writer
//                .append("themeManifest: ")
//                .append(manifest.toString())
//                .append("\n");
//            writer
//                .append("theme name: ")
//                .append(manifest.getName())
//                .append("\n");
//            writer
//                .append("Application name: ")
//                .append(applicationName)
//                .append("\n");
//            writer
//                .append("Application class: ")
//                .append(applicationClass)
//                .append("\n");
//            writer
//                .append("Application templates:\n");
//            for (final ApplicationTemplate template : templates
//                .getApplications()) {
//                writer
//                    .append("\t")
//                    .append(template.toString())
//                    .append("\n");
//            }
        } catch (IOException  | TemplateException ex) {
            throw new UncheckedWrapperException(ex);
        }

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Optional<ApplicationTemplate> findApplicationTemplate(
        final Templates templates,
        final String applicationName,
        final String applicationClass) {

        final Optional<ApplicationTemplate> forNameAndClass = templates
            .getApplications()
            .stream()
            .filter(template -> filterApplicationTemplates(template,
                                                           applicationName,
                                                           applicationClass))
            .findAny();
        if (forNameAndClass.isPresent()) {
            return forNameAndClass;
        } else {

            final Optional<ApplicationTemplate> forName = templates
                .getApplications()
                .stream()
                .filter(tpl -> tpl.getApplicationName().equals(applicationName))
                .findAny();

            return forName;
        }
    }

    private boolean filterApplicationTemplates(
        final ApplicationTemplate template,
        final String applicationName,
        final String applicationClass) {

        return template.getApplicationName().equals(applicationName)
                   && template.getApplicationClass().equals(applicationClass);
    }

}
