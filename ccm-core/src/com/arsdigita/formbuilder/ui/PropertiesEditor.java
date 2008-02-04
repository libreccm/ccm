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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;

import org.apache.log4j.Logger;

public class PropertiesEditor extends SimpleContainer {

    private static final Logger s_log =
        Logger.getLogger( PropertiesEditor.class );

    PropertiesForm m_form;
    String m_app;

    public PropertiesEditor(PropertiesForm form) {
        m_form = form;

        add(m_form);
    }

    public void setApplication(String app) {
        m_app = app;
        m_form.setApplication(app);
    }

    public String getApplication() {
        return m_app;
    }

    public PropertiesForm getPropertiesForm() {
        return m_form;
    }

    public boolean isComplete(PageState state) {
        return m_form.isComplete(state);
    }

    public boolean isCancelled(PageState state) {
        return m_form.isCancelled(state);
    }

    public void addProcessListener(FormProcessListener l) {
        m_form.addProcessListener(new PropertiesFormProcessListener(l));
    }

    public void addSubmissionListener(FormSubmissionListener l) {
        m_form.addSubmissionListener(new PropertiesFormSubmissionListener(l));
    }

    protected class PropertiesFormProcessListener implements FormProcessListener {
        FormProcessListener m_l;

        public PropertiesFormProcessListener(FormProcessListener l) {
            m_l = l;
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "PropertiesFormProcessListener.process(): " +
                              m_l.getClass().getName() );
            }

            m_l.process(new FormSectionEvent(PropertiesEditor.this,
                                             e.getPageState(),
                                             e.getFormData()));
        }
    }

    protected class PropertiesFormSubmissionListener implements FormSubmissionListener {
        FormSubmissionListener m_l;

        public PropertiesFormSubmissionListener(FormSubmissionListener l) {
            m_l = l;
        }

        public void submitted(FormSectionEvent e)
            throws FormProcessException {

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "PropertiesFormSubmissionListener.submitted(): " +
                             m_l.getClass().getName() );
            }

            m_l.submitted(new FormSectionEvent(PropertiesEditor.this,
                                               e.getPageState(),
                                               e.getFormData()));
        }
    }
}
