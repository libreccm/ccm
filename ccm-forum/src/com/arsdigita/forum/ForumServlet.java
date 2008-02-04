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

import com.arsdigita.forum.ui.Constants;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.BebopApplicationServlet;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: ForumServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ForumServlet extends BebopApplicationServlet
        implements Constants {
    public static final String versionId =
        "$Id: ForumServlet.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(ForumServlet.class);

    public void init() throws ServletException {
        super.init();

        final Page forum = BboardDispatcher.buildForumPage
            (new BigDecimalParameter(TOPIC_PARAM));
        final Page thread = BboardDispatcher.buildThreadPage
            (new BigDecimalParameter(THREAD_PARAM));

        put("/", forum);
        put("/index.jsp", forum);
        put("/thread.jsp", thread);
    }
}
