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
package com.arsdigita.dispatcher;

import com.arsdigita.initializer.Script;
import com.arsdigita.util.ResourceManager;
import java.io.InputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;

/**
 * Bootstraps the ACS by
 * calling the initializers in the enterprise.init file.
 *
 * @author Raphael Schloming (rhs@mit.edu)
 * @version $Revision: #15 $ $Date: 2004/08/16 $ */

public class InitializerServlet extends HttpServlet {

    public final static String versionId = "$Id: InitializerServlet.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Script m_ini = null;
    private static boolean loaded = false;

    /**
     * Starts up the web environment for the ACS by loading the specified
     * initializer script.
     **/
    public void init() throws ServletException {
        if (loaded) {
            System.err.println("Fatal error: Initializer executing multiple " +
                               "times.  Aborting.");
            return;
        } else {
            loaded = true;
        }
        ServletConfig conf = getServletConfig();
        String scriptName = conf.getInitParameter("init");
        //ClassLoader cl = getClass().getClassLoader();
        //InputStream is = cl.getResourceAsStream(scriptName);
        //URL url = cl.getResource(scriptName);

        ResourceManager rm =
            com.arsdigita.util.ResourceManager.getInstance();
        rm.setServletContext(getServletContext());
        InputStream is = rm.getResourceAsStream(scriptName);

        System.out.println("Initialization using: " + scriptName);

        if (is == null) {
            throw new ServletException("Couldn't find " + scriptName);
        }

        try {
            m_ini = new Script(is);
            m_ini.startup();
        } catch (Throwable e) {
            // We positively want to know about any error during
            // initialization, including things like NoClassDefFoundError
            // etc.
            System.err.println("Initialization failed with error " +
                               e.getMessage() + ".");
            e.printStackTrace();
            System.err.println("ERROR: INITIALIZATION FAILED. "+
                               "ACS SYSTEM NOT USEABLE.  PLEASE CORRECT " +
                               "ERRORS ABOVE.");
            throw new UnavailableException(e + "\n" + e.getMessage());
        }

        System.out.println("Initialization finished");
    }

    public void destroy() {
        m_ini.shutdown();
    }
}
