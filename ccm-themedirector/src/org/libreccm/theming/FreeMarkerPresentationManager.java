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
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelListSequence;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import org.libreccm.theming.manifest.DateFormat;
import org.libreccm.theming.manifest.ThemeManifest;
import org.libreccm.theming.manifest.ThemeManifestUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FreeMarkerPresentationManager implements PresentationManager {

    private static final Logger LOGGER = Logger
        .getLogger(FreeMarkerPresentationManager.class);

    @Override
    public void servePage(final Document document,
                          final HttpServletRequest request,
                          final HttpServletResponse response) {

        if ("xml".equals(request.getParameter("output"))) {
            final PageTransformer pageTransformer = new PageTransformer();
            pageTransformer.servePage(document, request, response);
            return;
        }

        final org.w3c.dom.Document w3cDocument = document.getInternalDocument();

        final Node root = w3cDocument.getDocumentElement();

        // final String currentSiteName = Web.getConfig().getSiteName();
        final String currentSiteName = request.getServerName();
        Site subSite;
        try {
            subSite = Site.findByHostname(currentSiteName);
        } catch (DataObjectNotFoundException ex) {
            subSite = null;
        }
//        final boolean isSubSite = subSite != null;

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
            LOGGER.error(String.format(
                "No theme manifest found at path \"%s\". "
                    + "Falling back to \"%s\". Used sitename \"%s\".",
                themeManifestPath,
                PageTransformer.class.getName(),
                currentSiteName));
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
            data.put("selectedLanguage",
                     GlobalizationHelper.getNegotiatedLocale().getLanguage());
        }

        configuration.setSharedVariable(
            "getLocalizedText",
            new GetLocalizedText(servletContext,
                                 themePath,
                                 selectedLocale.getLanguage()));

        configuration.setSharedVariable(
            "_formatDateTime",
            new FormatDateTime(manifest.getDateFormats(),
                               new Locale(selectedLocale.getLanguage())));

        configuration.setSharedVariable(
            "sortAttachmentList", new SortAttachmentList()
        );

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

        try ( PrintWriter writer = response.getWriter()) {

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
                throw new IllegalArgumentException(
                    "GetContentItemTemplate requires the following parameters: "
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

    private class GetLocalizedText implements TemplateMethodModelEx {

//        private final ResourceBundle resourceBundle;
        private final ThemeResourceBundleControl control;
        private final String bundleName;
        private final Locale locale;

        public GetLocalizedText(final ServletContext servletContext,
                                final String themePath,
                                final String language) {

//            final String bundleName = String.format("%stexts", themePath);
//            final ThemeResourceBundleControl control
//                                                 = new ThemeResourceBundleControl(
//                    servletContext);
//            resourceBundle = ResourceBundle.getBundle(bundleName,
//                                                      new Locale(language),
//                                                      control);
            control = new ThemeResourceBundleControl(servletContext);
            bundleName = String.format("%stexts", themePath);
            locale = new Locale(language);
        }

        @Override
        public Object exec(final List list) throws TemplateModelException {

            if (list.size() != 1) {
                throw new IllegalArgumentException(
                    "GetLocalizedText requires exactly one parameter: "
                        + "The key for the text to localize."
                );
            }

            final String key = ((TemplateScalarModel) list
                                .get(0))
                .getAsString();

            ResourceBundle.clearCache();
            final ResourceBundle bundle = ResourceBundle.getBundle(bundleName,
                                                                   locale,
                                                                   control);

            try {
                return bundle.getString(key);
            } catch (MissingResourceException ex) {
                return key;
            }
        }

    }

    private class FormatDateTime implements TemplateMethodModelEx {

        private final List<DateFormat> dateFormats;
        private final Locale locale;

        public FormatDateTime(final List<DateFormat> dateFormats,
                              final Locale locale) {

            this.dateFormats = dateFormats;
            this.locale = locale;
        }

        @Override
        public Object exec(final List list) throws TemplateModelException {

            if (list.isEmpty()) {
                throw new IllegalArgumentException(
                    "FormatDateTime requires the following parameters: "
                        + "style: string, year: int, month: int, "
                        + "day of month: int, hour: int, minute: int,"
                        + "second: int");
            }

            final String style = ((TemplateScalarModel) list
                                  .get(0))
                .getAsString();
//            final String yearParam = ((TemplateScalarModel) list
//                                      .get(1))
//                .getAsString();
//            final String monthParam = ((TemplateScalarModel) list
//                                       .get(2))
//                .getAsString();
//            final String dayOfMonthParam = ((TemplateScalarModel) list
//                                            .get(3))
//                .getAsString();
//            final String hourParam = ((TemplateScalarModel) list
//                                      .get(4))
//                .getAsString();
//            final String minuteParam = ((TemplateScalarModel) list
//                                        .get(5))
//                .getAsString();
//            final String secondParam = ((TemplateScalarModel) list
//                                        .get(6))
//                .getAsString();

//            final int year = Integer.parseInt(yearParam);
//            final int month = Integer.parseInt(monthParam);
//            final int dayOfMonth = Integer.parseInt(dayOfMonthParam);
//            final int hour = Integer.parseInt(hourParam);
//            final int minute = Integer.parseInt(minuteParam);
//            final int second = Integer.parseInt(secondParam);
            final int year = ((TemplateNumberModel) list
                              .get(1))
                .getAsNumber()
                .intValue();
            final int month = ((TemplateNumberModel) list
                               .get(2))
                .getAsNumber()
                .intValue();
            final int dayOfMonth = fixDayOfMonth(
                ((TemplateNumberModel) list.get(3)).getAsNumber().intValue(),
                month,
                year);
            final int hour = ((TemplateNumberModel) list
                              .get(4))
                .getAsNumber()
                .intValue();
            final int minute = ((TemplateNumberModel) list
                                .get(5))
                .getAsNumber()
                .intValue();
            final int second = ((TemplateNumberModel) list
                                .get(6))
                .getAsNumber()
                .intValue();

            final String format = findFormat(dateFormats, style, locale)
                .orElse("YYYY-MM-dd");

            final LocalDateTime localDateTime = LocalDateTime.of(year,
                                                                 month,
                                                                 dayOfMonth,
                                                                 hour,
                                                                 minute,
                                                                 second);
            final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(format, locale);

            return formatter.format(localDateTime);
        }

        private int fixDayOfMonth(final int dayOfMonth,
                                  final int month,
                                  final int year) {

            // Febrary is the most complex
            if (month == 2) {
                if (year % 4 == 0 && year % 400 == 0) {
                    if (dayOfMonth > 29) {
                        return 29;
                    } else {
                        return dayOfMonth;
                    }
                } else if (year % 4 == 0 && year % 100 != 0) {
                    if (dayOfMonth > 29) {
                        return 29;
                    } else {
                        return dayOfMonth;
                    }
                } else {
                    if (dayOfMonth > 28) {
                        return 28;
                    } else {
                        return dayOfMonth;
                    }
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                if (dayOfMonth > 30) {
                    return 30;
                } else {
                    return dayOfMonth;
                }
            } else {
                return dayOfMonth;
            }
        }

        private Optional<String> findFormat(
            final List<DateFormat> dateFormats,
            final String style,
            final Locale locale) {

            final Optional<String> format = dateFormats
                .stream()
                .filter(dateFormat -> dateFormat.getStyle().equals(style))
                .filter(
                    dateFormat -> dateFormat.getLang().equals(locale.toString())
                )
                .map(DateFormat::getFormat)
                .findAny();

            return format;
        }

    }

    private class SortAttachmentList implements TemplateMethodModelEx {

        public SortAttachmentList() {
            super();
        }

        @Override
        public Object exec(final List list) throws TemplateModelException {

            // list.get(0).get(0).get("fileOrder").getNode().getTextContent()
            if (list.isEmpty() || list.size() != 2) {
                throw new IllegalArgumentException(
                    "SortAttachmentList requires the following parameters: "
                        + "list: list to sort, "
                        + "attribute: Attribute to use for sorting"
                );
            }

            if (!(list.get(0) instanceof TemplateModelListSequence)) {
                throw new IllegalArgumentException(
                    String.format(
                        "The first argument of SortAttachmentList must be a "
                            + "Sequence."
                    )
                );
            }

            if (!(list.get(1) instanceof SimpleScalar)) {
                throw new IllegalArgumentException(
                    String.format(
                        "The second argument of SortAttachmentList must be a "
                            + "string."
                    )
                );
            }

            final List<NodeModel> attachmentList
                                      = (List<NodeModel>) ((TemplateModelListSequence) list
                                                           .get(0))
                    .getWrappedObject();
            final String sortBy = ((SimpleScalar) list.get(1)).getAsString();

            attachmentList.sort(new AttachmentListComparator(sortBy));
            return list.get(0);
        }

    }

    private class AttachmentListComparator implements Comparator<NodeModel> {

        private final String sortBy;

        public AttachmentListComparator(final String sortBy) {
            this.sortBy = sortBy;
        }

        @Override
        public int compare(
            final NodeModel attachment1, final NodeModel attachment2
        ) {
            try {
                final String value1 = ((NodeModel) attachment1.get(sortBy))
                    .getNode().getTextContent();
                final String value2 = ((NodeModel) attachment2.get(sortBy))
                    .getNode().getTextContent();

                final int order1 = Integer.parseInt(value1);
                final int order2 = Integer.parseInt(value2);

                return Integer.compare(order1, order2);

            } catch (TemplateModelException ex) {
                throw new UncheckedWrapperException(ex);
            }

        }

    }

    private String findContentItemTemplate(
        final Templates templates,
        final String objectType,
        final ContentItemViews view,
        final String style) throws TemplateModelException {

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
