package org.libreccm.theming;

import org.libreccm.theming.manifest.ApplicationTemplate;
import org.libreccm.theming.manifest.ContentItemTemplate;
import org.libreccm.theming.manifest.Templates;

import com.arsdigita.bebop.Bebop;
import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.subsite.Site;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.themedirector.ui.ThemeXSLParameterGenerator;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        final InputStream manifestInputStream = servletContext
            .getResourceAsStream(themeManifestPath);
        if (manifestInputStream == null) {
            final PageTransformer pageTransformer = new PageTransformer();
            pageTransformer.servePage(document, request, response);
            return;
        }
        final ThemeManifestUtil manifestUtil = ThemeManifestUtil.getInstance();

        final ThemeManifest manifest = manifestUtil
            .loadManifest(manifestInputStream,
                          themeManifestPath);

        final Templates templates = manifest.getTemplates();

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

        if ("XSL_FALLBACK.XSL".equals(applicationTemplatePath)) {
            final PageTransformer pageTransformer = new PageTransformer();
            pageTransformer.servePage(document, request, response);
            return;
        }

        final Configuration configuration = new Configuration(
            Configuration.VERSION_2_3_28);
        final WebappTemplateLoader themeTemplateLoader
                                       = new WebappTemplateLoader(servletContext,
                                                                  themePath);
        final WebappTemplateLoader macrosLoader = new WebappTemplateLoader(
            servletContext,
            "/themes/freemarker");
        final MultiTemplateLoader templateLoader = new MultiTemplateLoader(
            new TemplateLoader[]{themeTemplateLoader, macrosLoader});
