/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.runtime;

import com.arsdigita.persistence.ConnectionSource;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;

import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Wrapper for Runtime to retain backwards compatibility. Deprecated (Dec. 2009)!
 * Will be removed soon.
 *
 * An entry point initializer for the CCM runtime used to bootstrap the CCM
 * runtime environment into a state where it is safe to perform database I/O.
 * It does this by accessing a persistent list of all the initializers
 * required by the currently loaded packages. This guarantees that any of these
 * packages has the opportunity to load any object-relational metadata and
 * register any domain-data coupling metadata before any database I/O is
 * performed.
 *
 * USAGE:
 * Construct an instance of this class and invoke its run() method.
 * <pre>
 * Startup startup = new Startup();
 * if ( !startup.hasRun() ) {
 *          startup.run();
 *      }
 * </pre>
 *
 * In a servlet container runtime environment this should be done either in the
 * first loaded servlet (BaseServlet in CCM) or in a special application
 * listener servlet invoked by the servlet container at startuo time and before
 * any other operation takes place (CCMApplicationListener).
 *
 * In a command line JVM environment (installation & maintenace procedures) it
 * has to be performed once at the very beginning (e.g. package c.a.packaging)
 * 
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Startup.java 738 2005-09-01 12:36:52Z sskracic $
 * @deprecated  use Runtime instead
 */
public  class Startup extends Runtime {

    private static final Logger s_log = Logger.getLogger(Startup.class);


    // Constructor section

    /**
     * Default startup method, which determines the database connection information
     * and delegates to a detailed constructor.
     * @deprecated  use Runtime() instead
     */
    public Startup() {
        super();
    }

    /**
     * Prepares a startup object with a list of child
     * <code>Initializers</code> to run in order to initialize the
     * runtime. This method relies on a caller-provided connection
     * source.
     *
     * @param source The connection source used to recover the list of
     * initializers from the database; it cannot be null.
     *
     * @deprecated use Runtime.startup(ConnectionSource source) instead
     **/
    public Startup(ConnectionSource source) {
        super(source);
    }


    // Public API


    /**
     * Executes the initialization process using the default session
     * and global metadata root.
     *
     * @see MetadataRoot#getMetadataRoot()
     * @see SessionManager#getSession()
     * @deprecated use Runtime.startup()  instead
     **/
    public final void run() {
        super.startup();
    }

    /**
     * Executes the initialization process.
     *
     * This method is going to move.
     * @deprecated use Runtimestartup(session.init)  instead
     */
    // public static final void run(final Session session,
    public static void run(final Session session,
                           final Initializer init) {

        Runtime.startup(session, init);

    }

    /**
     * Returns whether the run method had already been invoked.
     * 
     * @return true if run() has been invoked
     * @deprecated use Runtime.hasRun()  instead
     */
    public static boolean hasRun() {
        return Runtime.hasRun();
    }

    /**
     * 
     * @param args
     * @throws SQLException
     *
     * @deprecated use Runtime.main(args)  instead
     */
    public static final void main(final String[] args) throws SQLException {

        Runtime.main(args);

    }

}