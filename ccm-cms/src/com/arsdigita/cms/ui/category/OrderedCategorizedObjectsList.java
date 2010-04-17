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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;

/**
 * This list offers the option for the code to provide the developer
 * with links to sort the given categories.
 *
 * NOTE: This UI currently does not scale well with large numbers of items
 * since it just lists all of them.  It would probably be nice to integrate
 * a paginator as well to as to allow the user to move an item in large
 * distances and to insert an item in the middle.  Right now, when you add
 * an item it is just placed at the end.  However, if you want the item to
 * appear in the middle then you must hit the "up" arrow n/2 times where
 * n is the number of items in the list.  This clearly is not a good setup.
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Revision: #11 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: OrderedCategorizedObjectsList.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class OrderedCategorizedObjectsList extends CategorizedObjectsList {

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(OrderedCategorizedObjectsList.class);

    // It would be really nice if this used the save variable as is
    // used by List but because List has it as private, we cannot do that.
    private static final String SELECT_EVENT = "s";
    private static final String PREV_EVENT = "prev";
    private static final String NEXT_EVENT = "next";

    /**
     *  This just makes a standard
     *  {@link OrderedCategorizedObjectsList}
     */
    public OrderedCategorizedObjectsList(final CategoryRequestLocal category) {
        super(category);
    }


    /**
     *  This geneates the XML as specified by the arguments pass in to
     *  the constructor.
     */
    public void generateXML(PageState state, Element parent) {
        if ( ! isVisible(state) ) {
            return;
        }

        // They want the special sort items
        ListModel m = getModel(state);

        if (!m.next()) {
            super.generateXML(state, parent);
            return;
        }

        // because m.next() returned true, we know there are items
        // in the list
        Element list = parent.newChildElement
            ("cms:orderedCategorizedObjectList", CMS.CMS_XML_NS);
        exportAttributes(list);

        Component c;
        Object selKey = getSelectedKey(state);
        int i = 0;
        boolean hasNext;
        do {
            Element item = list.newChildElement
                (BebopConstants.BEBOP_CELL, BEBOP_XML_NS);
            item.addAttribute("configure", "true");

            String key = m.getKey();
            Assert.exists(key);

            // Converting both keys to String for comparison
            // since ListModel.getKey returns a String
            boolean selected = (selKey != null) &&
                key.equals(selKey.toString());

            if ( selected ) {
                item.addAttribute("selected", "selected");
            }
            state.setControlEvent(this, SELECT_EVENT, key);
            Label l = new Label(m.getElement().toString());
            c = new ControlLink(l);
            c.generateXML(state, item);

            hasNext = m.next();

            // Add attributes containing URLs that fire control events
            // for various portlet actions
            try {
                // Maybe add attribute containing URL for "move up" link
                if (i > 0) {
                    state.setControlEvent(this, PREV_EVENT, key);
                    item.addAttribute("prevURL", state.stateAsURL());
                }

                // Maybe add attribute containing URL for "move down" link
                if (hasNext) {
                    state.setControlEvent(this, NEXT_EVENT, key);
                    item.addAttribute("nextURL", state.stateAsURL());
                }

            } catch (IOException ex) {
                throw new IllegalStateException("Caught IOException: " +
                                                ex.getMessage());
            }
            i++;
        }  while (hasNext);

        state.clearControlEvent();
    }

    public void respond(PageState ps) throws ServletException {
        String event = ps.getControlEventName();

        if (NEXT_EVENT.equals(event) || PREV_EVENT.equals(event)) {
            try {
                ContentItem child =
                    new ContentItem(new BigDecimal(ps.getControlEventValue()));
                final Category parent = getCategory(ps);

                if (NEXT_EVENT.equals(event)) {
                    parent.swapWithNext(child);
                } else {
                    parent.swapWithPrevious(child);
                }
                parent.save();
            } catch (DataObjectNotFoundException e) {
                s_log.error("Trying to create categories with state = " + ps, e);
                throw new ServletException(e);
            }
        } else {
            super.respond(ps);
        }
    }
}
