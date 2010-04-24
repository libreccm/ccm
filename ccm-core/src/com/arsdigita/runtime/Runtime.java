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

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.ConnectionSource;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DedicatedConnectionSource;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PooledConnectionSource;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDLCompiler;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import com.arsdigita.util.SystemProperties;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * The central entry point initializer for the CCM runtime, used to bootstrap
 * the CCM runtime environment into a state where it is safe to perform
 * database I/O and to shutdown the system by the servlet container or a manager
 * application.
 *
 * It does this by accessing a persistent list of all the initializers required
 * by the currently loaded modules. During startup this guarantees that any of
 * these modules has the opportunity to load any object-relational metadata and
 * register any domain-data coupling metadata before any database I/O is performed.
 * During shutdown it enables all loaded modules to cleanup their runtime
 * resources.
 *
 * USAGE:
 * To startup construct an instance of this class and invoke its startup() method.
 * <pre>
 * Runtime runtime = new Runtime();
 * if ( !runtime.hasRun() ) {
 *          runtime.startup();
 *      }
 * </pre>
 *
 * In a servlet container runtime environment this should be done either in the
 * first loaded servlet (BaseServlet in CCM) or in a special application
 * listener servlet invoked by the servlet container at startup time and before
 * any other operation takes place (CCMApplicationListener in CCM, prefered).
 *
 * In a command line JVM environment (installation & maintenace procedures) it
 * has to be performed once at the very beginning (e.g. package c.a.packaging)
 * 
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: Runtime.java 738 2005-09-01 12:36:52Z sskracic $
 */
public  class Runtime extends CompoundInitializer {

    private static final Logger s_log = Logger.getLogger(Runtime.class);

    private static final String s_model = "com/arsdigita/runtime/Initializer.pdl";
    private static final String INIT = "com.arsdigita.runtime.Initializer";

    private final ConnectionSource m_source;

    private static final Parameter s_init = new StringParameter
        ("waf.runtime.init", Parameter.OPTIONAL, null);

    private static boolean s_hasRun = false;

    // ///////////////////
    // Constructor section
    // ///////////////////

    /**
     * Default instantiation, which determines the database connection
     * information and delegates to a detailed constructor.
     */
    public Runtime() {
        this(new PooledConnectionSource
             (RuntimeConfig.getConfig().getJDBCURL(),
              RuntimeConfig.getConfig().getJDBCPoolSize(),
              RuntimeConfig.getConfig().getJDBCPingInterval()));
        s_log.debug("Default instantiation performed");
    }

    /**
     * Detailed Constructor taking a ConnectionSource to instantiate a
     * Runtime Object.
     *
     * Using the super class it creates a new and empty compound initializer that
     * uses the <code>log</code> Logger to log progress through initialization.
     * Additionally it stores the caller-provided connection source to be able
     * to retrieve the list of initializsers from the database later.
     *
     * @param source The connection source used to recover the list of
     * initializers from the database; it cannot be null.
     **/

    public Runtime(ConnectionSource source) {
        super(s_log);

        s_log.debug("Detailed constructor instantiation performed");

        Assert.exists(source, ConnectionSource.class);
        m_source = source;
    }


    // ///////////////////
    // Public API
    // ///////////////////

    /**
     * Executes the startup process using default parameter values.
     *
     * Because ContextInitEvent can not be detected, only the base initialization
     * (i.e. data init, domain init) is done, not the context specific
     * initialization (i.e. various background threads). This variant is
     * specifically used by command line invocations, e.g. installation or
     * various maintenance tasks which are done outside a servlet container
     * context.
     */
    public final void startup() {
        // run shutdown with ContextCLoseEvent null, i.e. perform a base close
        startup(null);
    }

    /**
     * This variant is especially for unit testing where session and initializer
     * are given by the test task.
     *
     * Because ContextInitEvent can not be detected, only the base initialization
     * (i.e. data init, domain init) is done, not the context specific
     * initialization (i.e. various background threads).
     *
     * @param session
     * @param init
     */
    public static final void startup(final Session session,
                                     final Initializer init) {
        startup(session, init, null);
    }

