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
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.MetaObject;
import com.arsdigita.formbuilder.MetaObjectCollection;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.log4j.Logger;

public abstract class MetaObjectProperties extends SimpleContainer {
    private HashMap m_widget_map;
    private HashMap m_id_map;
    private String m_app;
    private Class m_type;

    private static final Logger s_log =
        Logger.getLogger( MetaObjectProperties.class );

    public MetaObjectProperties(String app,
                                Class type) {
        m_type = type;
        m_app = app;

        m_widget_map = new HashMap();
        m_id_map = new HashMap();
    }

    protected void addForms() {
        MetaObjectCollection forms = null;
        try {
            forms = MetaObject.getWidgets(m_app, m_type);
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("cannot find object type", ex);
        }
        while (forms.next()) {
            MetaObject obj = forms.getMetaObject();
            PropertiesEditor f = getPropertiesEditor(obj);
            f.setApplication(m_app);

            m_widget_map.put(obj.getWidgetClassName(), f);
            m_id_map.put(obj.getID(), f);
            add(f);
        }
    }

    public void register(Page p) {
        super.register(p);

        Iterator forms = m_widget_map.values().iterator();
        while (forms.hasNext()) {
            PropertiesEditor f = (PropertiesEditor)forms.next();
            p.setVisibleDefault(f, false);
        }
    }

    public void setFormVisible(PageState state,
                               String widget) {

        Iterator forms = m_widget_map.keySet().iterator();
        while (forms.hasNext()) {
            String className = (String)forms.next();
            PropertiesEditor f = (PropertiesEditor)m_widget_map.get(className);
            f.setVisible(state, className.equals(widget));
        }
    }

    public void setFormVisible(PageState state,
                               BigDecimal widget) {
        Iterator forms = m_id_map.keySet().iterator();
        while (forms.hasNext()) {
            BigDecimal id = (BigDecimal)forms.next();
            PropertiesEditor f = (PropertiesEditor)m_id_map.get(id);
            f.setVisible(state, id.equals(widget));
        }
    }


    public void addCompletionListener(FormCompletionListener l) {
        Iterator forms = m_widget_map.values().iterator();
        while (forms.hasNext()) {
            PropertiesEditor f = (PropertiesEditor)forms.next();

            MetaObjectListener ml = new MetaObjectListener(l, this);

            f.addProcessListener(ml);
            f.addSubmissionListener(ml);
        }
    }

    private class MetaObjectListener implements FormSubmissionListener, FormProcessListener {
        FormCompletionListener m_l;
        Object m_source;

        public MetaObjectListener(FormCompletionListener l,
                                  Object source) {
            m_l = l;
            m_source = source;
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            PropertiesEditor f = (PropertiesEditor)e.getSource();

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "MetaObjectListener.process(): " +
                             f.getClass().getName() );
            }

            if (f.isComplete(e.getPageState())) {
                m_l.complete(new FormSectionEvent(m_source,
                                                  e.getPageState(),
                                                  e.getFormData()));
            }
        }

        public void submitted(FormSectionEvent e)
            throws FormProcessException {

            PropertiesEditor f = (PropertiesEditor)e.getSource();

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "MetaObjectListener.submitted(): " +
                             f.getClass().getName() );
            }

            if (f.isCancelled(e.getPageState())) {
                m_l.complete(new FormSectionEvent(m_source,
                                                  e.getPageState(),
                                                  e.getFormData()));
            }
        }
    }

    protected abstract PropertiesEditor getPropertiesEditor(MetaObject obj);
}
