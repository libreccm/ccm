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

import java.util.Date;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.forum.Post;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.xml.Element;

/**
 * @author Chris Gilbert <a href="mailto:chris.gilbert@westsussex.gov.uk">chris.gilbert@westsussex.gov.uk</a>
 *
 * Traverse the post to output all it's attributes and associations for previewing
 * (or rather, pretend to - the post isn't persistent until confirm step is complete
 * so it can't be traversed. Instead, each step adds some xml to represent the  data
 * it has gathered
 */
public class ConfirmStep extends SimpleContainer implements Constants {

	private PostForm m_container;
	private ACSObjectSelectionModel m_post;

	public ConfirmStep(ACSObjectSelectionModel post, PostForm container) {
		m_container = container;
		m_post = post;
	}

	public void generateXML(PageState state, Element parent) {
		Element content = parent.newChildElement(FORUM_XML_PREFIX + ":postConfirm",FORUM_XML_NS);
		Date sent;
		Party sender;
		if (m_container.getContext(state).equals(PostForm.EDIT_CONTEXT)) {
			Post post = (Post)m_post.getSelectedObject(state);
			sent = post.getSentDate();
			sender = post.getFrom();
		} else {
			sent = new Date();
			sender = Kernel.getContext().getParty();
			if (sender == null) {
				sender = Kernel.getPublicUser();
			}
		}
		
		content.newChildElement(Post.SENT).setText(sent.toString());
		
		content.newChildElement(Post.SENDER).newChildElement(User.DISPLAY_NAME).setText(sender.getDisplayName());
		
		
		
		m_container.generatePostXML(state, content);
		
	}
}
