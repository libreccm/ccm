package com.arsdigita.london.rss.ui;

import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.london.rss.RSSService;
import com.arsdigita.categorization.Category;
import com.arsdigita.util.UncheckedWrapperException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.io.IOException;

/**
 * Implements a service for returning RSS for a specified channel.
 * A channel is identified as a particular category with an ID.
 * A typical URL is /channels/rss/channel?id=XX where XX corresponds
 * to the id of a category.
 *
 * @author Bryan Quinn (bquinn@arsdigita.com)
 * @version $Revision: #3 $, $Date: 2004/01/21 $
 */
public class ChannelGenerator implements com.arsdigita.dispatcher.Dispatcher {
    private static final org.apache.log4j.Category s_log =
        org.apache.log4j.Category.getInstance( ChannelGenerator.class );

    /**
     * Dispatches this request.
     * @param request the servlet request object
     * @param response the servlet response object
     * @param actx the request context
     * @exception java.io.IOException may be thrown by the dispatcher
     * to indicate an I/O error
     * @exception javax.servlet.ServletException may be thrown by the
     *  dispatcher to propagate a generic error to its caller
     */
    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
            throws IOException, ServletException {
        // Retrieve RSS parameter.
        Category cat = retrieveChannelCategory(request);
        if (cat == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            channelPage(cat, request, response, actx);
        }
    }

    /**
     * Generate a page for the channel.
     */
    private void channelPage(Category cat, 
                             HttpServletRequest request,
                             HttpServletResponse response,
                             RequestContext actx) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            RSSService.generateChannel(cat.getID(), request, response);
        } catch (Exception e) {
            throw new UncheckedWrapperException( e );
        }
    }

    /**
     * Retrieve the category object on this request.
     * @return The specified category, or null if it does not map to a category.
     */
    private Category retrieveChannelCategory(HttpServletRequest req) {
        Category cat;
        BigDecimal id;
        Object o = req.getParameter("id");
        try {
            id = new BigDecimal((String)o);
        } catch (Exception e) {
            return null;
        }

        try {
            cat = new Category(id);
        } catch (Exception e2) {
            return null;
        }
        return cat;
    }
}
