/*
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

import com.arsdigita.kernel.Kernel;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.GenericInitializer;
import com.arsdigita.util.URLRewriter;

import java.util.Arrays;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
//import java.util.Map;

import javax.security.auth.login.LoginException;
//import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Initializes the kernel security subpackage.
 *
 * @author pboy (pboy@barkhof.uni-bremen.de)
 */
public class Initializer extends GenericInitializer {

    // Creates a s_logging category with name = to the full name of class
    public static final Logger s_log = Logger.getLogger(Initializer.class);

    private static SecurityConfig s_conf = Kernel.getSecurityConfig();

    /**
     * Implementation of the {@link Initializer#init(DomainInitEvent)}
     * method.
     *
     *
     * @param evt The domain init event.
     */
    public void init(DomainInitEvent evt) {
        s_log.debug("kernel security domain init begin.");

        // Steps carried over from the old style initializer / enterprise.ini
        
        // Step 1:
        // Add the security package' parameter provider to the list of URL
        // parameters to enable cookielesss login.
        URLRewriter.addParameterProvider(new SecurityParameterProvider());

        // Step 2:
        // TODO: Implement an equivalant to loadExcludedExtensions() of
        // LegacyInitializer to set the list of ExludecExtensions in Util!
        // Avoid any reference to the config object in Util.

        // Step 3:
        // Set the SecurityHelper class to be used.
        try {

            Class theClass = s_conf.getSecurityHelperClass();
            Util.setSecurityHelper(theClass.newInstance());

        } catch (InstantiationException e) {
            throw new ConfigError(
                  "Class: " + s_conf.getSecurityHelperClass().getName()
                 +" is not concrete or lacks no-arg constructor: " );
        } catch (IllegalAccessException e) {
            throw new ConfigError(
                  "Class: " + s_conf.getSecurityHelperClass().getName()
                 +" is not public or lacks public constructor: " + e.toString() );
        }

        // Step 4:
        // LoadPageMap / handling of URL's. Not an initializer task.
        // Has to be handled anywhere else (e.g. com arsdigita.ui)

        // Step 5:
        loadLoginConfig();

        s_log.debug("kernel security domain init completed");
    }


    //  //////////////////////////////////////////////////////////
    //
    //  Helper methods
    //
    // ///////////////////////////////////////////////////////////

    private void loadLoginConfig() throws ConfigError {
        javax.security.auth.login.Configuration
                      .setConfiguration(getLoginConfig());

        checkLoginConfig();
    }

    private javax.security.auth.login.Configuration getLoginConfig()
        throws ConfigError {
        SecurityConfig conf = Kernel.getSecurityConfig();
        List loginConfig = Arrays.asList(conf.getLoginConfig());
        return new LoginConfig(loginConfig);
    }

    private void checkLoginConfig() throws ConfigError {
        // check the login configurations
        String[] contexts = new String[] {
            UserContext.REQUEST_LOGIN_CONTEXT,
            UserContext.REGISTER_LOGIN_CONTEXT
        };
        for (int i = 0; i < contexts.length; i++) {
            try {
                new LoginContext(contexts[i]);
            } catch (LoginException e) {
                throw new ConfigError
                    ("Could not instantiate login context '"+contexts[i]+"'.  "
                     +"Check that it is defined in your login configuration.");
            }
        }
    }

}
