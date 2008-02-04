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
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;


/**
 * This class implements the FormProcessListener and FormSubmissionListener
 * The primary function of this class is to reset the ToggleLink to a
 * particular state after a form is processed or submitted.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 */
public class ToggleLinkFormListener
    implements FormProcessListener, FormSubmissionListener {

    public static final String versionId = "$Id: ToggleLinkFormListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private ToggleLink m_toggleLink;
    private boolean m_isSelected;

    /**
     * Default Constructor.  Toggle link will be deselected when form is
     * processed or submitted
     *
     * @param l The toggle Link
     */
    public ToggleLinkFormListener(ToggleLink l) {
        this(l, false);
    }

    /**
     * Constructor.
     *
     * @param l The toggle Link
     * @param isSelected Specify true if the toggle link should be
     *   selected when form is processed or submited, false otherwise.
     */
    public ToggleLinkFormListener(ToggleLink l, boolean isSelected) {
        m_toggleLink = l;
        m_isSelected = isSelected;
    }

    /**
     * Returns the toggle link
     */
    public ToggleLink getToggleLink() {
        return m_toggleLink;
    }

    /**
     * Returns whether the toggle link should be selected after the form
     * is processed.
     */
    public boolean getIsSelected() {
        return m_isSelected;
    }

    /**
     * Set whether the toggle link should be selected after the form
     * is processed.
     *
     * @param isSelected Specify true if the toggle link should be
     *   selected when form is processed or submitted, false otherwise.
     */
    public void getIsSelected(boolean isSelected) {
        m_isSelected = isSelected;
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        //update the state of the toggle link
        m_toggleLink.setSelected(state, m_isSelected);
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        process(e);
    }

}
