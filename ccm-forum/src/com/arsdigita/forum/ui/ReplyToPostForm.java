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
package com.arsdigita.forum.ui;

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Forum;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import org.apache.log4j.Logger;

/**
 * Class ReplyToPostForm
 *
 * @author Jon Orris (jorris@arsdigita.com)
 * @author rewritten by Chris Gilbert
 * @version $Revision #1 $DateTime: 2004/08/17 23:26:27 $
 * @version $Id: ReplyToPostForm.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class ReplyToPostForm extends PostForm  {

	private static final Logger s_log = Logger.getLogger(ReplyToPostForm.class);



	public ReplyToPostForm(ACSObjectSelectionModel post) {
		super("replyPostForm", post);
        setupComponent();

   }


    

        
	protected PostTextStep getTextStep(ACSObjectSelectionModel post) {
		return new ReplyToPostTextStep(post, this);
    }
    
	/* (non-Javadoc)
	 * @see com.arsdigita.forum.ui.PostForm#getPost(com.arsdigita.bebop.PageState)
	 */
	protected Post getPost(PageState state) {
		Post reply;
		if (getContext(state).equals(PostForm.REPLY_CONTEXT)) {
			// post model contains the parent post, reply hasn't been created yet
			Post parent = getSelectedPost(state);
			reply = (Post) parent.replyTo();
			Party party = Kernel.getContext().getParty();
			if (party == null) {
				// anonymous posts MUST be allowed if we have reached here
				party = Kernel.getPublicUser();
			}

			reply.setFrom(party);

        } else {
			// reply is the post in the post model
			reply = getSelectedPost(state);
        }
		return reply;

    }

}
