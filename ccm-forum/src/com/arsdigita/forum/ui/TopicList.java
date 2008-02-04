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


public class TopicList extends SimpleComponent implements Constants {

    private static Set s_catProps;
    static {
        s_catProps = new HashSet();
        s_catProps.add("id");
        s_catProps.add("latestPost");
        s_catProps.add("numThreads");
        s_catProps.add("name");
    }
    
    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement("forum:topicList", 
                                                 FORUM_XML_NS);
        exportAttributes(content);
        
        URL url = URL.request(state.getRequest(), null);
        content.addAttribute("baseURL", url.toString());
        content.addAttribute("param", TOPIC_PARAM);

        Forum forum = ForumContext.getContext(state).getForum();
        DataQuery categories = forum.getCategories();
        generateQueryXML(content, categories);

        DataQuery unCategory = forum.getUnCategory();
        while (unCategory.next()) {
            Element noTopic = content.newChildElement("forum:noTopicSummary", 
                                                      FORUM_XML_NS);

            Element id = noTopic.newChildElement("id");
            id.setText(XML.format(TOPIC_NONE));

            Element latestPost = noTopic.newChildElement("latestPost");
            latestPost.setText(XML.format(unCategory.get("latestPost")));

            Element numThreads = noTopic.newChildElement("numThreads");
            numThreads.setText(XML.format(unCategory.get("numThreads")));
        }
    }    
    
    
    public void generateQueryXML(Element parent,
                                 DataQuery query) {
        while (query.next()) {
            Element content = parent.newChildElement("forum:topicSummary", 
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

