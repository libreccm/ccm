/*
 * Copyright (C) 2008-2009 Peter Boy, pboy@zes.uni-bremen.de All Rights Reserved.
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

/*
 *            !!! WORK IN PROGRESS !!!
 *          NOT READY for prime time yet
 */
package com.arsdigita.web;

// import com.arsdigita.runtime.CCMResourceManager;
import com.arsdigita.runtime.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * Web application lifecycle listener, used to perform central initialization
 * tasks at CCM startup in a servlet container / web application server,
 * expecially setting the runtime context (file locations) and (in the future)
 * the database connection.
 *
 * The methods of this classes are by definition only invoked by the servlet
 * container / web application server, not by any servlet or java class of the
 * application itself! Invokation is managed by the deployment descriptor.
 *
 * Note! Don't forget to configure it in web.xml deployment descriptor!
 *   <listener>
 *       <listener-class>
 *           com.arsdigita.runtime.CCMApplicationContextListener
 *       </listener-class>
 *   </listener>
 * According to the 2.3 specification these tags must be placed after the filter
 * tags and before the servlet tags!
 *
 * @author pboy
 */

public class CCMApplicationContextListener implements ServletContextListener {

    private static Logger s_log = Logger.getLogger(CCMApplicationContextListener.class);

    /**
     * Used to initialize classes at startup of the application, most of which
     * needs to be plain java objects (because they are also used by command
     * line interface - installation, configuration, maintenance).
     *
     * Here we provide one of the two supported ways to bring up the CCM
     * application. This handles the startup inside a servlet container. The
     * command line utilities handle the startup there. Both initialize the same
     * set of classes needed for CCM operations
     *
     * @param applicationStartEvent
     */
    public void contextInitialized(ServletContextEvent applicationStartEvent) {
        // throw new UnsupportedOperationException("Not supported yet.");

        String m_appBase ;

        // s_log.setLevel( INFO );


        ServletContext sc = applicationStartEvent.getServletContext();
        m_appBase = sc.getRealPath("/");

        CCMResourceManager.setBaseDirectory(sc.getRealPath("/"));
        s_log.info("CCMApplicationContextListener: BaseDir set to: " + m_appBase );
        s_log.debug("CCMApplicationContextListener done." );
        s_log.fatal("CCMApplicationContextListener: BaseDir set to: " + m_appBase );



    }

    /**
     * Not used yet.
     * 
     * @param applicationEndEvent
     */
    public void contextDestroyed(ServletContextEvent applicationEndEvent) {
        // throw new UnsupportedOperationException("Not supported yet.");

        // do nothing at the moment
    }
}