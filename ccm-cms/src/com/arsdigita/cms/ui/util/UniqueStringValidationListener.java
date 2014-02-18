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
package com.arsdigita.cms.ui.util;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Widget;


/**
 * Validates that the string value of the widget is unique.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: UniqueStringValidationListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class UniqueStringValidationListener
                      implements FormValidationListener {

    private final static String ERROR_MSG =
        "Unique string value constraint violation.";

    private Widget m_widget;
    private String m_errorMsg;


    /**
     * Private constructor.
     */
    private UniqueStringValidationListener() {}

    /**
     * Constructor.
     */
    public UniqueStringValidationListener(Widget widget) {
        this(widget, null);
    }

    /**
     * Constructor.
     *
     * @param widget The form widget
     * @param errorMsg An error message
     * @pre ( widget != null )
     */
    public UniqueStringValidationListener(Widget widget, String errorMsg) {
        m_widget = widget;

        if ( errorMsg == null ) {
            m_errorMsg = ERROR_MSG;
        } else {
            m_errorMsg = errorMsg;
        }
    }

    /**
     * Ensure that the name of the item is unique by resolving the
     * would-be URL of the item. If an item "exists" at the URL already,
     * then the name is invalid.
     *
     * @param event The form section event
     * @pre ( event != null )
     */
    public final void validate(FormSectionEvent event)
        throws FormProcessException {

        PageState state = event.getPageState();

        String value = (String) m_widget.getValue(state);

        if ( !isUnique(state, value) ) {
            throw new FormProcessException(m_errorMsg);
        }

    }

    /**
     * Returns true if the string value is unique, false otherwise.
     *
     * @param state The page state
     * @param value The submitted string value
     * @return true if the string value is unique, false otherwise
     */
    protected abstract boolean isUnique(PageState state, String value);

}
