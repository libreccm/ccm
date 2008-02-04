/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.rss.ui;

import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.london.rss.RSSService;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * Displays an RSS Channel Index.
 *
 * @author Bryan Quinn (bquinn@arsdigita.com)
 * @version $Revision: #4 $, $Date: 2004/01/21 $
 */
public class FeedGenerator implements com.arsdigita.dispatcher.Dispatcher {
    private static Logger s_log =
        Logger.getLogger(FeedGenerator.class);
    
    private boolean m_acsj;
    
    /**
     * Create an index of RSS Channels available for the specified category
     * and its children.
         */
    public FeedGenerator(boolean acsj) {
        m_acsj = acsj;
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

        // Generate all of the channels.
        try {
            RSSService.generateFeedList(m_acsj, request, response);
        } catch (Exception e) {
            throw new UncheckedWrapperException( e );
        }
    }
}