//        configuration.setServletContextForTemplateLoading(servletContext,
//                                                          themePath);
        configuration.setTemplateLoader(templateLoader);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setSharedVariable("getContentItemTemplate",
                                        new GetContentItemTemplate(templates));

        final Map<String, Object> data = new HashMap<>();

        // The XML document
        data.put("model", NodeModel.wrap(root));

        // Parameters (in XSL provided as XSL parameters)
        data.put("contextPath", request.getContextPath());
        data.put("contextPrefix",
                 Web.getWebContext().getRequestURL().getContextPath());
        data.put("dcpOnButtons",
                 Bebop.getConfig().doubleClickProtectionOnButtons());
        data.put("themePrefix", themePath);
        data.put("dcpOnLinks",
                 Bebop.getConfig().doubleClickProtectionOnLinks());
        data.put("dispatcherPrefix", com.arsdigita.web.URL.getDispatcherPath());
        final String host;
        if (request.getServerPort() == 80) {
            host = String.format("%s://%s",
                                 request.getScheme(),
                                 request.getServerName());
        } else {
            host = String.format("%s://%s:%d",
                                 request.getScheme(),
                                 request.getServerName(),
                                 request.getServerPort());
        }
        data.put("host", host);
        data.put("internalTheme",
                 Web.getWebContext().getRequestURL().getContextPath()
                     + com.arsdigita.web.URL.INTERNAL_THEME_DIR);
        data.put("negotiatedLanguage",
                 GlobalizationHelper.getNegotiatedLocale().getLanguage());
        data.put("requestScheme", request.getScheme());
        data.put("rootContextPrefix",
                 Web.getConfig().getDispatcherContextPath());
        final Locale selectedLocale = GlobalizationHelper
            .getSelectedLocale(request);
        if (selectedLocale == null) {
            data.put("selectedLanguage", "");
        } else {
            data.put("selectedLanguage", selectedLocale.getLanguage());
        }
        data.put("serverName", request.getServerName());
        data.put("serverPort", request.getServerPort());
        data.put("userAgent", request.getHeader("user-Agent"));

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
        } catch (IOException | TemplateException ex) {
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

    private class GetContentItemTemplate implements TemplateMethodModelEx {

        private final Templates templates;

        public GetContentItemTemplate(final Templates templates) {
            this.templates = templates;
        }

        @Override
        public Object exec(final List list) throws TemplateModelException {

            if (list.isEmpty()) {
                throw new IllegalArgumentException("GetContentItemTemplate "
                                                       + "requires the following parameters: "
                                                   + "item: NodeModel, view: String, style: String");
            }

//            final Object arg0 = list.get(0);
//            if (!(arg0 instanceof NodeModel)) {
//                throw new IllegalArgumentException(
//                    "Parameter item must be a NodeModel.");
//            }
//            final NodeModel itemModel = (NodeModel) arg0;
            final String objectType = ((TemplateScalarModel) list
                                       .get(0))
                .getAsString();

            final String view;
            if (list.size() >= 2) {
                view = ((TemplateScalarModel) list
                        .get(1))
                    .getAsString()
                    .toUpperCase(Locale.ROOT);
            } else {
                view = "DETAIL";
            }
            final ContentItemViews contentItemView = ContentItemViews
                .valueOf(view);

            final String style;
            if (list.size() >= 3) {
                style = ((TemplateScalarModel) list.get(2)).getAsString();
            } else {
                style = "";
            }

            return findContentItemTemplate(templates,
                                           objectType,
                                           contentItemView,
                                           style);
        }

    }

    private String findContentItemTemplate(
        final Templates templates,
        final String objectType,
        final ContentItemViews view,
        final String style) throws TemplateModelException {

//        final String nodeNamespace = itemModel.getNodeNamespace();
//        final String nodeName = itemModel.getNodeName();
//        final String contentType;
//        if ("http://www.arsdigita.com/cms/1.0".equals(nodeNamespace)
//                && "item".equals(nodeName)) {
//            contentType = ((TemplateScalarModel) itemModel
//                           .get("objectType"))
//                .getAsString();
//        } else if ("http://ccm.redhat.com/navigation".equals(nodeNamespace)
//                       && "item".equals(nodeName)) {
//            final TemplateModel objectTypeElems = itemModel
//                .get("attribute[@name='objectType']");
//            if (objectTypeElems instanceof TemplateSequenceModel) {
//                final TemplateModel objectTypeElem
//                                    = ((TemplateSequenceModel) objectTypeElems)
//                        .get(0);
//                contentType = ((TemplateScalarModel) objectTypeElem)
//                    .getAsString();
//            } else if (objectTypeElems instanceof TemplateScalarModel) {
//                contentType = ((TemplateScalarModel) objectTypeElems)
//                    .getAsString();
//            } else {
//                throw new IllegalArgumentException(
//                    "Can't determine object type of item.");
//            }
//        } else {
//            throw new IllegalArgumentException(String.format(
//                "Unexpected combination of node namespace and nodename. "
//                    + "nodeNamespace = \"%s\"; nodeName = \"%s\"",
//                nodeNamespace,
//                nodeName));
//        }
        final Optional<ContentItemTemplate> forTypeViewAndStyle = templates
            .getContentItems()
            .stream()
            .filter(template -> filterContentItemTemplate(template,
                                                          objectType,
                                                          view,
                                                          style))
            .findAny();

        if (forTypeViewAndStyle.isPresent()) {
            return forTypeViewAndStyle.get().getTemplate();
        } else {

            final Optional<ContentItemTemplate> forTypeAndView = templates
                .getContentItems()
                .stream()
                .filter(template -> filterContentItemTemplate(template,
                                                              objectType,
                                                              view))
                .findAny();

            if (forTypeAndView.isPresent()) {
                return forTypeAndView.get().getTemplate();
            } else {
                return templates.getDefaultContentItemsTemplate();
            }
        }

    }

    private boolean filterContentItemTemplate(
        final ContentItemTemplate template,
        final String contentType,
        final ContentItemViews view) {

        final ContentItemViews templateView;
        if (template.getView() == null) {
            templateView = ContentItemViews.DETAIL;
        } else {
            templateView = template.getView();
        }

        return template.getContentType().equals(contentType)
                   && templateView == view;
    }

    private boolean filterContentItemTemplate(
        final ContentItemTemplate template,
        final String contentType,
        final ContentItemViews view,
        final String style) {

        final ContentItemViews templateView;
        if (template.getView() == null) {
            templateView = ContentItemViews.DETAIL;
        } else {
            templateView = template.getView();
        }

        final String templateStyle;
        if (template.getStyle() == null) {
            templateStyle = "";
        } else {
            templateStyle = template.getStyle();
        }

        return template.getContentType().equals(contentType)
                   && templateView == view
                   && templateStyle.equals(style);
    }

}
