package com.arsdigita.bundle;

import com.arsdigita.kernel.KernelConfig;

import org.apache.log4j.Logger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet filter which adds the {@code lang} parameter to the requested URL.
 * A the moment the same URL in CCM may point to different language variants of
 * content. This can be a problem when a cache like mod_cache is used. This
 * filter redirect the incoming request to an URL with the {@code lang} set to
 * the default language if the requested URL does not contain the {@code lang}
 * parameter.
 *
 * If the requested URL contains one of the following the filter does nothing:
 * <ul>
 * <li>/ccm/admin</li>
 * <li>/ccm/content-center</li>
 * <li>/themes</li>
 * <li>admin.jsp</li>
 * <li>item.jsp</li>
 * </ul>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LanguageUrlFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(
        LanguageUrlFilter.class);

    @Override
    public void init(final FilterConfig fc) throws ServletException {
        // Nothing
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain filterChain)
        throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {

            final HttpServletRequest httpRequest = (HttpServletRequest) request;
//            if (!httpRequest.getRequestURI().contains("/navigation/")
//                    && !httpRequest.getRequestURI()
//                    .contains("/content-section/")
//                    && !httpRequest.getRequestURI().contains("/profiles/")
//                    && !httpRequest.getRequestURI().contains("/redirect/")) {
//
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            if (httpRequest.getRequestURI().contains("/content-section/")
//                    && httpRequest.getRequestURI().contains("admin.jsp")) {
//
//                filterChain.doFilter(request, response);
//                return;
//            }

            if (httpRequest.getRequestURI().contains("/ccm/admin")
                    || httpRequest.getRequestURI().contains(
                    "/ccm/content-center")
                    || httpRequest.getRequestURI().contains("/themes/")
                    || httpRequest.getRequestURI().contains("admin.jsp")
                    || httpRequest.getRequestURI().contains("item.jsp")) {

                filterChain.doFilter(request, response);
                return;
            }

            final String queryString = httpRequest.getQueryString();

            if (queryString != null
                    && queryString.contains("lang=")) {

                LOGGER.debug("Request URI query string already contains lang "
                                 + "parameter. Nothing to do.");
                filterChain.doFilter(request, response);
            } else {

                LOGGER.debug("Request URI query string does *not* contain lang"
                                 + "parameter. Redirect to URL with language parameter set "
                             + "to default language.");
                final String defaultLang = KernelConfig
                    .getConfig()
                    .getDefaultLanguage();

                final StringBuilder redirectToUriBuilder = new StringBuilder();
                redirectToUriBuilder
                    .append(httpRequest.getContextPath())
                    .append(httpRequest.getServletPath())
                    .append(httpRequest.getPathInfo());
                if (queryString == null || queryString.trim().isEmpty()) {
                    redirectToUriBuilder.append("?lang=").append(defaultLang);
                } else {
                    redirectToUriBuilder
                        .append("?")
                        .append(queryString)
                        .append("&lang=")
                        .append(defaultLang);
                }

                final String redirectToUri = redirectToUriBuilder.toString();
                LOGGER.debug(String.format("Redirecting to \"%s\"...",
                                           redirectToUri));
                ((HttpServletResponse) response).sendRedirect(redirectToUri);

//                httpRequest
//                    .getRequestDispatcher(redirectToUri)
//                    .forward(request, response);
                //                final StringBuilder redirectToUriBuffer = new StringBuilder();
////                redirectToUriBuffer.append(httpRequest.getContextPath());
//                redirectToUriBuffer.append(httpRequest.getServletPath());
//                redirectToUriBuffer.append(redirectToPathInfo);
//                if (queryString == null
//                        || queryString.trim().isEmpty()) {
//
//                    redirectToUriBuffer.append("?lang=").append(lang);
//                } else {
//                    redirectToUriBuffer
//                        .append(queryString)
//                        .append("&lang=")
//                        .append(lang);
//                }
//
//                final String redirectToUri = redirectToUriBuffer
//                    .toString()
//                    .replace("//", "/");
//
//                LOGGER.debug(String.format("Redirecting to \"%s\"...",
//                                           redirectToUri));
//                httpRequest
//                    .getRequestDispatcher(redirectToUri)
//                    .forward(request, response);
//                LOGGER.debug("Determing languge to use.");
//                final String pathInfo = httpRequest.getPathInfo();
//                final String lang;
//                final String redirectToPathInfo;
//                if (pathInfo != null && pathInfo.matches("(.*)\\.([a-zA-Z0-9])*")) {
//                    final String languageExt = pathInfo
//                        .substring(pathInfo.lastIndexOf("."), 
//                                   pathInfo.length() - 1);
//                    final String[] supportedLangs = KernelConfig
//                        .getConfig().getSupportedLanguages().split(",");
//                    redirectToPathInfo = pathInfo
//                        .substring(0, pathInfo.lastIndexOf("."));
//                    final boolean isSupported = Arrays
//                        .binarySearch(supportedLangs, languageExt) != -1;
//
//                    if (isSupported) {
//                        LOGGER.debug(String.format(
//                            "Using language \"%s\" from language extension.",
//                            languageExt));
//                        lang = languageExt;
//                    } else {
//                        LOGGER.debug(String.format(
//                            "Language \"%s\" is not supported. Using "
//                                + "standard language.",
//                            languageExt));
//                        lang = KernelConfig.getConfig().getDefaultLanguage();
//                    }
//                } else {
//                    LOGGER.debug(
//                        "No language extension, using standard language...");
//                    lang = KernelConfig.getConfig().getDefaultLanguage();
//                    redirectToPathInfo = pathInfo;
//                }
//
//                final StringBuilder redirectToUriBuffer = new StringBuilder();
////                redirectToUriBuffer.append(httpRequest.getContextPath());
//                redirectToUriBuffer.append(httpRequest.getServletPath());
//                redirectToUriBuffer.append(redirectToPathInfo);
//                if (queryString == null
//                        || queryString.trim().isEmpty()) {
//
//                    redirectToUriBuffer.append("?lang=").append(lang);
//                } else {
//                    redirectToUriBuffer
//                        .append(queryString)
//                        .append("&lang=")
//                        .append(lang);
//                }
//
//                final String redirectToUri = redirectToUriBuffer
//                    .toString()
//                    .replace("//", "/");
//
//                LOGGER.debug(String.format("Redirecting to \"%s\"...",
//                                           redirectToUri));
//                httpRequest
//                    .getRequestDispatcher(redirectToUri)
//                    .forward(request, response);
            }

        } else {
            LOGGER.debug("Request is not a HttpServletRequest. Skiping filter.");
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Nothing
    }

}
