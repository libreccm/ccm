/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Creates a list of defined (available) topics as a GUI component. Currently
 * invoked by TopicsPanel only.
 *
 * XXX: Forum knows about <i>threads</i> which groups a set of posts to the same
 * subject, and <i>topics</i> which group a set of threads about the same general
 * theme. Currently Forum uses <i>catgegory</i> as synonym for topic, which may be
 * misleading in some contexts, because there is <i>forum-categorized</i> which
 * uses category in the usual CMS way, esp. navigation categories.
 *
 */
public class TopicsList extends SimpleComponent implements Constants {

    /** List of properties a topic may have. */
    private final static Set s_catProps;
    static {
        s_catProps = new HashSet();
        s_catProps.add("id");
        s_catProps.add("name");
        s_catProps.add("numThreads");
        s_catProps.add("latestPost");
    }
    
    /**
     * 
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(PageState state,
                            Element parent) {

        // Header of list is constructed in xsl, no globalization here
        // Globalization has currently to be done in theme.
        Element content = parent.newChildElement(FORUM_XML_PREFIX + ":topicList", 
                                                 FORUM_XML_NS);
        exportAttributes(content);
        
        URL url = URL.request(state.getRequest(), null);
        content.addAttribute("baseURL", url.toString());
        content.addAttribute("param", TOPIC_PARAM);

        // Get handle to current forum instance
        Forum forum = ForumContext.getContext(state).getForum();
        // Get categories for the forum instance
        DataQuery categories = forum.getCategories();
        // Generate xml for the retrieved topics (categories)
        generateQueryXML(content, categories);

        DataQuery unCategory = forum.getUnCategory();
        while (unCategory.next()) {
            Element noTopic = content.newChildElement(FORUM_XML_PREFIX +
                                                      ":noTopicSummary",
                                                      FORUM_XML_NS);

            Element id = noTopic.newChildElement("id");
            id.setText(XML.format(TOPIC_NONE));

            Element latestPost = noTopic.newChildElement("latestPost");
            latestPost.setText(XML.format(unCategory.get("latestPost")));

            Element numThreads = noTopic.newChildElement("numThreads");
            numThreads.setText(XML.format(unCategory.get("numThreads")));
        }
    }    
    
    
    /**
     * 
     * @param parent
     * @param query
     */
    public void generateQueryXML(Element parent,
                                 DataQuery query) {
        while (query.next()) {
            Element content = parent.newChildElement(FORUM_XML_PREFIX +
                                                     ":topicSummary",
                                                     FORUM_XML_NS);

            Iterator keys = s_catProps.iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                Object value = query.get(key);
                if (value == null) {
                    continue;
                }

                Element attr = content.newChildElement(key);
                attr.setText(XML.format(value));
            }
        }
    }
}

