/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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

import java.math.BigDecimal;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
// import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.forum.ui.DiscussionThreadSimpleView;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.toolbox.ui.ApplicationAuthenticationListener;
import com.arsdigita.xml.Element;

/**
 * Implementation of com.arsdigita.forum.PageBuilder that creates a basic
 * thread page with read access check
 *
 * @author chris.gilbert@westsussex.gov.uk
 */
public class ThreadPageBuilder implements PageBuilder, Constants {

	/* (non-Javadoc)
	 * @see com.arsdigita.forum.PageBuilder#buildPage(
     *          com.arsdigita.bebop.parameters.ParameterModel)
	 */
	/** 
     * 
     * @return
     */
    public Page buildPage() {
		Page threadPage = PageFactory.buildPage(Constants.FORUM_XML_PREFIX,
                                                "Threads", "forumThreadPage");
		//Output the title in an easy to find place
		threadPage.add(new SimpleComponent(){
			public void generateXML(PageState state, Element parent) {
				Element nameElement = parent.newChildElement(
                                           Constants.FORUM_XML_PREFIX + ":name",
                                           Constants.FORUM_XML_NS);
				nameElement.setText(ForumContext.getContext(state).getForum().
                                                                   getTitle());
				Element introductionElement = parent.newChildElement(
                                                     Constants.FORUM_XML_PREFIX +
                                                     ":introduction",
                                                     Constants.FORUM_XML_NS);
				introductionElement.setText(ForumContext.getContext(state).
                                                         getForum().
                                                         getIntroduction());
			} 
		});
        // 
		threadPage.add(new DiscussionThreadSimpleView());
		// Register the thread id parameter as a global state parameter.
		BigDecimalParameter threadID =  new BigDecimalParameter(THREAD_PARAM);
		threadPage.addGlobalStateParam(threadID);
		threadPage.addRequestListener(
                new ApplicationAuthenticationListener(PrivilegeDescriptor.READ));
       
		threadPage.addRequestListener
			(new ThreadPageRequestListener(threadID));
		return threadPage;
	}
	
	/**
     * 
     */
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

}
