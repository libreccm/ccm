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

package com.arsdigita.formbuilder.ui.editors;

import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.actions.ConfirmRedirectListener;
import com.arsdigita.formbuilder.util.GlobalizationUtil; 

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.URLParameter;

import java.math.BigDecimal;

public class ConfirmRedirectForm extends ProcessListenerForm {
    private TextField m_url;

    public ConfirmRedirectForm(String name,
                               SingleSelectionModel form,
                               SingleSelectionModel action) {
        super(name, form, action);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_url = new TextField(new URLParameter("url"));
        m_url.setSize(50);
        m_url.addValidationListener(new NotNullValidationListener());
        m_url.addValidationListener(new StringInRangeValidationListener(1, 160));
        m_url.addValidationListener(new ParameterListener() {
            // Ensure that the URL is either fully qualified, or an absolute
            // path.
            public void validate(ParameterEvent ev)
                throws FormProcessException
            {
                ParameterData data = ev.getParameterData();
                String value = (String) data.getValue();

                if (null == value) return;
                if (value.indexOf("://") != -1) return;
                if (value.startsWith("/")) return;

                data.addError("URL must be either fully qualified, eg http://www.google.co.uk/, or an absolute path, eg /ccm/portal. Relative paths, eg admin/index.jsp, are not allowed.");
            }
        });


        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.url")), ColumnPanel.RIGHT);
        section.add(m_url);
    }


    protected PersistentProcessListener getProcessListener() {
        return new ConfirmRedirectListener();
    }

    protected PersistentProcessListener getProcessListener(BigDecimal id) {
        return new ConfirmRedirectListener(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentProcessListener listener)
        throws FormProcessException {
        super.initWidgets(e, listener);

        ConfirmRedirectListener l = (ConfirmRedirectListener)listener;

        PageState state = e.getPageState();

        if (l == null) {
            m_url.setValue(state, "");
        } else {
            m_url.setValue(state, l.getUrl());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentProcessListener listener)
        throws FormProcessException {
        super.processWidgets(e, listener);

        ConfirmRedirectListener l = (ConfirmRedirectListener)listener;

        FormData data = e.getFormData();
        String url = (String)data.get("url");
        l.setUrl(url);
    }
}
