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
package com.arsdigita.forum.categorised;


import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumPageFactory;
import com.arsdigita.kernel.URLService;

import org.apache.log4j.Logger;

/**
 * The forum-categorized initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java,v 1.1 2006/03/03 10:54:45 cgyg9330 Exp $
 */
public class Initializer extends CompoundInitializer {

    private static final Logger s_log = Logger.getLogger(Initializer.class);

    @Override
    public void init(DomainInitEvent e) {

        super.init(e);
        ForumPageFactory.registerPageBuilder(
                ForumPageFactory.FORUM_PAGE,
                new CategorisedForumPageBuilder());
        ForumPageFactory.registerPageBuilder(
                ForumPageFactory.THREAD_PAGE,
                new CategorisedThreadPageBuilder());
        ForumPageFactory.registerPageBuilder(
                "load-cat.jsp",
                new CategorySubtreePageBuilder());
        URLService.registerFinder(
                Forum.BASE_DATA_OBJECT_TYPE,
                new ForumURLFinder());

    }

}
