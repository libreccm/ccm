package com.arsdigita.profiler;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Initialize Profiler timers at the request start, and log them at the end. 
 * This Filter must be mapped as a first in filter chain!
 *  
 * @author Alan Pevec
 */
public class ProfilerFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        long startTime = System.currentTimeMillis(); 
        // optional: unique generated cookie to distinguish client requests  
        String info = ((HttpServletRequest)request).getPathInfo();
        Profiler.startRequest();
        filterChain.doFilter(request, response);
        Profiler.stopRequest(info, System.currentTimeMillis() - startTime );
    }

    public void destroy() {
    }

}
