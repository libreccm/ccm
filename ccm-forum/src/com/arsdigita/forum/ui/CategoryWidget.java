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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.TooManyListenersException;

/**
 * A widget providing a choice of categories for a forum.
 *
 * @author ron@arsdigita.com
 * @author sarah@arsdigita.com
 *
 * @version $Id: CategoryWidget.java 1628 2007-09-17 08:10:40Z chrisg23 $
 */
public class CategoryWidget extends SingleSelect implements Constants {

    public CategoryWidget(ParameterModel categoryParameter) {
        super(categoryParameter);

        try {
            addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState s = e.getPageState();
                        final Forum forum = getForum(s);
						SingleSelect target = (SingleSelect) e.getTarget();
                            
                        // Get categories for this forum
                        if (forum.noCategoryPostsAllowed()) {
                            target.addOption(new Option(
                                    TOPIC_NONE.toString(),
                                    new Label(Text.gz("forum.ui.topic.none"))));
                        }
                        final Category root = forum.getRootCategory();
                        if (root != null) {
                            addCategories(root, target);
                        }
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    /**
     * Adds categories to the SingleSelect widget
     *
     * @param root Root of the category tree
     * @param target SingleSelect element for category names
     */
    private void addCategories(final Category root, final SingleSelect target) {
        CategoryCollection children = root.getChildren();
        try {
            while (children.next()) {
                Category c = children.getCategory();
                target.addOption(new Option
                                 (c.getID().toString(),
                                  c.getName()));

            }
        } finally {
            children.close();
        }
    }

    /**
     * Gets forum from page state
     *
     * @param s The page state
     * @return Forum
     */
    private Forum getForum(final PageState s) {
        Forum forum;
        if (ForumContext.getContext(s).getThreadID() != null ) {
            Post rootPost = (Post)ForumContext.getContext(s).
                getMessageThread().getRootMessage();
            forum = rootPost.getForum();
        } else if (ForumContext.getContext(s).getForum() != null) {
            forum = ForumContext.getContext(s).getForum();
        } else {
            // sanity check
            throw new UncheckedWrapperException
                ("Must be either a forum, or a thread page.");
        }
        return forum;
    }
}
