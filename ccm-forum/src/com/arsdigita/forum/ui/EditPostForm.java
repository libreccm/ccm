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
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Forum;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Class EditPostForm
 *
 * @author Jon Orris (jorris@arsdigita.com)
 *
 * @version $Revision #1 $DateTime: 2004/08/17 23:26:27 $
 */
public class EditPostForm  extends PostForm {

    private static Logger s_log = Logger.getLogger(EditPostForm.class);

    private ACSObjectSelectionModel m_postModel;

    private CategoryWidget m_category;

    public EditPostForm(ACSObjectSelectionModel postModel) {
        super("editPostForm");
        m_postModel = postModel;
        setupComponent();
    }

    protected Container dataEntryStep() {
        Container initial = super.dataEntryStep();
    
        m_category = new CategoryWidget(new BigDecimalParameter("topic"));
        initial.add(m_category);

        return initial;
    }

    public void register(Page p) {
        super.register(p);
        
        p.addGlobalStateParam(m_postModel.getStateParameter());
    }

    protected Post getPost(PageState state,
                           boolean create) {
        return (Post)m_postModel.getSelectedObject(state);
    }

    protected void initWidgets(PageState state,
                               Post post) {
        super.initWidgets(state, post);
    }
    
    protected void processWidgets(PageState state,
                                  Post post) {
        super.processWidgets(state, post);

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
            if (Post.APPROVED.equals(post.getStatus())) {
                post.setStatus(Post.REAPPROVE);
            } else {
                post.setStatus(Post.PENDING);
            }
        } else {
            post.setStatus(Post.APPROVED);
        }

    }

}
