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

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ThreadSubscription;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * @see com.arsdigita.forum.Post.java 
 * @see com.arsdigita.forum.EditPostForm.java 
 * @author rewritten by Chris Gilbert
 * @version $Id: RootPostForm.java,v 1.3 2006/07/13 10:19:28 cgyg9330 Exp $
 */

class RootPostForm extends PostForm {

	private static final Logger s_log = Logger.getLogger(RootPostForm.class);

	public RootPostForm(ACSObjectSelectionModel m_post) {
		super("newPostForm", m_post);
		setupComponent();

	}
	public RootPostForm() {
		super("newPostForm");
		setupComponent();
	}

	
	/* (non-Javadoc)
		 * @see com.arsdigita.forum.ui.PostForm#getTextStep(com.arsdigita.kernel.ui.ACSObjectSelectionModel)
		 */
	protected PostTextStep getTextStep(ACSObjectSelectionModel post) {
		return new RootPostTextStep(post, this);
	}
	/* (non-Javadoc)
	 * @see com.arsdigita.forum.ui.PostForm#getPost(com.arsdigita.bebop.PageState)
	 */
	protected Post getPost(PageState state) {
		Post post = getSelectedPost(state);
		if (post == null) {

			post = Post.create(ForumContext.getContext(state).getForum());
			Party party = Kernel.getContext().getParty();
			if (party == null) {
				// anonymous posts MUST be allowed if we have reached here
				party = Kernel.getPublicUser();
			}
			post.setFrom(party);
		}
		
		return post;

	}

}
