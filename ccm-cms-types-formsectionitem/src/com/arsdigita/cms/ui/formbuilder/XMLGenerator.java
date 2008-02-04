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
package com.arsdigita.cms.ui.formbuilder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.xml.Element;


public class XMLGenerator extends SimpleXMLGenerator {

    public void generateXML(PageState state,
                            Element parent,
                            String useContext) {
        ContentItem item = getContentItem(state);

        try {
            // XXX bad OO access to parent attribute
            String defClass = (String)item.get("defaultDomainClass");
            if (!item.getClass().getName().equals(defClass)) {
                item = (ContentItem)FormBuilderUtil.instantiateObjectOneArg(defClass, item.getID());
            }

            com.arsdigita.cms.dispatcher.XMLGenerator generator =
                (com.arsdigita.cms.dispatcher.XMLGenerator)item;

            generator.generateXML(state, parent, useContext);
        } catch (ClassCastException ex) {
            super.generateXML(state, parent, useContext);
        }
    }
}
