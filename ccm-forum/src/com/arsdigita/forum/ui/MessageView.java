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
package com.arsdigita.forum.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.forum.Post;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.messaging.Message;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;

/** Displays the details of one message or a Post */

class MessageView extends SimpleComponent implements Constants {
    private static final Logger s_log = Logger.getLogger(MessageView.class);

    private ReplyToPostForm m_container;
    private ACSObjectSelectionModel m_postModel;
    private Post m_post;

    /** For dynamically selected message views */
    public MessageView(ACSObjectSelectionModel postModel, ReplyToPostForm container) {
        m_postModel = postModel;
        m_container = container;
    }

    public MessageView(Post post) {
        m_post = post;
    }

    @Override
    public void register(Page page) {
        super.register(page);
        if (m_postModel != null) {
            page.addComponentStateParam(this, 
                                        m_postModel.getStateParameter());
        }
    }

    @Override
    public void generateXML(PageState state,
                            Element parent) {
        Message post = m_post;
        if (m_post == null) {
            post = (Post)m_postModel.getSelectedObject(state);
        }
        if (m_container.getContext(state).equals(ReplyToPostForm.EDIT_CONTEXT)) {
        	// post in postmodel is the reply being edited, not the parent
        	post = post.getParent();
        }
        
        
        
        Element messageEl = parent.newChildElement(FORUM_XML_PREFIX + ":message", 
                                                   FORUM_XML_NS);
        exportAttributes(messageEl);

        DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(messageEl);
        xr.setWrapRoot(false);
        xr.setWrapAttributes(true);
        xr.setWrapObjects(false);
        
        xr.walk(post, DiscussionPostsList.class.getName());
    }
}

