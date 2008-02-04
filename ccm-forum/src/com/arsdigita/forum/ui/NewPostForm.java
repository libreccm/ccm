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
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.Forum;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * @see com.arsdigita.forum.Post.java 
 * @see com.arsdigita.forum.EditPostForm.java 
 */

class NewPostForm extends PostForm {
    public static final String versionId =
        "$Id: NewPostForm.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(NewPostForm.class);

    private CategoryWidget m_category;

    public NewPostForm() {
        super("newPostForm");
        setupComponent();
    }

    protected Container dataEntryStep() {
        Container initial = super.dataEntryStep();
    
        m_category = new CategoryWidget(new BigDecimalParameter("topic"));
        initial.add(m_category);

        return initial;
    }

    protected Post getPost(PageState state,
                           boolean create) {
        if (!create) {
            return null;
        }
        
        Post post = Post.create(ForumContext.getContext(state).getForum());
        post.setFrom(Kernel.getContext().getParty());
        return post;
    }    

    protected void initWidgets(PageState state,
                               Post post) {
        super.initWidgets(state, post);
    }

    protected void processWidgets(PageState state,
                                  Post post) {
        super.processWidgets(state,
                             post);

        post.clearCategories();
        BigDecimal categoryID = (BigDecimal)m_category.getValue(state);
        if (categoryID != null && 
            !categoryID.equals(Constants.TOPIC_NONE)) {
            
            post.mapCategory(new Category(categoryID));
        }

        ForumContext ctx = ForumContext.getContext(state);
        Forum forum = ctx.getForum();
        if (forum.isModerated() &&
            !ctx.canModerate()) {
            post.setStatus(Post.PENDING);
        } else {
            post.setStatus(Post.APPROVED);
        }
        
        // XXX we shouldn't have to save the post yet 
        // just to create a subscription :-(
        post.save();
        post.createThreadSubscription();

        throw new RedirectSignal(URL.here(state.getRequest(), "/"), true);
    }
}
