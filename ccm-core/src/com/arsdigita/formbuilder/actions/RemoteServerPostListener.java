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
package com.arsdigita.formbuilder.actions;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.formbuilder.PersistentProcessListener;

import java.math.BigDecimal;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.util.UncheckedWrapperException;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.MalformedURLException;
import org.apache.log4j.Logger;
import java.util.Iterator;
import java.io.DataInputStream;


/**
 *  This is a process listener that takes all of the FormData from
 *  the submission and then sends it to the passed in URL as a POST.
 *  This does not currently handle multi-part data.
 */
public class RemoteServerPostListener extends PersistentProcessListener {
    private static final Logger s_log =
        Logger.getLogger(RemoteServerPostListener.class);


    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.actions.RemoteServerPostListener";

    public static final String REMOTE_URL = "remoteURL";

    public RemoteServerPostListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public RemoteServerPostListener(String typeName) {
        super(typeName);
    }

    public RemoteServerPostListener(ObjectType type) {
        super(type);
    }

    public RemoteServerPostListener(DataObject obj) {
        super(obj);
    }

    public RemoteServerPostListener(BigDecimal id)
        throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public RemoteServerPostListener(OID oid)
        throws DataObjectNotFoundException {
        super(oid);
    }

    public static RemoteServerPostListener create(String name,
                                                 String description,
                                                 String remoteURL) {
        RemoteServerPostListener l = new RemoteServerPostListener();

        l.setup(name, description, remoteURL);

        return l;
    }

    protected void setup(String name,
                         String description,
                         String remoteURL) {
        super.setup(name, description);
        setRemoteURL(remoteURL);
    }

    // XXX hack to get around some wierd issues
    // with mdsql associations where the object
    // type in question is a subtype of the
    // one named in the association definition
    public boolean isContainerModified() {
        return false;
    }


    public String getRemoteURL() {
        return (String)get(REMOTE_URL);
    }

    public void setRemoteURL(String remoteURL) {
        set(REMOTE_URL, remoteURL);
    }

    public FormProcessListener createProcessListener() {
        return new RemoteServerPostProcessListener(getRemoteURL());
    }

    private class RemoteServerPostProcessListener implements FormProcessListener {
        URL m_remoteURL;

        public RemoteServerPostProcessListener(String remoteURL) {
            try {
                m_remoteURL = new URL(remoteURL);
            } catch (MalformedURLException murle) {
                throw new UncheckedWrapperException
                    ("malformed URL " + remoteURL, murle);
            }
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {

            FormData data = e.getFormData();
            Iterator params = data.getParameters().iterator();
            StringBuffer paramBuffer = new StringBuffer();
            while (params.hasNext()) {
                ParameterData paramData = (ParameterData)params.next();
                paramBuffer.append(paramData.getKey()).append("=")
                    .append(paramData.getValue()).append("&");
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("Sending the values [" + paramBuffer.toString() + 
                            "] to the URL " + m_remoteURL.toString());
            }

            try {
                // open the connection
                URLConnection connection = m_remoteURL.openConnection();
                connection.setDoOutput(true);
                DataOutputStream stream =
                    new DataOutputStream(connection.getOutputStream());
                stream.writeBytes(paramBuffer.toString());
                stream.close();

                if (s_log.isDebugEnabled()) {
                    // Get response data.
                    DataInputStream fromStream = 
                        new DataInputStream(connection.getInputStream ());
                    String currentLine = fromStream.readLine();
                    StringBuffer outputBuffer = new StringBuffer();
                    while (currentLine != null) {
                        outputBuffer.append(currentLine);
                        currentLine = fromStream.readLine();
                    }
                    s_log.debug("Output from page " + m_remoteURL + " is: \n" + 
                                outputBuffer.toString());
                    fromStream.close();
                }
            } catch (IOException ioe) {
                s_log.error("Unable to open a connection to " + m_remoteURL +
                            " with parameters " + paramBuffer.toString(), ioe);
            }
        }
    }
}
