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
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mail.Mail;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLEmailListener extends PersistentProcessListener {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.actions.XMLEmailListener";

    public static final String TO = "recipient";
    public static final String SUBJECT = "subject";

    private ThreadLocal s_dbFactory = new ThreadLocal() {
        protected Object initialValue() {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setCoalescing( true );
            f.setNamespaceAware( false );
            f.setValidating( false );

            return f;
        }
    };

    public XMLEmailListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public XMLEmailListener(String typeName) {
        super(typeName);
    }

    public XMLEmailListener(ObjectType type) {
        super(type);
    }

    public XMLEmailListener(DataObject obj) {
        super(obj);
    }

    public XMLEmailListener(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public XMLEmailListener(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }


    public static XMLEmailListener create(String name,
                                          String description,
                                          String to,
                                          String subject) {
        XMLEmailListener l = new XMLEmailListener();
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
        return new XMLEmailProcessListener(getTo(),
                                           getSubject());
    }

    private class XMLEmailProcessListener implements FormProcessListener {
        String m_to;
        String m_subject;

        public XMLEmailProcessListener(String to,
                                       String subject) {
            m_to = to;
            m_subject = subject;
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            Placeholders p = new Placeholders(getForm(), e.getFormData());

            String to = p.interpolate(m_to);
            String subject = p.interpolate(m_subject);

            String from;
            User user = (User)Kernel.getContext().getParty();
            if( null == user ) {
                from = Mail.getConfig().getDefaultFrom();
            } else {
                from = user.getPrimaryEmail().getEmailAddress();
            }

            DocumentBuilder db;
            try {
                db = ((DocumentBuilderFactory) (s_dbFactory.get())).newDocumentBuilder();
            } catch( ParserConfigurationException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            Document doc = db.newDocument();
            Element formDataE = doc.createElement( "form-data" );
            doc.appendChild( formDataE );

            if( null != user ) {
                Element fromE = doc.createElement( "from" );
                fromE.setAttribute( "email", from );
                formDataE.appendChild( fromE );

                PersonName name = user.getPersonName();

                String givenName = name.getGivenName();
                Element givenNameE = doc.createElement( "givenname" );
                givenNameE.appendChild( doc.createTextNode( givenName ) );
                fromE.appendChild( givenNameE );

                String familyName = name.getFamilyName();
                Element familyNameE = doc.createElement( "familyname" );
                familyNameE.appendChild( doc.createTextNode( familyName ) );
                fromE.appendChild( familyNameE );
            }

            Element fieldListE = doc.createElement( "field-list" );
            formDataE.appendChild( fieldListE );

            FormData data = e.getFormData();
            Iterator keys = data.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                Object value = data.get(key);

                Element fieldE = doc.createElement( "field" );
                fieldListE.appendChild( fieldE );

                Element keyE = doc.createElement( "key" );
                keyE.appendChild( doc.createTextNode( key ) );
                fieldE.appendChild( keyE );

                if( null == value ) continue;

                Class valueClass = value.getClass();

                String type;
                if( valueClass.isArray() ) {
                    type = valueClass.getComponentType().getName();
                } else {
                    type = valueClass.getName();
                }
                fieldE.setAttribute( "type", type );

                if( valueClass.isArray() ) {
                    Object[] values = (Object[]) value;

                    for( int i = 0; i < values.length; i++ ) {
                        Element valueE = doc.createElement( "value" );
                        valueE.appendChild( doc.createTextNode( values[i].toString() ) );
                        fieldE.appendChild( valueE );
                    }
                } else {
                    Element valueE = doc.createElement( "value" );
                    valueE.appendChild( doc.createTextNode( value.toString() ) );
                    fieldE.appendChild( valueE );
                }
            }

            StringWriter body = new StringWriter();
            DOMSource source = new DOMSource( doc );

            Transformer tr;
            try {
                tr = TransformerFactory.newInstance().newTransformer();
            } catch( TransformerConfigurationException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            tr.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
            tr.setOutputProperty( OutputKeys.INDENT, "yes" );

            try {
                tr.transform( source, new StreamResult( body ) );
            } catch( TransformerException ex ) {
                throw new UncheckedWrapperException( ex );
            }

            try {
                Mail message = new Mail(to, from, subject, body.toString());
                message.send();
            } catch (MessagingException ex) {
                throw new UncheckedWrapperException("cannot send message", ex);
            }
        }
    }
}
