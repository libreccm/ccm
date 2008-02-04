/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.arsdigita.london.search;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 * This isn't really used as a servlet - its just there
 * isn't any other way to ensure that the threads are
 * only started with a servlet container - ie not when
 * using ccm-run.
 */
public class SearchWorkerServlet extends HttpServlet {

    private static final Logger s_log = Logger.getLogger(SearchWorkerServlet.class);

    private Thread[] m_workers;

    public void init(ServletConfig config) 
        throws ServletException {

        int nWorkers = Search.getConfig().getNumberOfThreads().intValue();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Starting " + nWorkers + " worker threads");
        }
        m_workers = new Thread[nWorkers];
        for (int i = 0 ; i < nWorkers ; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Starting thread " + i);
            }
            m_workers[i] = new RemoteSearcher(SearchJobQueue.getInstance());
            m_workers[i].start();
        }
    }
    
}
