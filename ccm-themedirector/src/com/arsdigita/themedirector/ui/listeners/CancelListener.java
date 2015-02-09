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
 */

package com.arsdigita.themedirector.ui.listeners;

import com.arsdigita.bebop.PageState;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.toolbox.ui.ModalPanel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.themedirector.util.GlobalizationUtil;

/**
 *  This class is essentially a copy of the CancelListener
 *  inside of ModalPanel.  We could not use the one in ModalPanel
 *  becaue it was protected
 */
public class CancelListener implements FormSubmissionListener {
    private Cancellable m_form;
    private ModalPanel m_modalPanel;
    public CancelListener(Cancellable form, ModalPanel panel) {
        m_form = form;
        m_modalPanel = panel;
    }
    
    public void submitted(FormSectionEvent event) 
        throws FormProcessException {
        PageState state = event.getPageState();
        if (m_form.isCancelled(state)) {
            m_modalPanel.pop(state);
            throw new FormProcessException(GlobalizationUtil.globalize("cancelled"));
        }
    }
}
