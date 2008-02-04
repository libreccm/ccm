package com.arsdigita.london.rss.ui;

import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.london.rss.RSSService;
import com.arsdigita.categorization.Category;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.math.BigDecimal;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * Displays an RSS Channel Index.
 *
 * @author Bryan Quinn (bquinn@arsdigita.com)
 * @version $Revision: #6 $, $Date: 2004/01/21 $
 */
public class ChannelIndex implements com.arsdigita.dispatcher.Dispatcher {
    private static Logger s_log =
        Logger.getLogger(ChannelIndex.class);
    private BigDecimal m_catId;
    /**
     * Create an index of RSS Channels available for the specified category
     * and its children.
         */
    public ChannelIndex() {
    }

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

        response.setStatus(HttpServletResponse.SC_OK);

        try {
            Application app = Web.getContext().getApplication();
            Category root = Category.getRootForObject(app);

            RSSService.generateChannelList(root, request, response);
        } catch (Exception e) {
            throw new UncheckedWrapperException( e );
        }
    }
}






