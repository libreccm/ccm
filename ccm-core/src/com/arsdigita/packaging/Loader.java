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
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.Initializer;
import com.arsdigita.runtime.Script;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.runtime.Startup;
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
 * Helper class for load which actually performs the loading of
 * the database schema and of the initial content.
 * 
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 * @version $Id: Loader.java 2115 2011-01-13 17:11:50Z pboy $
 */
class Loader {

    private static final Logger s_log = Logger.getLogger(Loader.class);
    private static final String INIT = "com.arsdigita.runtime.Initializer";

    public static Loader get(String pkg) {
        ClassLoader ldr = Loader.class.getClassLoader();
        InputStream is = ldr.getResourceAsStream(pkg + ".load");
        if (is == null) {
            s_log.error(String.format("Failed to find '%s.load'.", pkg));
            return null;
        }
        LoaderInfo info = new LoaderInfo(is);
        try {
            is.close();
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        }
        return new Loader(pkg, info, Checklist.get(pkg));
    }
    private String m_key;
    private LoaderInfo m_info;
    private Checklist m_checks;
    private List m_scripts;

    public Loader(String key, LoaderInfo info, Checklist checks) {
        m_key = key;
        m_info = info;
        m_checks = checks;
        m_scripts = new ArrayList();
        for (Iterator it = m_info.getDataScripts().iterator();
             it.hasNext();) {
            String script = (String) it.next();
            m_scripts.add(Classes.newInstance(script));
        }
    }

    public String getKey() {
        return m_key;
    }

    public LoaderInfo getInfo() {
        return m_info;
    }

    public List getScripts() {
        return m_scripts;
    }

    public boolean checkSchema() {
        if (m_checks == null) {
            return true;
        } else {
            return m_checks.run(Checklist.SCHEMA, new ScriptContext(null, null));
        }
    }

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

    public boolean checkData(Session ssn) {
        if (m_checks == null) {
            return true;
        } else {
            return m_checks.run(Checklist.DATA, new ScriptContext(ssn, null));
        }
    }

    // deprecated:
    // public void loadData(Session ssn, ParameterLoader loader) {
    public void loadData(Session ssn, ParameterReader prd) {
        final List inits = m_info.getProvidedInitializers();
        CompoundInitializer ini = new CompoundInitializer();
        for (Iterator it = inits.iterator(); it.hasNext();) {
            String init = (String) it.next();
            s_log.info("Running initializer " + init);
            ini.add((Initializer) Classes.newInstance(init));
        }
        Startup.run(ssn, ini);

        TransactionContext txn = ssn.getTransactionContext();
        txn.beginTxn();

        //  final ScriptContext ctx = new ScriptContext(ssn, loader);
        final ScriptContext ctx = new ScriptContext(ssn, prd);
        new KernelExcursion() {

            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                for (Iterator it = m_scripts.iterator(); it.hasNext();) {
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

    public void loadInits(final Session ssn) {
        final TransactionContext txn = ssn.getTransactionContext();
        txn.beginTxn();

        final List inits = m_info.getProvidedInitializers();
        final List required = m_info.getRequiredInitializers();
        for (Iterator it = inits.iterator(); it.hasNext();) {
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

    Set getRequired() {
        Set result = new HashSet();
        result.addAll(m_info.getRequiredTables());
        result.addAll(m_info.getRequiredInitializers());
        result.addAll(m_info.getRequiredPackages());
        return result;
    }

    Set getProvided() {
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
