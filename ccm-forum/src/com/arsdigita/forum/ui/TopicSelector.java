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

import com.arsdigita.domain.DomainObjectXMLRenderer;

import com.arsdigita.persistence.DataAssociationCursor;

import com.arsdigita.web.URL;

import com.arsdigita.xml.Element;

import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.categorization.Category;

import java.math.BigDecimal;


public class TopicSelector extends SimpleComponent implements Constants {
    
    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement("forum:topicSelector", 
                                                 FORUM_XML_NS);

        URL url = URL.request(state.getRequest(), null);
        
        BigDecimal id = ForumContext.getContext(state).getCategorySelection();
        if (id != null) {
            content.addAttribute("currentTopicID", id.toString());
        }
        content.addAttribute("baseURL", url.toString());
        content.addAttribute("param", TOPIC_PARAM);
        content.addAttribute("anyTopicID", TOPIC_ANY.toString());
        content.addAttribute("noTopicID", TOPIC_NONE.toString());

        Forum forum = ForumContext.getContext(state).getForum();
        DataAssociationCursor cursor =
            forum.getFilledCategories();
        
        while (cursor.next()) {
            Category c = new Category(cursor.getDataObject());
            Element topicEl = content.newChildElement("forum:topic", FORUM_XML_NS);
            DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(topicEl);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);
            
            xr.walk(c, TopicSelector.class.getName());
        }
    }    
}

