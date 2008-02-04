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
package com.arsdigita.formbuilder.actions;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.PersistentSubmit;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.WidgetLabel;
import com.arsdigita.formbuilder.util.Placeholders;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.mail.Mail;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.Iterator;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

public class SimpleEmailListener extends PersistentProcessListener {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.actions.SimpleEmailListener";

    private static final Logger s_log =
        Logger.getLogger( SimpleEmailListener.class );

    public static final String TO = "recipient";
    public static final String SUBJECT = "subject";

    public SimpleEmailListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SimpleEmailListener(String typeName) {
        super(typeName);
    }

    public SimpleEmailListener(ObjectType type) {
        super(type);
    }

    public SimpleEmailListener(DataObject obj) {
        super(obj);
    }

    public SimpleEmailListener(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SimpleEmailListener(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }


    public static SimpleEmailListener create(String name,
                                             String description,
                                             String to,
                                             String subject) {
        SimpleEmailListener l = new SimpleEmailListener();
        l.setup(name, description, to, subject);
        return l;
    }

    protected void setup(String name,
                         String description,
                         String to,
                         String subject) {
        super.setup(name, description);
        set(TO, to);
        set(SUBJECT, subject);
    }

    // XXX hack to get around some wierd issues
    // with mdsql associations where the object
    // type in question is a subtype of the
    // one named in the association definition
    public boolean isContainerModified() {
        return false;
    }

    public String getTo() {
        return (String)get(TO);
    }

    public void setTo(String to) {
        set(TO, to);
    }

    public String getSubject() {
        return (String)get(SUBJECT);
    }

    public void setSubject(String subject) {
        set(SUBJECT, subject);
    }


    public FormProcessListener createProcessListener() {
        return new SimpleEmailProcessListener(getTo(),
                                              getSubject());
    }

    private class SimpleEmailProcessListener implements FormProcessListener {
        String m_to;
        String m_subject;

        public SimpleEmailProcessListener(String to,
                                          String subject) {
            m_to = to;
            m_subject = subject;
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            FormData data = e.getFormData();

            PersistentFormSection form = getForm();
            Placeholders p = new Placeholders(form, data);

            String subject = p.interpolate(m_subject);
            String to = p.interpolate(m_to);

            String from = (String) p.interpolate("::user.email::");

            if (null == from) {
                User user = (User) Kernel.getContext().getParty();
                if (null == user) {
                    from = Mail.getConfig().getDefaultFrom();
                } else {
                    from = user.getPrimaryEmail().getEmailAddress();
                }
            }

            StringBuffer body = new StringBuffer();

            if (from == null || from.trim().length() == 0) {
                body.append("Anonymous visitor");
                from = Kernel.getSecurityConfig().getAdminContactEmail();
            } else {
                body.append(from);
            }
            body.append(" submitted the following:\n\n");

            String submit = null;

            DataAssociationCursor components = form.getComponents();
            while( components.next() ) {
                PersistentComponent c = (PersistentComponent)
                    DomainObjectFactory.newInstance( components.getDataObject() );

                handleComponent(data, c, body);
            }

            try {
                Mail message = new Mail(to, from, subject, body.toString());
                message.send();
            } catch (MessagingException ex) {
                throw new UncheckedWrapperException("cannot send message", ex);
            }
        }

        private void handleComponent(FormData data, PersistentComponent c,
                                     StringBuffer output) {
            if( c instanceof PersistentWidget ) {
                PersistentWidget w = (PersistentWidget) c;

                if( w instanceof PersistentSubmit ) {
                    if (w.getValue(data) != null)
                        output.append("User clicked ")
                              .append(((PersistentSubmit) w).getButtonLabel());
                } else {
                    WidgetLabel label = WidgetLabel.findByWidget( w );

                    if( null == label )
                        output.append( w.getParameterName() );
                    else
                        output.append( label.getLabel() );
                    
                    output.append( ": " );

                    Object value = w.getValue(data);
                    if( null != value ) {
                        if( value.getClass().isArray() ) {
                            Object[] values = (Object[]) value;

                            for( int i = 0; i < values.length; i++ ) {
                                output.append( values[i].toString() );
                                if( values.length - 1 != i )
                                    output.append( ", " );
                            }
                        } else {
                            output.append( value.toString() );
                        }
                    }

                    output.append( '\n' );
                }
            } else if (c instanceof CompoundComponent) {
                Iterator i = ((CompoundComponent) c).getComponentsIter();
                while (i.hasNext()) {
                    handleComponent(data, (PersistentComponent) i.next(),
                                    output);
                }
            } else if (s_log.isDebugEnabled()) {
                s_log.debug("Ignoring component: " + c.getClass().getName());
            }
        }
    }
}
