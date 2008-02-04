/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel.security;

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import org.apache.log4j.Logger;

/**
 * Manages a string value stored in a URL parameter.
 *
 * @see URLLoginModule
 *
 * @author Sameer Ajmani
 **/
public class URLManager extends CredentialManager {

    public static final String versionId = "$Id: URLManager.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private static final Logger s_log =
        Logger.getLogger(URLManager.class.getName());

    private Subject m_subject;
    private Set m_models;

    /**
     * Creates a URLManager that manages the URL parameters specified by the
     * given set of bebop <code>StringParameters</code>.
     *
     * @param models a set of bebop <code>StringParameters</code>
     **/
    public URLManager(Set models) {
        m_models = models;
    }

    /**
     * Adds the <code>StringParameters</code> provided in the constructor to
     * the Subject's set of public credentials.  Allows the calling code to
     * determine what URL parameters might be set by this URLManager.
     **/
    public void initialize(CredentialLoginModule module,
                           Subject subject,
                           CallbackHandler handler,
                           Map shared,
                           Map options) {
        super.initialize(module, subject, handler, shared, options);
        m_subject = subject;
        // add the parameter models to the Subject's credentials
        m_subject.getPublicCredentials().addAll(getModels());
    }

    /**
     * Ensures that <code>setValue()</code> is called for every commit.
     *
     * @return <code>true</code>.
     *
     * @throws LoginException if an error occurs.
     **/
    protected boolean shouldSetValue(String value)
        throws LoginException {
        return true;
    }

    /**
     * Extracts the parameter named
     * <code>getModule().getCredentialName()</code> from the current HTTP
     * request.
     *
     * @return the value of the URL parameter.
     *
     * @throws LoginException if an error occurs.
     **/
    protected final String getValue()
        throws LoginException {
        s_log.debug("START getValue");
        String value = getModule().getRequest()
            .getParameter(getModule().getCredentialName());
        if (value == null) {
            s_log.debug("FAILURE getValue");
            throw new CredentialNotFoundException();
        }
        s_log.debug("SUCCESS getValue: "+value);
        return value;
    }

    /**
     * Adds a URL parameter that authenticates the user to this Subject's
     * public credentials.  The URL paramater is a bebop
     * <code>ParameterData</code> whose <code>ParameterModel</code> is one
     * of the models provided in the constructor.
     *
     * @throws LoginException if an error occurs.
     **/
    protected final void setValue(String value)
        throws LoginException {
        m_subject.getPublicCredentials().add
            (new ParameterData(getModel(), value));
    }

    /**
     * Removes all public credentials in this Subject that are
     * <code>ParameterData</code> with name
     * <code>getModule().getCredentialName()</code>.
     *
     * @throws LoginException if an error occurs.
     **/
    protected final void deleteValue()
        throws LoginException {
        String name = getModule().getCredentialName();
        Iterator creds = m_subject.getPublicCredentials().iterator();
        while (creds.hasNext()) {
            Object o = creds.next();
            if (o instanceof ParameterData) {
                ParameterData p = (ParameterData)o;
                if (p.getName().equals(name)) {
                    // remove the credential
                    creds.remove();
                }
            }
        }
    }

    /**
     * Returns the set of bebop <code>StringParameters</code> provided in
     * the constructor.
     *
     * @return the set of bebop <code>StringParameters</code> provided in
     * the constructor.
     **/
    private Set getModels() {
        return m_models;
    }

    /**
     * Returns the <code>StringParameter</code> for the URL parameter to set
     * in <code>setValue()</code>.
     *
     * @return the model named <code>getModule().getCredentialName()</code>.
     *
     * @throws LoginException if an error occurs.
     **/
    private StringParameter getModel()
        throws LoginException {
        String name = getModule().getCredentialName();
        for (Iterator i = m_models.iterator(); i.hasNext();) {
            StringParameter model = (StringParameter)i.next();
            if (name.equals(model.getName())) {
                return model;
            }
        }
        throw new IllegalStateException
            ("Unrecognized parameter name: "+name);
    }
}
