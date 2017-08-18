package com.arsdigita.bundle;

import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.web.WebConfig;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AddVaryHeaderFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain)
        throws IOException, ServletException {

        if (servletResponse instanceof HttpServletResponse) {

            final HttpServletResponse response
                                      = (HttpServletResponse) servletResponse;

            final WebConfig webConfig = WebConfig.getInstanceOf();
                if (webConfig.getVaryHeaders() != null
                        && !webConfig.getVaryHeaders().isEmpty()) {
                    response
                        .addHeader("Vary", webConfig.getVaryHeaders());
                }
                final HttpSession session = ((HttpServletRequest) servletRequest)
                    .getSession();
                if (session != null && session.getAttribute(
                    GlobalizationHelper.LANG_PARAM) != null) {

                    response.addCookie(new Cookie(
                        GlobalizationHelper.LANG_PARAM,
                        (String) session.getAttribute(
                            GlobalizationHelper.LANG_PARAM)));
                }
            
//            response.addHeader("Vary", "accept-language");

            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {
    }

}
