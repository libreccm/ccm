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
 *
 */
package com.arsdigita.forum;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.forum.ui.ForumComponent;
import com.arsdigita.forum.ui.ThreadComponent;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * Maps the bboard user interface onto "index.jsp" and "thread.jsp".
 *
 * @author Kevin Scaldeferri
 * @author Vadim Nasardinov (vadimn@redhat.com)
 *
 * @version $Revision: #18 $ $Author: sskracic $ $DateTime: 2004/08/17 23:26:27 $
 */
public class BboardDispatcher extends BebopMapDispatcher
        implements Constants {
    public static final String versionId =
        "$Id: BboardDispatcher.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final String XSL_HOOK = "forum";

    private static final Logger s_log = Logger.getLogger
        (BboardDispatcher.class);

    /**
     * Constructor.  Instantiates the subsite url/page mapping.
     */
    public BboardDispatcher() {
        s_log.warn("BboardDispatcher created!");

        Page index = buildForumPage(new BigDecimalParameter(TOPIC_PARAM));
        addPage("", index);
        addPage("index.jsp", index);
        addPage("thread.jsp", buildThreadPage(
                    new BigDecimalParameter(THREAD_PARAM)));
    }

    static Page buildThreadPage(BigDecimalParameter stateParam) {
        Page threadPage = PageFactory.buildPage(XSL_HOOK, "Threads", "forumThreadPage");
        threadPage.add(new ThreadComponent());
        // Register the thread id parameter as a global state parameter.
        threadPage.addGlobalStateParam(stateParam);
        threadPage.addRequestListener
            (new ThreadPageRequestListener(stateParam));
        threadPage.lock();
        return threadPage;
    }

    static Page buildForumPage(BigDecimalParameter stateParam) {
        Page page = PageFactory.buildPage(XSL_HOOK, "Forum", "forumPage");
        ForumComponent forumComp = new ForumComponent();
        page.add(forumComp);
        page.addGlobalStateParam(stateParam);
        page.addRequestListener(new ForumPageRequestListener(stateParam, forumComp));
        page.lock();
        return page;
    }

    private static class ThreadPageRequestListener implements RequestListener {
        private BigDecimalParameter m_threadID;

        public ThreadPageRequestListener(BigDecimalParameter threadID) {
            m_threadID = threadID;
        }


        public void pageRequested(RequestEvent event) {
            PageState state = event.getPageState();
            ForumContext context = ForumContext.getContext(state);
            context.setThreadID
                ((BigDecimal) event.getPageState().getValue(m_threadID));
        }
    }

    private static class ForumPageRequestListener implements RequestListener {
        private BigDecimalParameter m_categorySelection;
        private ForumComponent m_forumComp;

        public ForumPageRequestListener(BigDecimalParameter categorySelection,
                                        ForumComponent forumComp) {
            m_categorySelection = categorySelection;
            m_forumComp = forumComp;
        }

        public void pageRequested(RequestEvent event) {

            PageState state = event.getPageState();
            ForumContext context = ForumContext.getContext(state);

            context.setCategorySelection
                ((BigDecimal) event.getPageState().getValue(m_categorySelection));
        }
    }
}