    /**
     * Executes the initialization process using the default session (this) and
     * global metadata root for a given ContextInitEvent.
     *
     * This variant is specifically used when run in a servlet container context
     * and usually invoked by the ApplicationContextListener class
     * (@see CCMApplicationContextListener).
     *
     * Collects a list of <code>Initializers</code> to run from the database,
     * determines the current session and global metadata root, and delegates
     * to an initialization method..
     *
     * @see MetadataRoot#getMetadataRoot()
     * @see SessionManager#getSession()
     */
    public final void startup(final ContextInitEvent evt) {
        s_log.info("Initializing WAF runtime");

        DbHelper.setDatabase
            (DbHelper.getDatabaseFromURL(RuntimeConfig.
                                         getConfig().getJDBCURL()));
        addRuntimeInitializers();

        final MetadataRoot root = MetadataRoot.getMetadataRoot();
        // XXX It shouldn't be necessary to do this until the legacy
        // init step, but SessionManager.getMetadataRoot depends on
        // the session, and the DomainObjectFactory calls it.
        final Session session = session("default", root);

        startup(session, this, evt);

        s_log.info("Initialization complete");
    }


    /**
     * Initialization worker method.
     *
     * Executes the various initialization tasks.
     *
     * This method is going to move.
     */
    public static final void startup(final Session session,
                                     final Initializer init,
                                     final ContextInitEvent evt) {

        Assert.exists(session, Session.class);
        Assert.exists(init, Initializer.class);

        s_hasRun = true;

        final PDLCompiler compiler = new PDLCompiler();
        final MetadataRoot root = session.getMetadataRoot();

        Assert.exists(root, MetadataRoot.class);

        init.init(new DataInitEvent(compiler));

        compiler.emit(root);

        init.init(new DomainInitEvent(new DomainObjectFactory()));

        init.init(new LegacyInitEvent(session));

        if(evt != null) {
           init.init(evt);
        }

    }


    /**
     * Returns whether the run method had already been invoked.
     * 
     * @return true if run() has been invoked
     */
    public static boolean hasRun() {
        return s_hasRun;
    }


    /**
     * Executes the shutdown process using the default parameter values
     */
    public final void shutdown() {
        // run shutdown with ContextCLoseEvent null, i.e. perform a base close
        shutdown(null);
    }

    /**
     * Executes the shutdown process for a given context using the default session
     * and global metadata root.
     *
     * Collects a list of <code>Initializers</code> to run from the database,
     * determines the current dession and global metadata root, and delegates
     * to an initialization method..
     *
     * @see MetadataRoot#getMetadataRoot()
     * @see SessionManager#getSession()
     */
    public final void shutdown(final ContextCloseEvent evt) {

        s_log.info("Shutting down WAF runtime using defaults");

        s_log.debug("Going to execute MetadataRoot.getMetadataRoot().");
        final MetadataRoot root = MetadataRoot.getMetadataRoot();
        // XXX It shouldn't be necessary to do this until the legacy
        // init step, but SessionManager.getMetadataRoot depends on
        // the session, and the DomainObjectFactory calls it.
        s_log.debug("Going to execute session().");
        final Session session = session("default", root);

        s_log.debug("Going to execute shutdown worker method.");
        shutdown(session, this, evt);


        s_log.info("Shutdown complete");
    }

    public static final void shutdown(final Session session,
                                      final Initializer init) {
        // run shutdown with ContextCLoseEvent null, i.e. perform a base close
        shutdown(session,init,null);
    }

    /**
     * Executes the initialization process.
     *
     * This method is going to move.
     */
    public static final void shutdown(final Session session,
                                      final Initializer init,
                                      final ContextCloseEvent evt) {

        Assert.exists(session, Session.class);
        Assert.exists(init, Initializer.class);

        if (s_hasRun == true) {

            final MetadataRoot root = session.getMetadataRoot();
            Assert.exists(root, MetadataRoot.class);

            s_log.debug("Going to call CompoundInitializer.close()");
            init.close(evt);

            s_log.info("Waiting 60 secs for background threads to terminate");
            try {
                Thread.currentThread().sleep(60000);
            } catch( InterruptedException e) {
                // do nothing
            }
        } else {
            s_log.warn("Shutdown must only be run AFTER startup. Skipped.");
        }

    }

    /**
     * 
     * @param args
     * @throws SQLException
     */
    // public static final void main(final String[] args) throws SQLException {
    public static void main(final String[] args) throws SQLException {
        final String url;
        final int size;
        if (args.length == 0) {
            url = RuntimeConfig.getConfig().getJDBCURL();
        } else {
            url = args[0];
        }

        new Runtime(new DedicatedConnectionSource(url)).startup();
    }

    // ////////////////////////////////
    // Private section / helper methods
    // ////////////////////////////////

