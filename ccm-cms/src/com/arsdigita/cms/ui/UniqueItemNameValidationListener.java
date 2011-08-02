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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.ui.util.UniqueStringValidationListener;


/**
 * Ensures that the name of the item is unique by resolving the
 * would-be URL of the item. If an item already "exists" at the URL in the
 * current context, then the name is invalid.
 *
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: UniqueItemNameValidationListener.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class UniqueItemNameValidationListener
    extends UniqueStringValidationListener {

    private final static String ERROR_MSG =
        "An item with this name already exists.";

    /**
     * Constructor.
     *
     * @param widget The widget that contains the name of the item.
     * @pre ( widget != null )
     */
    public UniqueItemNameValidationListener(Widget widget) {
        this(widget, ERROR_MSG);
    }

    /**
     * Constructor.
     *
     * @param widget The widget that contains the name of the item.
     * @param errorMsg The error message
     * @pre ( widget != null )
     */
    public UniqueItemNameValidationListener(Widget widget, String errorMsg) {
        super(widget, errorMsg);
    }


    /**
     * Returns true if the string value is unique, false otherwise.
     *
     * @param state The page state
     * @param value The submitted string value
     * @return true if the string value is unique, false otherwise
     */
    protected boolean isUnique(PageState state, String value) {

        // Fetch the current content section.
        ContentSection section = getContentSection(state);

        ItemResolver resolver = section.getItemResolver();

        // Resolve the would-be URL to a content item.
        String context = resolver.getCurrentContext(state);
        ContentItem item = resolver.getItem(section, value, context);

        return ( item != null );
    }

    /**
     * Fetch the current content section.
     *
     * @param state The page state
     * @return The current content section
     * @pre ( state != null )
     */
    protected ContentSection getContentSection(PageState state) {
        ContentSection section = CMS.getContext().getContentSection();
        return section;
    }

}
