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
 * An entry point initializer for the CCM runtime. This class may be
 * used to bootstrap the CCM runtime environment into a state where it
 * is safe to perform database I/O. It does this by accessing a
 * persistent list of all the initializers required by the currently
 * loaded packages. This guarantees that any of these packages has the
 * opportunity to load any object-relational metadata and register any
 * domain-data coupling metadata before any database I/O is performed.
 *
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Startup.java 738 2005-09-01 12:36:52Z sskracic $
 */
public  class Startup extends CompoundInitializer {
    public final static String versionId =
        "$Id: Startup.java 738 2005-09-01 12:36:52Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Startup.class);

    private static final String s_model = "com/arsdigita/runtime/Initializer.pdl";
    private static final String INIT = "com.arsdigita.runtime.Initializer";

    private final ConnectionSource m_source;

    private static final Parameter s_init = new StringParameter
        ("waf.runtime.init", Parameter.OPTIONAL, null);

    private static boolean s_hasRun = false;

    public Startup() {
        this(new PooledConnectionSource
             (RuntimeConfig.getConfig().getJDBCURL(),
              RuntimeConfig.getConfig().getJDBCPoolSize(),
              RuntimeConfig.getConfig().getJDBCPingInterval()));
    }

    /**
     * Prepares a startup object with a list of child
     * <code>Initializers</code> to run in order to initialize the
     * runtime. This method relies on a caller-provided connection
     * source.
     *
     * @param source The connection source used to recover the list of
     * initializers from the database; it cannot be null.
     **/

    public Startup(ConnectionSource source) {
        super(s_log);

        Assert.exists(source, ConnectionSource.class);

        m_source = source;
    }

    private void addWafInitializer() {
        String claas = (String)SystemProperties.get(s_init);
        if (claas != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("adding: " + claas);
            }
            add((Initializer) Classes.newInstance(claas));
        }
    }

    /*
     *  Adds the runtime initializers from the database.
     */
    protected void addRuntimeInitializers() {
        Collection names = getRuntimeInitializerNames();
        String[] inits = (String[]) names.toArray(new String[0]);
        MetadataRoot mroot = root();
        sort(inits, session("initializer", mroot), mroot);
        for (int i = 0; i < inits.length; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("adding: " + inits[i]);
            }
            add((Initializer) Classes.newInstance(inits[i]));
        }
        addWafInitializer();
    }

    /**
     * Fetches the names of initializers loaded into the database.
     * Made protected so that test code can override the initializers
     * loaded at test startup.
     *
     * @return Collection of initializer names.
     */
    protected Collection getRuntimeInitializerNames() {
        final Session session = session("startup", root());

        LinkedList initNames = new LinkedList();
        final DataCollection inits = session.retrieve
            ("com.arsdigita.runtime.Initializer");

        try {
            while (inits.next()) {
                final String initName = (String) inits.get("className");
                initNames.add(initName);
            }
        } finally {
            inits.close();
        }

        return initNames;
    }

    private static MetadataRoot root() {
        final MetadataRoot root = new MetadataRoot();
        final PDLCompiler compiler = new PDLCompiler();

        final InputStream in = Startup.class.getClassLoader
            ().getResourceAsStream(s_model);

        compiler.parse(new InputStreamReader(in), s_model);
        compiler.emit(root);

        return root;
    }

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
     * Executes the initialization process.
     *
     * This method is going to move.
     */
    public static final void run(final Session session,
                                 final Initializer init) {

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

    }

    /**
     * Executes the initialization process using the default session
     * and global metadata root.
     *
     * @see MetadataRoot#getMetadataRoot()
     * @see SessionManager#getSession()
     **/

    public final void run() {
        s_log.info("Initializing WAF runtime");

        // Deprecated, no longer needed.
        // startup();
        
        DbHelper.setDatabase
            (DbHelper.getDatabaseFromURL(RuntimeConfig.
                                         getConfig().getJDBCURL()));
        addRuntimeInitializers();
        //addWafInitializer();

        final MetadataRoot root = MetadataRoot.getMetadataRoot();
        // XXX It shouldn't be necessary to do this until the legacy
        // init step, but SessionManager.getMetadataRoot depends on
        // the session, and the DomainObjectFactory calls it.
        final Session session = session("default", root);
        run(session, this);

        s_log.info("Initialization complete");
    }

    public static boolean hasRun() {
        return s_hasRun;
    }

//     /**
//      * Executes the initialization process for the default session and
//      * the <code>Startup</code>-determined set of initializers.
//      */
//     public final void run() {
//          s_log.info("Initializing WAF runtime");

//          final MetadataRoot root = MetadataRoot.getMetadataRoot();
//          final Session session = session("default", root);

//          Startup.run(session, this);

//          s_log.info("Initialization complete");
//     }

    public static final void main(final String[] args) throws SQLException {
        final String url;
        final int size;
        if (args.length == 0) {
            url = RuntimeConfig.getConfig().getJDBCURL();
        } else {
            url = args[0];
        }

        new Startup(new DedicatedConnectionSource(url)).run();
    }

    private static void sort(String[] inits, Session session, MetadataRoot mroot) {
        Set all = new HashSet(Arrays.asList(inits));
        Set provided = new HashSet();
        List sorted = new ArrayList();
        List in = new ArrayList(Arrays.asList(inits));
        int before;
        do {
            before = in.size();
            for (Iterator it = in.iterator(); it.hasNext(); ) {
                Set required = new HashSet();
                String init = (String) it.next();
                DataObject dataobject = session.retrieve(new OID(mroot.getObjectType(INIT), init));
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

        if (in.size() > 0) {
            throw new IllegalStateException
                ("circular dependencies: " + in);
        }

        int index = 0;
        for (Iterator it = sorted.iterator(); it.hasNext(); ) {
            inits[index++] = (String) it.next();
        }
    }
}