    /**
     *  Adds the runtime initializers from the database.
     */
    protected void addRuntimeInitializers() {

        s_log.debug("Method addRuntimeInitializers invoked.");

        Collection names = getRuntimeInitializerNames();

        s_log.debug("Converting DataCollection names to StringArray inits. ");
        String[] inits = (String[]) names.toArray(new String[0]);
        s_log.debug("Create MetadataRoot  mroot. ");
        MetadataRoot mroot = root();
        s_log.debug("Going to sort Stringarray inits. ");
        sort(inits, session("initializer", mroot), mroot);
        
        s_log.debug("Going to process StringArray inits.");
        for (int i = 0; i < inits.length; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("adding: " + inits[i]);
            }
            // Fill initilizer list in @see CompoundInitializer#add
                s_log.info("adding: " + inits[i]);
            add((Initializer) Classes.newInstance(inits[i]));
        }

        s_log.debug("Going to call addWafInitializer()");
        addWafInitializer();

        s_log.debug("Method addRuntimeInitializers completed.");
    }

    /**
     * 
     */
    private void addWafInitializer() {
        String claas = (String)SystemProperties.get(s_init);
        if (claas != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("adding: " + claas);
            }
            add((Initializer) Classes.newInstance(claas));
        }
    }

    /**
     * Fetches the names of initializers loaded into the database.
     *
     * Made protected so that test code can override the initializers
     * loaded at test startup.
     *
     * @return Collection of initializer names.
     */
    protected Collection getRuntimeInitializerNames() {

        s_log.debug("Method getRuntimeInitializerNames invoked.");

        final Session session = session("startup", root());

        LinkedList initNames = new LinkedList();
        final DataCollection inits = session.retrieve
            ("com.arsdigita.runtime.Initializer");

        if (inits.isEmpty()) {
            s_log.debug("DataCollection inits is empty!");
        }

        try {
            while (inits.next()) {
                final String initName = (String) inits.get("className");
                s_log.debug("Got: " + initName );
                initNames.add(initName);
            }
        } finally {
            inits.close();
        }

        s_log.debug("Leaving method getRuntimeInitializerNames.");
        return initNames;
    }

    /**
     * 
     * @return
     */
    private static MetadataRoot root() {
        final MetadataRoot root = new MetadataRoot();
        final PDLCompiler compiler = new PDLCompiler();

        final InputStream in = Runtime.class.getClassLoader
            ().getResourceAsStream(s_model);

        compiler.parse(new InputStreamReader(in), s_model);
        compiler.emit(root);

        return root;
    }

    /**
     * 
     * @param key
     * @param root
     * @return
     */
    private Session session(final String key, final MetadataRoot root) {
        Assert.exists(key, String.class);
        Assert.exists(root, MetadataRoot.class);

        if ( !SessionManager.hasSession(key) ) {
            SessionManager.configure(key, root, m_source);
        }
        final Session session = SessionManager.getSession(key);

        Assert.exists(session, Session.class);

        return session;
    }


    /**
     * 
     * @param inits
     * @param session
     * @param mroot
     */
    private static void sort(String[] inits, Session session, MetadataRoot mroot) {
        s_log.debug("Method Runtime.sort invoked.");

        Set all = new HashSet(Arrays.asList(inits));
        Set provided = new HashSet();
        List sorted = new ArrayList();
        List in = new ArrayList(Arrays.asList(inits));
        int before;
        s_log.debug("Starting to sort.");
        do {
            before = in.size();
            for (Iterator it = in.iterator(); it.hasNext(); ) {
                Set required = new HashSet();
                String init = (String) it.next();
                DataObject dataobject = session.retrieve(
                        new OID(mroot.getObjectType(INIT), init));
                DataAssociation da = (DataAssociation)dataobject.get("requirements");
                DataAssociationCursor cursor = da.cursor();
                while (cursor.next()) {
                    required.add((String)(cursor.getDataObject().get("className")));
                }
                cursor.close();
                required.retainAll(all);
                if (provided.containsAll(required)) {
                    sorted.add(init);
                    provided.add(init);
                    it.remove();
                }
            }
        } while (in.size() < before);

        s_log.debug("Checking List in.");
        if (in.size() > 0) {
            throw new IllegalStateException
                ("circular dependencies: " + in);
        }

        int index = 0;
        s_log.debug("Constructing inits.");
        for (Iterator it = sorted.iterator(); it.hasNext(); ) {
            inits[index++] = (String) it.next();
        }

        s_log.debug("Method Runtime.sort completed.");
    }
}
