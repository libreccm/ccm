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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.util.GlobalizationUtil;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.mail.Mail;

import java.math.BigDecimal;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

public class TemplateEmailListener extends PersistentProcessListener {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.actions.TemplateEmailListener";

    public static final String TO = "recipient";
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";

    public TemplateEmailListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public TemplateEmailListener(String typeName) {
        super(typeName);
    }

    public TemplateEmailListener(ObjectType type) {
        super(type);
    }

    public TemplateEmailListener(DataObject obj) {
        super(obj);
    }

    public TemplateEmailListener(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public TemplateEmailListener(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }


    public static TemplateEmailListener create(String name,
                                               String description,
                                               String to,
                                               String subject,
                                               String body) {
        TemplateEmailListener l = new TemplateEmailListener();
        l.setup(name, description, to, subject, body);
        return l;
    }

    protected void setup(String name,
                         String description,
                         String to,
                         String subject,
                         String body) {
        super.setup(name, description);
        set(TO, to);
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

    public String getBody() {
        return (String)get(BODY);
    }

    public void setBody(String body) {
        set(BODY, body);
    }


    public FormProcessListener createProcessListener() {
        return new TemplateEmailProcessListener(getTo(),
                                                getSubject(),
                                                getBody());
    }

    private class TemplateEmailProcessListener implements FormProcessListener {

	private Logger i_log = Logger.getLogger(TemplateEmailProcessListener.class);
        String m_to;
        String m_subject;
        String m_body;

        public TemplateEmailProcessListener(String to,
                                            String subject,
                                            String body) {
            m_to = to;
            m_subject = subject;
            m_body = body;
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            FormData data = e.getFormData();

            Placeholders p = new Placeholders(getForm(), data);

            String from = (String) p.interpolate("::user.email::");
                if (null == from) {
                User user = (User) Kernel.getContext().getParty();
                if( null == user ) {
                    from = Mail.getConfig().getDefaultFrom();
                } else {
                    from = user.getPrimaryEmail().getEmailAddress();
                }
            }

            String subject = p.interpolate(m_subject);
            String body = p.interpolate(m_body);
            String to = p.interpolate(m_to);

            try {
                Mail message = new Mail(to, from, subject, body);
                message.send();
            } catch (MessagingException ex) {
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "formbuilder.actions.cannot_sent_message"));
            }
        }
    }
}
