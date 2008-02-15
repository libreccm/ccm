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
package com.arsdigita.forum.ui;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.forum.Post;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 * @author chris.gilbert@westsussex.gov.uk
 * reply to post text step automatically completes
 * the subject field
 * 
 * (nb other forums eg FirstClass allocate numbers to long
 * chains of replies, so instead of having a tree with all
 * posts having subject 're: original post' they have varied
 * subjects 're:(2) original post'. If we wanted to implement
 * similar, this is the place to do it
 *
 */
public class ReplyToPostTextStep extends PostTextStep {

	private static Logger s_log = Logger.getLogger(ReplyToPostTextStep.class);
	private ReplyToPostForm m_container;
	
	public ReplyToPostTextStep(
		ACSObjectSelectionModel post,
		ReplyToPostForm container) {
		super(post, container);
		MessageView inReplyTo = new MessageView(post, container);
		m_container = container;
		add(inReplyTo);

	}
	
	protected void initWidgets(PageState state, Post post) {
		if (m_container.getContext(state).equals(ReplyToPostForm.EDIT_CONTEXT)) {
			super.initWidgets(state, post);
		} else {
			
			s_log.debug("init called");
			s_log.debug("parent is " + post);
			String prefix = "Re:";
			String subject = post.getSubject();

			if (subject.length() < 3
				|| prefix.equalsIgnoreCase(subject.substring(0, 3))) {
				setSubject(state, subject);
			} else {
				setSubject(state, prefix + " " + subject);
			}
		}

	}

}
