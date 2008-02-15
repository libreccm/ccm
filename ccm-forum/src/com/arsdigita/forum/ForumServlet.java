/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.forum;

import java.util.Iterator;
import java.util.Map;

import com.arsdigita.forum.ui.Constants;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.BebopApplicationServlet;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: ForumServlet.java 1628 2007-09-17 08:10:40Z chrisg23 $
 * 
 * Updated chris.gilbert@westsussex.gov.uk to make use of PageFactory and to enable 
 * disablement of client/middleware caching
 */
public class ForumServlet extends BebopApplicationServlet
        implements Constants {
    public static final String versionId =
        "$Id: ForumServlet.java 1628 2007-09-17 08:10:40Z chrisg23 $" +
        "$Author: chrisg23 $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(ForumServlet.class);

    public void init() throws ServletException {
        super.init();
		s_log.debug("creating forum page");
        final Page forum = ForumPageFactory.getPage(ForumPageFactory.FORUM_PAGE);
		s_log.debug("creating thread page");
        final Page thread = ForumPageFactory.getPage(ForumPageFactory.THREAD_PAGE);

        put("/", forum);
        put("/index.jsp", forum);
        put("/thread.jsp", thread);
        if (Forum.getConfig().disableClientPageCaching()) {
        	s_log.debug("caching disabled");
        	disableClientCaching("/");
        	disableClientCaching("/index.jsp");
        	disableClientCaching("/thread.jsp");
        }
        
        // allow other pages to be added
        // eg - allows categorised forum to add load-category page 
        // for AJAX category asignment
        Iterator it = ForumPageFactory.getPages();
        while (it.hasNext()) {
        	Object key = (Object)it.next();
        	if (!key.equals(ForumPageFactory.FORUM_PAGE) && !key.equals(ForumPageFactory.THREAD_PAGE)) {
        		put("/" + key, ForumPageFactory.getPage((String)key));
				if (Forum.getConfig().disableClientPageCaching()) {
					disableClientCaching("/" + key);
        	
    }
}
        }
        
    }
    
	
									   
}
