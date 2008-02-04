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
package com.arsdigita.formbuilder.ui;


import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;

public class PropertiesForm extends Form {
    SaveCancelSection m_buttons;
    String m_app;

    public PropertiesForm(String name) {
        super(name);

        createWidgets();

        addSubmissionListener(new FormSubmissionListener() {
            public void submitted(FormSectionEvent e)
                throws FormProcessException {

                if (m_buttons.getCancelButton().isSelected(e.getPageState()))
                    throw new FormProcessException("Cancel pressed");
            }
        });
    }

    public void setApplication(String app) {
        m_app = app;
    }

    public String getApplication() {
        return m_app;
    }

    protected void createWidgets() {
        addWidgets(this);
        addButtons(this);
    }

    protected void addWidgets(FormSection section) {
    }

    protected void addButtons(FormSection section) {
        m_buttons = new SaveCancelSection();
        section.add(new Label("")); // Padding
        section.add(m_buttons);
    }

    public boolean isComplete(PageState state) {
        return m_buttons.getCancelButton().isSelected(state) ||
            m_buttons.getSaveButton().isSelected(state);
    }

    public boolean isCancelled(PageState state) {
        return m_buttons.getCancelButton().isSelected(state);
    }
}
