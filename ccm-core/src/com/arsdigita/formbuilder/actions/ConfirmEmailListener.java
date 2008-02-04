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

import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.util.Placeholders;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.mail.Mail;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;
import javax.mail.MessagingException;

import org.apache.log4j.Logger;

public class ConfirmEmailListener extends PersistentProcessListener {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.actions.ConfirmEmailListener";

    private static final Logger s_log =
        Logger.getLogger( ConfirmEmailListener.class );

    public static final String FROM = "from";
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";

    public ConfirmEmailListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public ConfirmEmailListener(String typeName) {
        super(typeName);
    }

    public ConfirmEmailListener(ObjectType type) {
        super(type);
    }

    public ConfirmEmailListener(DataObject obj) {
        super(obj);
    }

    public ConfirmEmailListener(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ConfirmEmailListener(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }


    public static ConfirmEmailListener create(String name,
                                              String description,
                                              String from,
                                              String subject,
                                              String body) {
        ConfirmEmailListener l = new ConfirmEmailListener();
        l.setup(name, description, from, subject, body);
        return l;
    }

    protected void setup(String name,
                         String description,
                         String from,
                         String subject,
                         String body) {
        super.setup(name, description);
        set(FROM, from);
        set(SUBJECT, subject);
        set(BODY, body);
    }

    // XXX hack to get around some wierd issues
    // with mdsql associations where the object
    // type in question is a subtype of the
    // one named in the association definition
    public boolean isContainerModified() {
        return false;
    }

    public String getFrom() {
        return (String)get(FROM);
    }

    public void setFrom(String from) {
        set(FROM, from);
    }

    public String getSubject() {
        return (String)get(SUBJECT);
    }

    public void setSubject(String subject) {
        set(SUBJECT, subject);
    }

    public String getBody() {
        return (String)get(BODY);
    }

    public void setBody(String body) {
        set(BODY, body);
    }

    public FormProcessListener createProcessListener() {
        return new ConfirmEmailProcessListener(getFrom(),
                                               getSubject(),
                                               getBody());
    }

    private class ConfirmEmailProcessListener implements FormProcessListener {
        String m_from;
        String m_subject;
        String m_body;

        public ConfirmEmailProcessListener(String from,
                                           String subject,
                                           String body) {
            m_from = from;
            m_subject = subject;
            m_body = body;
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            Placeholders p = new Placeholders(getForm(), e.getFormData());

            String to = (String) p.interpolate("::user.email::");
                if (null == to) {
                User user = (User) Kernel.getContext().getParty();
                if( null == user ) {
                    s_log.info( "User not logged in. Not sending confirmation" );
                } else {
                    to = user.getPrimaryEmail().getEmailAddress();
                }
            }

            String from = p.interpolate(m_from);
            String subject = p.interpolate(m_subject);
            String body = p.interpolate(m_body);

            try {
                Mail message = new Mail(to, from, subject, body);
                message.send();
            } catch (MessagingException ex) {
                throw new FormProcessException("cannot send message", ex);
            }
        }
    }
}
