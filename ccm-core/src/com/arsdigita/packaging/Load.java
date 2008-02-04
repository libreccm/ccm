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

import com.arsdigita.core.DBCheck;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.ConnectionSource;
import com.arsdigita.persistence.DedicatedConnectionSource;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDLCompiler;
import com.arsdigita.runtime.InteractiveParameterLoader;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.Startup;
import com.arsdigita.util.JavaPropertyReader;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.config.JavaPropertyLoader;
import com.arsdigita.util.jdbc.Connections;
import com.arsdigita.util.parameter.CompoundParameterLoader;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * PackageTool
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #29 $ $Date: 2004/08/16 $
 **/

class Load extends Command {

    public final static String versionId = "$Id: Load.java 736 2005-09-01 10:46:05Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Options OPTIONS = getOptions();

    static {
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("config")
             .withDescription("Load configuration")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("schema")
             .withDescription("Load schema")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("data")
             .withDescription("Load data")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("init")
             .withDescription("Load initializers")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg()
             .withLongOpt("parameter-file")
             .withArgName("FILE")
             .withDescription("Use key-value pairs from FILE")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("interactive")
             .withDescription("Prompt for required parameter values")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArgs()
             .withLongOpt("parameters")
             .withArgName("KEY=VALUE ...")
             .withDescription("Use key-value pairs from command line")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("recursive")
             .withDescription("Recursively load required packages")
             .create());
        /*OPTIONS.addOption
          (OptionBuilder
          .hasArg()
          .withLongOpt("log")
          .withArgName("FILE")
          .withDescription("Log parameter values as key-value " +
          "pairs in FILE")
          .create());*/
    }

    public Load() {
        super("load", "Load a CCM package");
    }

    public boolean run(String[] args) {
        CommandLine line;

        try {
            line = new PosixParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return false;
        }

        List packages = line.getArgList();
        if (packages.isEmpty()) {
            usage(OPTIONS, System.err, "PACKAGE-KEYS");
            return false;
        }

        if (line.hasOption("usage") || line.hasOption("help")) {
            usage(OPTIONS, System.out, "PACKAGE-KEYS");
            return true;
        }

        final boolean all = !(line.hasOption("config")
                              || line.hasOption("schema")
                              || line.hasOption("data")
                              || line.hasOption("init"));

        RegistryConfig rc = new RegistryConfig();
        rc.load();
        List loaded = Arrays.asList(rc.getPackages());

        Map loaders = new HashMap();
        List keys = new ArrayList();
        keys.addAll(packages);
        boolean err = false;
        while (!keys.isEmpty()) {
            String key = (String) keys.remove(0);
            if (loaders.containsKey(key)) { continue; }
            Loader l = Loader.get(key);
            if (l == null) {
                System.err.println("unable to locate package: " + key);
                err = true;
            } else {
                loaders.put(key, l);
                if (line.hasOption("recursive")) {
                    keys.addAll(l.getInfo().getRequiredPackages());
                }
            }
        }
        if (err) { return false; }

        Loader[] sorted = (Loader[]) loaders.values().toArray
            (new Loader[loaders.size()]);
        sort(sorted);

        if (all) {
            List missing = new ArrayList();
            addTo(missing, getRequiredPackages(sorted));
            missing.removeAll(getProvidedPackages(sorted));
            missing.removeAll(loaded);
            List conflicts = new ArrayList(loaded);
            conflicts.retainAll(getProvidedPackages(sorted));
            if (!missing.isEmpty()) {
                System.err.println("required packages: " + missing);
            }
            if (!conflicts.isEmpty()) {
                System.err.println("conflicting packages: " + conflicts);
            }
            if (missing.size() + conflicts.size() > 0) {
                return false;
            }
        }

        ParameterMap contexts = new ParameterMap();

        Properties parameters = new Properties();
        CompoundParameterLoader loader = new CompoundParameterLoader();
        if (line.hasOption("parameter-file")) {
            String file = line.getOptionValue("parameter-file");
            try {
                InputStream fis = new FileInputStream(file);
                parameters.load(fis);
                fis.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
            loader.add(new JavaPropertyLoader(parameters));
        }
        if (line.hasOption("parameters")) {
            Properties props = props(line.getOptionValues("parameters"));
            loader.add(new JavaPropertyLoader(props));
            parameters.putAll(props);
        }
        if (line.hasOption("interactive")) {
            loader.add(new InteractiveParameterLoader(System.in, System.out));
        }

        Config config = null;
        try {
            if (all || line.hasOption("config")) {
                ConfigRegistry reg = new ConfigRegistry();
                for (int i = 0; i < sorted.length; i++) {
                    if (!reg.getPackages().contains(sorted[i].getKey())) {
                        reg.initialize(sorted[i].getKey());
                    }
                }
                config = new Config(reg);
                config.load(System.err);

                Parameter param = config.getParameter("waf.config.packages");
                ParameterContext ctx = config.getContainer(param);
                String[] pkgs = (String[]) ctx.get(param);
                for (int i = 0; i < sorted.length; i++) {
                    if (!contains(pkgs, sorted[i].getKey())) {
                        pkgs = concat(pkgs, new String[] { sorted[i].getKey() });
                    }
                }
                ctx.set(param, pkgs);

                contexts.addContexts(config.getContexts());
            }

            if (all || line.hasOption("data")) {
                for (int i = 0; i < sorted.length; i++) {
                    contexts.addContexts(sorted[i].getScripts());
                }
            }

            if (!contexts.load(new JavaPropertyReader(parameters), System.err)) {
                return false;
            }

            if (line.hasOption("interactive")) {
                ParameterEditor editor =
                    new ParameterEditor(contexts, System.in, System.out);
                if (!editor.edit()) { return true; }
            } else if (!contexts.validate(System.err)) {
                return false;
            }

            if (!saveConfig(config)) {
                return false;
            }

            Session ssn = null;
            if (all || line.hasOption("schema") || line.hasOption("data")) {

                Check dbcheck = new DBCheck();
                dbcheck.run(null);
                if (dbcheck.getStatus() == null || dbcheck.getStatus().equals(Check.FAIL)) {
                    rollbackConfig(config,packages);
                    return false;
                }

                if (all || line.hasOption("schema")) {
                    boolean passed = true;
                    for (int i = 0; i < sorted.length; i++) {
                        passed &= sorted[i].checkSchema();
                    }
                    if (!passed) {
                        rollbackConfig(config,packages);
                        return false;
                    }
                }

                Connection conn =
                    Connections.acquire(RuntimeConfig.getConfig().getJDBCURL());

                List required = new ArrayList();
                addTo(required, getRequiredTables(sorted));
                List provided = new ArrayList();

                if (all || line.hasOption("schema")) {
                    required.removeAll(getProvidedTables(sorted));
                    addTo(provided, getProvidedTables(sorted));
                } else if (line.hasOption("data")) {
                    addTo(required, getProvidedTables(sorted));
                }

                List missing = getMissing(conn, required);
                List conflicts = getConflicts(conn, provided);

                if (!missing.isEmpty()) {
                    System.err.println("required tables: " + missing);
                }

                if (!conflicts.isEmpty()) {
                    System.err.println("conflicting tables: " + conflicts);
                }

                if (conflicts.size() > 0 || missing.size() > 0) {
                    rollbackConfig(config,packages);
                    return false;
                }

                if (PackageLoader.exists(conn, "inits")
                        && (line.hasOption("init") || all)) {
                    final boolean success = checkInitializerDependencies
                        (sorted);

                    if (!success) {
                        rollbackConfig(config,packages);
                        return false;
                    }
                }

                if (all || line.hasOption("schema")) {
                    for (int i = 0; i < sorted.length; i++) {
                        sorted[i].loadSchema(conn);
                    }
                    try {
                        conn.commit();
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                        rollbackConfig(config,packages);
                        return false;
                    }
                }


                if (all || line.hasOption("data")) {
                    if (ssn == null) {
                        new Startup().run();
                        ssn = SessionManager.getSession();
                    }

                    boolean passed = true;
                    for (int i = 0; i < sorted.length; i++) {
                        passed &= sorted[i].checkData(ssn);
                    }
                    if (!passed) {
                        rollbackConfig(config,packages);
                        return false;
                    }

                    for (int i = 0; i < sorted.length; i++) {
                        sorted[i].loadData(ssn, loader);
                    }
                }
            }
            if (all || line.hasOption("init")) {
                if (ssn == null) {
                    new Startup().run();
                    ssn = SessionManager.getSession();
                }
                for (int i = 0; i < sorted.length; i++) {
                    sorted[i].loadInits(ssn);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            rollbackConfig(config,packages);
            return false;
        }
        return true;
    }

    private boolean checkInitializerDependencies(final Loader[] sorted) {
        final List required = new ArrayList();
        final List provided = new ArrayList();
        addTo(required, getRequiredInitializers(sorted));
        required.removeAll(getProvidedInitializers(sorted));
        addTo(provided, getProvidedInitializers(sorted));

        final Session boot = session();
        final List missing = getMissing(boot, required);
        final List conflicts = getConflicts(boot, provided);

        if (!missing.isEmpty()) {
            System.err.println("required initializers: " + missing);
            return false;
        }

        if (!conflicts.isEmpty()) {
            System.err.println("conflicting initializers: " + conflicts);
            return false;
        }

        return true;
    }

    private boolean saveConfig(Config config) {
        if (config != null) {
            try {
                config.save();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void rollbackConfig(Config config, List packages) {
        if (config != null) {
            Parameter param = config.getParameter("waf.config.packages");
            ParameterContext ctx = config.getContainer(param);
            String[] pkgs = (String[]) ctx.get(param);
            LinkedList original = new LinkedList();
            for (int i = 0; i < pkgs.length; i++) {
                boolean isnew = false;
                for (int j = 0; j < packages.size(); j++) {
                    if (pkgs[i].toString() == packages.get(j).toString()) {
                        isnew = true;
                    }
                }
                if (!isnew) {
                    original.add(pkgs[i]);
                }
            }
            String[] orig = new String[original.size()];
            for (int i = 0; i < original.size(); i++) {
                orig[i] = (String)original.get(i);
            }
            ctx.set(param, orig);
            saveConfig(config);
        }
    }

    private static List getMissing(Connection conn, List tables) {
        List missing = new ArrayList();
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            String table = (String) it.next();
            if (!PackageLoader.exists(conn, table)) {
                missing.add(table);
            }
        }
        return missing;
    }

    private static List getConflicts(Connection conn, List tables) {
        List conflicts = new ArrayList();
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            String table = (String) it.next();
            if (PackageLoader.exists(conn, table)) {
                conflicts.add(table);
            }
        }
        return conflicts;
    }

    private static final String INIT = "com.arsdigita.runtime.Initializer";

    private static List getMissing(Session ssn, List inits) {
        List missing = new ArrayList();
        for (Iterator it = inits.iterator(); it.hasNext(); ) {
            String init = (String) it.next();
            OID oid = new OID(ssn.getMetadataRoot().getObjectType(INIT), init);
            if (ssn.retrieve(oid) == null) {
                missing.add(init);
            }
        }
        return missing;
    }

    private static List getConflicts(Session ssn, List inits) {
        List conflicts = new ArrayList();
        for (Iterator it = inits.iterator(); it.hasNext(); ) {
            String init = (String) it.next();
            OID oid = new OID(ssn.getMetadataRoot().getObjectType(INIT), init);
            if (ssn.retrieve(oid) != null) {
                conflicts.add(init);
            }
        }
        return conflicts;
    }

    private static String[] concat(String[] a, String[] b) {
        if (a == null) { return b; }
        if (b == null) { return a; }
        String[] result = new String[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static boolean contains(String[] array, String str) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(str)) {
                return true;
            }
        }

        return false;
    }

    private static Session session() {
        Session ssn = SessionManager.getSession("loader");
        if (ssn == null) {
            String pdl = "/com/arsdigita/runtime/Initializer.pdl";
            MetadataRoot root = new MetadataRoot();
            PDLCompiler compiler = new PDLCompiler();
            compiler.parse
                (new InputStreamReader
                 (Load.class.getResourceAsStream(pdl)),
                 pdl);
            compiler.emit(root);
            ConnectionSource source = new DedicatedConnectionSource
                (RuntimeConfig.getConfig().getJDBCURL());
            ssn = SessionManager.open("loader", root, source);
        }

        return ssn;
    }

    static Properties props(String[] args) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(baos);
            for (int i = 0; i < args.length; i++) {
                w.write(args[i]);
                w.write("\n");
            }

            w.flush();

            Properties props = new Properties();
            props.load(new ByteArrayInputStream(baos.toByteArray()));
            return props;
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private static void sort(Loader[] loaders) {
        Set all = new HashSet();
        for (int i = 0; i < loaders.length; i++) {
            all.addAll(loaders[i].getProvided());
        }

        Set provided = new HashSet();
        List sorted = new ArrayList();
        List in = new ArrayList(Arrays.asList(loaders));
        int before;
        do {
            before = in.size();
            for (Iterator it = in.iterator(); it.hasNext(); ) {
                Loader loader = (Loader) it.next();
                Set required = loader.getRequired();
                required.retainAll(all);
                if (provided.containsAll(required)) {
                    sorted.add(loader);
                    provided.addAll(loader.getProvided());
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
            loaders[index++] = (Loader) it.next();
        }
    }

    private static final int REQ_TABLE = 0;
    private static final int REQ_INITIALIZER = 1;
    private static final int REQ_PACKAGE = 2;
    private static final int PROV_TABLE = 3;
    private static final int PROV_INITIALIZER = 4;
    private static final int PROV_PACKAGE = 5;

    private static List get(Loader[] loaders, int type) {
        ArrayList result = new ArrayList();

        for (int i = 0; i < loaders.length; i++) {
            LoaderInfo info = loaders[i].getInfo();

            List c;

            switch (type) {
            case REQ_TABLE:
                c = info.getRequiredTables();
                break;
            case REQ_INITIALIZER:
                c = info.getRequiredInitializers();
                break;
            case REQ_PACKAGE:
                c = info.getRequiredPackages();
                break;
            case PROV_TABLE:
                c = info.getProvidedTables();
                break;
            case PROV_INITIALIZER:
                c = info.getProvidedInitializers();
                break;
            case PROV_PACKAGE:
                c = new ArrayList();
                c.add(loaders[i].getKey());
                break;
            default:
                throw new IllegalArgumentException("unknown type: " + type);
            }

            addTo(result, c);
        }

        return result;
    }

    private static List getRequiredTables(Loader[] loaders) {
        return get(loaders, REQ_TABLE);
    }

    private static List getProvidedTables(Loader[] loaders) {
        return get(loaders, PROV_TABLE);
    }

    private static List getRequiredInitializers(Loader[] loaders) {
        return get(loaders, REQ_INITIALIZER);
    }

    private static List getProvidedInitializers(Loader[] loaders) {
        return get(loaders, PROV_INITIALIZER);
    }

    private static List getRequiredPackages(Loader[] loaders) {
        return get(loaders, REQ_PACKAGE);
    }

    private static List getProvidedPackages(Loader[] loaders) {
        return get(loaders, PROV_PACKAGE);
    }

    private static void addTo(List a, List b) {
        for (Iterator it = b.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (!a.contains(o)) {
                a.add(o);
            }
        }
    }

}
