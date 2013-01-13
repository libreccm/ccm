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


import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import org.apache.log4j.Logger;

/**
 * <p>Executes nonrecurring at install time and loads (installs and initializes)
 * the HTTP Auth application and type persistently into database.</p>
 *
 * @author Daniel Berrange
 * @version $Id: Loader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Loader extends PackageLoader {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger.getLogger(Loader.class);

    private StringParameter m_adminEmail = new StringParameter
        ("auth.http.admin_email", Parameter.REQUIRED, null);
    private StringParameter m_adminIdent = new StringParameter
        ("auth.http.admin_identifier", Parameter.REQUIRED, null);


    /**
     * Constructor registers parameters
     */
    public Loader() {

        register(m_adminEmail);
        register(m_adminIdent);

        loadInfo();

    }

    /**
     * 
     * @param ctx 
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                setupAdministrator();
                setupHTTPAuth();
            }
        }.run();
    }

    /**
     * Loads HTTPAuth type as a legacy free type of application and 
     * instantiates a (single) default instance.
     */
    private void setupHTTPAuth() {

        /* Create new type legacy free application type                 
         * NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "Auth HTTP" will become "auth-http".                   */
        ApplicationType type = new  ApplicationType("Auth HTTP",
                                                    HTTPAuth.BASE_DATA_OBJECT_TYPE );

        type.setDescription("CCM HTTP authentication administration");
        type.save();

        Application admin = Application.retrieveApplicationForPath("/admin/");

        Application app = Application
                          .createApplication(type,
                                             "auth-http",
                                             "CCM HTTP Authentication Admin",
                                             admin);
        app.save();
    }

    /**
     * 
     */
    private void setupAdministrator() {

        s_log.info("Administrator eMail is retrieved as: " + getAdminEmail());

        DataCollection coll = SessionManager.getSession()
                                            .retrieve(User.BASE_DATA_OBJECT_TYPE);
        coll.addEqualsFilter( "primaryEmail", getAdminEmail() );

        if (!coll.next()) {
            coll.close();
            throw new IllegalStateException
                ( "administratorEmail for HTTP Authentication doesn't " +
                  "specify an existing user." );
        }

        User admin = User.retrieve( coll.getDataObject() );
        coll.close();
        s_log.info("Administrator is retrieved as: " + admin);
        
        UserLogin login = UserLogin.findByUser(admin);
        if (login == null) {
            login = UserLogin.create(admin, getAdminIdentifier());
            login.save();
        }
    }

    /**
     * 
     * @return 
     */
    private String getAdminEmail() {
        return (String) get(m_adminEmail);
    }

    /**
     * 
     * @return 
     */
    private String getAdminIdentifier() {
        return (String) get(m_adminIdent);
    }


}
