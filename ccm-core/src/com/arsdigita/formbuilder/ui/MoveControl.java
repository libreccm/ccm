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

import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.WidgetLabel;
import com.arsdigita.formbuilder.ui.BaseAddObserver;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.MetaForm;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class MoveControl extends MetaForm {
    private static final Logger s_log = Logger.getLogger( MoveControl.class );

    private SingleSelectionModel m_form;
    private SingleSelectionModel m_control;

    private ArrayList m_listeners;

    public MoveControl(SingleSelectionModel form,
                       SingleSelectionModel control) {
        super("view_form");

        m_form = form;
        m_control = control;

        m_listeners = new ArrayList();

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();

                String name = state.getControlEventName();
                String value = state.getControlEventValue();

                if (name.equals("move")) {
                    Integer pos = new Integer(value);

                    OID controlOID =
                        new OID( PersistentComponent.BASE_DATA_OBJECT_TYPE,
                                 m_control.getSelectedKey( state ) );
                    PersistentComponent control = (PersistentComponent)
                        DomainObjectFactory.newInstance( controlOID );

                    OID formOID =
                        new OID( PersistentFormSection.BASE_DATA_OBJECT_TYPE,
                                 m_form.getSelectedKey( state ) );
                    PersistentFormSection form = (PersistentFormSection)
                        DomainObjectFactory.newInstance( formOID );

                    WidgetLabel label = null;

                    if (control instanceof PersistentWidget) {
                        label = WidgetLabel.findByWidget((PersistentWidget)control);
                    }

                    if( s_log.isDebugEnabled() ) {
                        StringBuffer buf = new StringBuffer();

                        buf.append( "form: " );
                        buf.append( form.getOID().toString() );

                        buf.append( "\ncontrol: " );
                        buf.append( control.getOID().toString() );

                        buf.append( "\nwidget: " );
                        if( null != label ) {
                            buf.append( label.getOID().toString() );
                        } else {
                            buf.append( "null" );
                        }

                        buf.append( "\nnew pos: " );
                        buf.append( pos );

                        s_log.debug( buf.toString() );
                    }

                    int newPos = pos.intValue();

                    if (label != null)
                        form.removeComponent(label);
                    form.removeComponent(control);

                    if (label != null) {
                        if( 1 == newPos ) {
                            form.addComponent( label, newPos );
                            newPos++;
                        } else {
                            form.addComponent(label, newPos - 1);
                        }
                    }
                    form.addComponent(control, newPos);
                }
            }
        });
    }

    public Form buildForm(PageState state) {
        OID formOID = new OID( PersistentForm.BASE_DATA_OBJECT_TYPE,
                               m_form.getSelectedKey(state) );
        PersistentFormSection section = (PersistentFormSection)
            DomainObjectFactory.newInstance( formOID );

        BigDecimal control_id = (BigDecimal)m_control.getSelectedKey(state);
        section.setComponentAddObserver(new MoveControlObserver(this, control_id));

        try {
            PersistentForm form = (PersistentForm)section;
            return (Form)form.createComponent();
        } catch (ClassCastException ex) {
            Form form = new Form("view_form", new ColumnPanel(1));//, new BoxPanel(BoxPanel.HORIZONTAL));
            form.add((FormSection)section.createComponent());
            return form;
        }
    }

    public void addActionListener(ActionListener l) {
        m_listeners.add(l);
    }

    public void fireActionEvent(ActionEvent e) {
        Iterator listeners = m_listeners.iterator();
        while (listeners.hasNext()) {
            ActionListener l = (ActionListener)listeners.next();

            l.actionPerformed(e);
        }
    }

    public void respond(PageState state)
        throws javax.servlet.ServletException {
        super.respond(state);

        fireActionEvent(new ActionEvent(this, state));
    }


    private class MoveControlObserver extends BaseAddObserver {
        Component m_handler;
        boolean m_before;
        boolean m_after;
        BigDecimal m_moving;
        HashSet m_seen;

        public MoveControlObserver(Component handler,
                                   BigDecimal moving) {
            m_handler = handler;
            m_before = true;
            m_after = false;
            m_moving = moving;
            m_seen = new HashSet();
        }

        public void beforeAddingComponent(FormSection formSection,
                                          PersistentComponent component,
                                          int componentPosition) {
            super.beforeAddingComponent(formSection,
                                        component,
                                        componentPosition);

            if (component.getID().equals(m_moving))
                m_before = false;

            if (component instanceof WidgetLabel) {
                WidgetLabel label = (WidgetLabel)component;
                BigDecimal id = label.getWidget().getID();
                m_seen.add(id);

                if (id.equals(m_moving)) {
                    addLabel(formSection);
                    m_seen.add(component.getID());
                }
            }

            if (!m_seen.contains(component.getID()) &&
                m_before)
                addLink(formSection, componentPosition);

        }

        public void addingComponent(PersistentComponent persistentComponent,
                                    int componentPosition,
                                    Component component) {
            super.addingComponent(persistentComponent,
                                  componentPosition,
                                  component);
        }

        public void afterAddingComponent(FormSection formSection,
                                         PersistentComponent component,
                                         int componentPosition) {
            super.afterAddingComponent(formSection,
                                       component,
                                       componentPosition);

            if (!(component instanceof WidgetLabel) &&
                m_after) {
                addLink(formSection, componentPosition);
            }

            if (component.getID().equals(m_moving)) {
                m_after = true;
                addLabel(formSection);
            }
        }

        protected void addLabel(FormSection form) {
            Label l = new Label(GlobalizationUtil.globalize("formbuilder.ui.move_it_here"));
            form.add(l, GridPanel.FULL_WIDTH);
        }

        protected void addLink(FormSection form,
                               int row) {
            ControlLink l = new MoveLink("[move it here]", row);
            form.add(l, GridPanel.FULL_WIDTH);
        }

        private class MoveLink extends ControlLink {
            int m_row;

            public MoveLink(String name,
                            int row) {
                super(name);
                m_row = row;
            }

            public void setControlEvent(PageState state) {
                state.setControlEvent(m_handler, "move", (new Integer(m_row)).toString());
            }
        }
    }
}
