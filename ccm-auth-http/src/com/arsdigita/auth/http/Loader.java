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
 */

package com.arsdigita.auth.http;

import com.arsdigita.loader.PackageLoader;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.runtime.ScriptContext;

import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Loads the HTTP Auth application and type
 *
 * @author Daniel Berrange
 * @version $Id: Loader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    private StringParameter m_adminEmail = new StringParameter
        ("auth.http.admin_email", Parameter.REQUIRED, null);
    private StringParameter m_adminIdent = new StringParameter
        ("auth.http.admin_identifier", Parameter.REQUIRED, null);


    public Loader() {
        register(m_adminEmail);
        register(m_adminIdent);
        loadInfo();
    }
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupAdministrator();
                setupHTTPAuth();
            }
        }.run();
    }

    private void setupHTTPAuth() {
        ApplicationType type = ApplicationType
            .createApplicationType("auth-http",
                                   "CCM HTTP Authentication Admin",
                                   HTTPAuth.BASE_DATA_OBJECT_TYPE);
        type.setDescription("CCM HTTP authentication administration");

        Application admin = Application.retrieveApplicationForPath("/admin/");

        Application app =
            Application.createApplication(type,
                                          "auth-http",
                                          "CCM HTTP Authentication Admin",
                                          admin);
    }

    private void setupAdministrator() {
        DataCollection coll = SessionManager.getSession().retrieve
            ( User.BASE_DATA_OBJECT_TYPE );
        coll.addEqualsFilter( "primaryEmail", getAdminEmail() );

        if (!coll.next()) {
            coll.close();
            throw new IllegalStateException
                ( "administratorEmail for HTTP Authentication doesn't " +
                  "specify an existing user." );
        }

        User admin = User.retrieve( coll.getDataObject() );
        coll.close();
        
        UserLogin login = UserLogin.findByUser(admin);
        if (login == null) {
            login = UserLogin.create(admin, getAdminIdentifier());
            login.save();
        }
    }

    private String getAdminEmail() {
        return (String) get(m_adminEmail);
    }

    private String getAdminIdentifier() {
        return (String) get(m_adminIdent);
    }


}
