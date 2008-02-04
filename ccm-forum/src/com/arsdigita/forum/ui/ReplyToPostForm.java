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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Forum;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import org.apache.log4j.Logger;

/**
 * Class ReplyToPostForm
 *
 * @author Jon Orris (jorris@arsdigita.com)
 *
 * @version $Revision #1 $DateTime: 2004/08/17 23:26:27 $
 */
public class ReplyToPostForm extends PostForm  {
    public static final String versionId =
        "$Id: ReplyToPostForm.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger
        (ReplyToPostForm.class);


    private ACSObjectSelectionModel m_parent;


    public ReplyToPostForm(ACSObjectSelectionModel parent) {
        super("replyPostForm");
        m_parent = parent;
        setupComponent();
    }

    public void register(Page p) {
        super.register(p);
        
        p.addGlobalStateParam(m_parent.getStateParameter());
    }


    protected Post getPost(PageState state,
                           boolean create) {
        if (create) {
            Post post = (Post)((Post)m_parent.getSelectedObject(state)).replyTo();
            post.setFrom(Kernel.getContext().getParty());
            return post;
        }
        return null;
   }

    protected Container dataEntryStep() {
        Container entryStep =  super.dataEntryStep();
        MessageView replyTo = new MessageView(m_parent);
        entryStep.add(replyTo);

        return entryStep;
    }
    
    protected void initWidgets(PageState state,
                               Post post) {
        super.initWidgets(state, post);

        Post parent = (Post)m_parent.getSelectedObject(state);
        String prefix  = "Re:";
        String subject = parent.getSubject();
        
        if (subject.length() < 3 ||
            prefix.equalsIgnoreCase(subject.substring(0,3))) {
            setSubject(state, subject);
        } else {
            setSubject(state, prefix + " " + subject);
        }
    }
    
    protected void processWidgets(PageState state,
                                  Post post) {
        super.processWidgets(state, post);

        ForumContext ctx = ForumContext.getContext(state);
        Forum forum = ctx.getForum();

        if (forum.isModerated() &&
            !ctx.canModerate()) {
            post.setStatus(Post.PENDING);
        } else {
            post.setStatus(Post.APPROVED);
        }

        post.setRefersTo(forum);
    }

}
