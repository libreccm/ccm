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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;
import java.util.Iterator;

/**
 * 
 *
 * @author Justin Ross
 * @version $Id: ItemDetail.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ItemDetail extends SimpleComponent {
    public static final String versionId = "$Id: ItemDetail.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private ItemDetailModelBuilder m_builder;

    public ItemDetail(ItemDetailModelBuilder builder) {
        m_builder = builder;
    }

    public void generateXML(PageState pageState, Element parent) {
        ItemDetailModel model = m_builder.buildModel(pageState);

        Element itemDetail = new Element
            ("bebop:itemDetail", Component.BEBOP_XML_NS);
        parent.addContent(itemDetail);
        itemDetail.addAttribute("title", model.getTitle(pageState));

        generatePropertyXML(pageState, itemDetail, model);

        generateActionXML(pageState, itemDetail, model);
    }

    private void generatePropertyXML(PageState pageState, Element parent,
                                     ItemDetailModel model) {
        Iterator iter = model.getProperties(pageState);

        while (iter.hasNext()) {
            ItemDetailProperty property = (ItemDetailProperty)iter.next();
            property.generateXML(pageState, parent);
        }
    }

    private void generateActionXML(PageState pageState, Element parent,
                                   ItemDetailModel model) {
        Iterator iter = model.getActions(pageState);

        while (iter.hasNext()) {
            ItemDetailAction action = (ItemDetailAction)iter.next();
            action.generateXML(pageState, parent);
        }
    }
}
