package com.arsdigita.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.arsdigita.util.servlet.HttpHost;

/**
 * Require secure connection for a set of URLs.
 * Redirect to the same path but take the host:port part from "waf.web.secure_server".
 *  
 * @author Alan Pevec
 */
public class SecureFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest hreq = (HttpServletRequest) request;
        HttpServletResponse hresp = (HttpServletResponse) response;
        String uri = hreq.getRequestURI();
        WebConfig conf = Web.getConfig(); 
        if (conf.isSecureRequired(uri) && !request.isSecure()) {
            StringBuffer secureEquivalent = new StringBuffer("https://");
            HttpHost secureServer = conf.getSecureServer(); 
            secureEquivalent.append(secureServer.getName());
            int securePort = secureServer.getPort();
            if (securePort != 443) {
                secureEquivalent
                	.append(':')
                	.append(securePort);
            }
            if (uri != null) {
                secureEquivalent.append(uri);
            }
            String queryString = hreq.getQueryString(); 
            if (queryString != null) {
                secureEquivalent.append('?')
                	.append(queryString);
            }
            hresp.sendRedirect(secureEquivalent.toString());
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void destroy() {
    }

}
