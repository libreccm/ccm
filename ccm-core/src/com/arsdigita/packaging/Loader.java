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
package com.arsdigita.packaging;

import com.arsdigita.db.DbHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.packaging.LoadCenter.LoadType;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.Initializer;
import com.arsdigita.runtime.Script;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.runtime.Runtime;
import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.ParameterReader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Loader
 *
 * Helper class for load and unload which actually performs the loading and 
 * unloading of the database schema, the data and of the initial content.
 * 
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt; tosmers;
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 * @version $Id: Loader.java 2116 2015-04-15 14:18:40Z tosmers $
 */
class Loader {

    private static final Logger s_log = Logger.getLogger(Loader.class);
    private static final String INIT = "com.arsdigita.runtime.Initializer";

    private String m_key;
    private LoaderInfo m_info;
    private Checklist m_checks;
    private List m_scripts;
    

    /**
     * Constructor. Creates a loader to the given parameters.
     * 
     * @param key The corresponding package-key to the loader
     * @param info The informations from the ".load"-file
     * @param checks A Checklist
     */
    public Loader(String key, LoaderInfo info, Checklist checks, 
            LoadType scriptType) {
        m_key = key;
        m_info = info; //all the stuff form .load-file
        m_checks = checks;
        m_scripts = new ArrayList();
        for (Iterator it = m_info.getDataScripts(scriptType).iterator();
            it.hasNext();) {
            String script = (String) it.next();
            m_scripts.add(Classes.newInstance(script));
        }
    }
    
    /**
     * Gets a loader to the given package-key via an input-stream if
     * a corresponding ".load"-file to the package-key exists. The
     * loader contains the informations stored in the ".load"-file.
     * 
     * @param pkg The package-key
     * @return A Loader to the given package-key
     */
    public static Loader get(String pkg, LoadType scriptType) {
        ClassLoader ldr = Loader.class.getClassLoader();
        InputStream is = ldr.getResourceAsStream(pkg + ".load");
        if (is == null) {
            s_log.error(String.format("Failed to find '%s.load'.", pkg));
            return null;
        }
        //Contains all relevante data from the .load-file.
        LoaderInfo info = new LoaderInfo(is);
        try {
            is.close();
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        }
        return new Loader(pkg, info, Checklist.get(pkg), scriptType);
    }

    /**
     * Getter for the package-key.
     * 
     * @return The package-key
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Getter for the loader-informations.
     * 
     * @return The loader-informations
     */
    public LoaderInfo getInfo() {
        return m_info;
    }

    /**
     * List of scripts for the loader, provided by the ".load"-file.
     * 
     * @return A list of scripts
     */
    public List getScripts() {
        return m_scripts;
    }

    /**
     * Checks the schema, set in the ".load"-file.
     * 
     * @return true on success, otherwise false
     */
    public boolean checkSchema() {
        if (m_checks == null) {
            return true;
        } else {
            return m_checks.run(Checklist.SCHEMA, new ScriptContext(null, null));
        }
    }

    /**
     * Loads the schema, set in the ".load"-file.
     * 
     * @param conn The connection to the database
     */
    public void loadSchema(Connection conn) {
        int db = DbHelper.getDatabase(conn);
        String dir = DbHelper.getDatabaseDirectory(db);
        List scripts = m_info.getSchemaScripts();
        for (Iterator it = scripts.iterator(); it.hasNext();) {
            String script = (String) it.next();
            s_log.info("Loading schema for " + script);
            PackageLoader.load(conn, script + "/" + dir + "-create.sql");
        }
    }
    
    /**
     * Unloads the schema, set in the ".load"-file.
     * 
     * @param conn The connection to the database
     */
    public void unloadSchema(Connection conn) {
        int db = DbHelper.getDatabase(conn);
        String dir = DbHelper.getDatabaseDirectory(db);
        List scripts = m_info.getSchemaScripts();
        for (Iterator it = scripts.iterator(); it.hasNext();) {
            String script = (String) it.next();
            s_log.info("Unloading schema for " + script);
            PackageLoader.load(conn, script + "/" + dir + "-drop.sql");
        }
    }

    /**
     * Checks the data, set in the ".load"-file.
     * 
     * @param ssn The session
     * @return true on success, otherwise false
     */
    public boolean checkData(Session ssn) {
        if (m_checks == null) {
            return true;
        } else {
            return m_checks.run(Checklist.DATA, new ScriptContext(ssn, null));
        }
    }

