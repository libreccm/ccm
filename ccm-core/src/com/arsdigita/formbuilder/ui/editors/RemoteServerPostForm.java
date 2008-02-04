/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui.editors;


import com.arsdigita.formbuilder.util.GlobalizationUtil ; 


import com.arsdigita.formbuilder.actions.RemoteServerPostListener;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.bebop.form.TextField;

import com.arsdigita.bebop.parameters.URLParameter;

import com.arsdigita.domain.DataObjectNotFoundException;



import java.math.BigDecimal;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.ColumnPanel;


public class RemoteServerPostForm extends ProcessListenerForm {

    private static final String REMOTE_URL = "remoteURL";
    private TextField m_remoteURL;

    public RemoteServerPostForm(String name,
                               SingleSelectionModel form,
                               SingleSelectionModel action) {
        super(name, form, action);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_remoteURL = new TextField(new URLParameter(REMOTE_URL));
        m_remoteURL.setSize(50);
        m_remoteURL.addValidationListener(new NotNullValidationListener());
        m_remoteURL.addValidationListener(new StringInRangeValidationListener(1, 160));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.url")), ColumnPanel.RIGHT);
        section.add(m_remoteURL);
    }


    protected PersistentProcessListener getProcessListener() {
        return new RemoteServerPostListener();
    }

    protected PersistentProcessListener getProcessListener(BigDecimal id)
        throws DataObjectNotFoundException {
        return new RemoteServerPostListener(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentProcessListener listener)
        throws FormProcessException {
        super.initWidgets(e, listener);

        RemoteServerPostListener l = (RemoteServerPostListener)listener;

        PageState state = e.getPageState();

        if (l == null) {
            m_remoteURL.setValue(state, "");
        } else {
            m_remoteURL.setValue(state, l.getRemoteURL());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentProcessListener listener)
        throws FormProcessException {
        super.processWidgets(e, listener);
        RemoteServerPostListener l = (RemoteServerPostListener)listener;

        FormData data = e.getFormData();
        l.setRemoteURL((String)data.get(REMOTE_URL));
        l.save();
    }
}
