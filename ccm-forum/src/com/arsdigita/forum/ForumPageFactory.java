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
package com.arsdigita.forum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.arsdigita.bebop.Page;
// import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * Factory class that enables projects to provide their own page creators. 
 * A reason for doing this is that a particular forum project may wish to 
 * include components on the page that introduce dependencies on other projects
 *
 * @author chris.gilbert@westsussex.gov.uk
 */
public class ForumPageFactory {

    private static final Logger logger = Logger.getLogger(ForumPageFactory.class);
    public static final String THREAD_PAGE = "thread";
    public static final String FORUM_PAGE = "forum";
    private static Map pageBuilders = new HashMap();

    static {
        logger.debug("Static initalizer starting...");
        // default pageBuilders are those provided with this project
        pageBuilders.put(THREAD_PAGE, new ThreadPageBuilder());
        pageBuilders.put(FORUM_PAGE, new ForumPageBuilder());
        logger.debug("Static initalizer finished.");
    }

    public static Page getPage(String pageType) {
        Assert.isTrue(pageBuilders.containsKey(pageType),
                      "Requested page type (" + pageType
                      + ") does not have a builder registered");
        PageBuilder builder = (PageBuilder) pageBuilders.get(pageType);
        Page page = builder.buildPage();
        page.lock();
        return page;


    }

    public static Iterator getPages() {
        return pageBuilders.keySet().iterator();
    }

    public static void registerPageBuilder(String pageType, PageBuilder builder) {
        pageBuilders.put(pageType, builder);

    }
}