    /**
     * Loads the data, set in the ".load"-file.
     * 
     * @param ssn The session
     * @param prd The ParameterReader
     */
    public void loadData(Session ssn, ParameterReader prd) {
        final List inits = m_info.getProvidedInitializers();
        CompoundInitializer ini = new CompoundInitializer();
        for (Iterator it = inits.iterator(); it.hasNext();) {
            String init = (String) it.next();
            s_log.info("Running initializer " + init);
            ini.add((Initializer) Classes.newInstance(init));
        }
        Runtime.startup(ssn, ini);

        TransactionContext txn = ssn.getTransactionContext();
        txn.beginTxn();

        //  final ScriptContext ctx = new ScriptContext(ssn, loader);
        final ScriptContext ctx = new ScriptContext(ssn, prd);
        new KernelExcursion() {
            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                for (Iterator it = m_scripts.iterator(); it.hasNext(); ) {
                    Script script = (Script) it.next();
                    s_log.info("Running data loader " + script.getClass().
                            getName());
                    script.run(ctx);
                }
            }
        }.run();

        if (txn.inTxn()) {
            txn.commitTxn();
        }
    }
    
    /**
     * Unloads the data. Takes the data-unload script from the ".load"-file and
     * implements a new KernelExcursion's excurse-method, in which the
     * Script.java's run-method is called. This run-method is implemented by
     * the AbstractContentTypeUnloader.java, which implements himself an other
     * new KernelExcursion's excurse-method, calling the sweepTypes-method, the 
     * heart of the unloadData functionality.
     * 
     * @param ssn The session to the database
     */
    public void unloadData(Session ssn) {
        TransactionContext txn = ssn.getTransactionContext();
        txn.beginTxn();
        
        final ScriptContext ctx = new ScriptContext(ssn, null);
        new KernelExcursion() {
            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                for (Iterator it = m_scripts.iterator(); it.hasNext(); ) {
                    Script script = (Script) it.next();
                    s_log.info("Running data unloader " + script.getClass().
                            getName());
                    script.run(ctx);
                }
            }
        }.run();

        if (txn.inTxn()) {
            txn.commitTxn();
        } 
    }

    /**
     * Loads the initializers, set in the ".load"-file. Creates a new data-
     * object with an "oid" for every provided initializer and and add the
     * required initializers to its requirements for data-associations.
     * 
     * @param ssn The session
     */
    public void loadInits(final Session ssn) {
        final TransactionContext txn = ssn.getTransactionContext();
        txn.beginTxn();

        final List inits = m_info.getProvidedInitializers();
        final List required = m_info.getRequiredInitializers();
        for (Iterator it = inits.iterator(); it.hasNext(); ) {
            String init = (String) it.next();
            DataObject dataobject = ssn.create(new OID(INIT, init));
            DataAssociation da =
                            (DataAssociation) dataobject.get("requirements");
            for (Iterator reqit = required.iterator(); reqit.hasNext();) {
                String reqinit = (String) reqit.next();
                da.add(ssn.retrieve(new OID(INIT, reqinit)));
            }
        }

        if (txn.inTxn()) {
            txn.commitTxn();
        }
    }
    
    /**
     * Unloads the initializers, set in the ".load"-file". Removes for every
     * provided initializer the required initializers form the requirements in
     * the data-association and deletes the data object to that provided
     * initializer.
     * 
     * @param ssn The session
     */
    public void unloadInits(final Session ssn) {
        final TransactionContext txn = ssn.getTransactionContext();
        txn.beginTxn();
        
        final List inits = m_info.getProvidedInitializers();
        final List required = m_info.getRequiredInitializers();
        for (Iterator it = inits.iterator(); it.hasNext(); ) {
            String init = (String) it.next();
            DataObject dataObject = ssn.retrieve(new OID(INIT, init));
            
            DataAssociation da1 =
                            (DataAssociation) dataObject.get("requirements");
            for (Iterator reqIt = required.iterator(); reqIt.hasNext(); ) {
                String reqInit = (String) reqIt.next();
                da1.remove(ssn.retrieve(new OID(INIT, reqInit)));
            }
            
            DataAssociation da2 =
                            (DataAssociation) dataObject.get("inits");
            da2.remove(ssn.retrieve(new OID(INIT, init)));
        }
        
        if (txn.inTxn()) {
            txn.commitTxn();
        }
    }

    /**
     * Returns all tables, initializers and packages required by this loader.
     * Used in Load.sort
     * 
     * @return A set of required things like tables, initializers and packages
     */
    public Set getRequired() {
        Set result = new HashSet();
        result.addAll(m_info.getRequiredTables());
        result.addAll(m_info.getRequiredInitializers());
        result.addAll(m_info.getRequiredPackages());
        return result;
    }

    /**
     * Returns all tables and initializers provided by this loader.
     * Used in Load.sort
     * 
     * @return A set of provided things like tables and initializers.
     */
    public Set getProvided() {
        Set result = new HashSet();
        result.addAll(m_info.getProvidedTables());
        result.addAll(m_info.getProvidedInitializers());
        result.add(m_key);
        return result;
    }

    @Override
    public String toString() {
        return "<loader for " + m_key + ">";
    }
}
