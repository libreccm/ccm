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

import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.ConnectionSource;
import com.arsdigita.persistence.DedicatedConnectionSource;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDLCompiler;
import com.arsdigita.runtime.ConfigRegistry;
import com.arsdigita.runtime.RegistryConfig;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * The helper class for the "delegate-design-pattern" known as the "delegate"
 * whom is given the responsibility to execute tasks for the "load"- and 
 * "unload"-command.
 * 
 * Contains all implementations defined in the interface as well as their needed 
 * support methods. Furthermore this class implements some methods with support
 * methods only used by the load.java-class.
 * 
 * 
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #2 $ $Date: 2015/04/27 $
 */
public class LoadCenterDelegate implements LoadCenter {
    
    private static final Logger logger = Logger.getLogger(Unload.class);
    private static final String INIT = "com.arsdigita.runtime.Initializer";
    
    private final InfoGetter infoGetter = new InfoGetter();
    
    /**
     * Gets all packages to be unloaded either from the command-line or a file,
     * if the option flag [--packagekeys-file FILE] has been set and puts them 
     * in a list of packages (by their package-keys).
     * 
     * @param line The command-line with all options and arguments
     * @return The list of packages to be unloaded
     * @throws IOException 
     */
    @Override
    public List getAllPackages(CommandLine line) throws IOException {
        List packages = line.getArgList();
        //[--packagekeys-file FILE]
        if (line.hasOption("packagekeys-file")) {
            String file = line.getOptionValue("packagekeys-file");
            logger.debug("File with package keys: " + file );
            Scanner sc = new Scanner(new File(file));
            while (sc.hasNext()) {
                packages.add(sc.next());
            }
        }
        return packages;
    }
    
    /**
     * Gets all loaders to the given package-list and sorts them before re-
     * turning. Creates a map that assigns to every package-key an equivalent 
     * loader. This loader contains a bunch of informations (required, 
     * provided, scripts) provided by an ".load"-file. Then all loaders from 
     * the map (pkg-key -> loader) are composed into an array of loaders and 
     * sorted.
     * 
     * @param line The command-line with all options and arguments
     * @param packages The list of packages to be loaded
     * @param loadType Weather packages are been loaded or unloaded
     * @return A sorted list of loaders
     * @throws Error 
     */
    @Override
    public Loader[] getAllLoaders(CommandLine line, List packages, 
            LoadType loadType) throws Error {
        Map pkgLoaderMap = new HashMap();
        List packageKeys = new ArrayList();
            packageKeys.addAll(packages);
        while (!packageKeys.isEmpty()) {
            String packageKey = (String) packageKeys.remove(0);
            if (pkgLoaderMap.containsKey(packageKey)) { 
                continue; 
            }
            Loader loader = Loader.get(packageKey, loadType);
            if (loader == null) {
                throw new Error("unable to locate package: " + packageKey);
            } else {
                pkgLoaderMap.put(packageKey, loader);
                //[--recursive]
                //If set, all packages required for this package will 
                //be added to the list of packages being unloaded.
                if (line.hasOption("recursive")) {
                    packageKeys.addAll(loader.getInfo().getRequiredPackages());
                }
            }
        }        
        Loader[] loaders = (Loader[]) pkgLoaderMap.values().toArray
            (new Loader[pkgLoaderMap.size()]);
        
        sort(loaders, loadType);
        
        return loaders;
    }
    
    /**
     * [SUPPORT]
     * Sorts a given list of loaders, so that the internal order of which
     * package when to load/unload is consistent with the packages available. 
     * Load: If a package A requires package B, package B has to be loaded 
     * already.
     * Unload: If a package A provides package B, package B has to be unloaded
     * first.
     * Set all:           Set of packages to be loaded and its provided packages
     * List in:           List of packages to be loaded (input)
     * Set required:      Set of packages required by the packages from in
     * List sorted:       List of loaders to be loaded
     * Set provided:      Set of provided packages and to be loaded packages
     * Loaders[] loaders: List of packages to be loaded in the right order
     * 
     * used in: getAllLoaders
     * 
     * @param loaders A list of loaders (to be loaded packages)
     * @param loadType Weather packages are been loaded or unloaded
     */
    private static void sort(Loader[] loaders, LoadType loadType) {
        Set all = new HashSet();
        for (Loader loader : loaders) {
            all.addAll(loader.getProvided());
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
                //Only possible to load
                required.retainAll(all);
                //If the already provided packages contain all 
                //required ones its save to add this one too. 
                //Ensures that the order of loading packages is
                //right.
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
        
        //In case packages are being unloaded
        if (loadType == LoadType.UNLOAD) {
            reverseArray(loaders);
        }
    }
    
    /**
     * [SUPPORT]
     * Reverses the list of loaders to ensure the right order for unloading.
     * 
     * used in: sort
     * 
     * @param loaders The loaders for the packages being unloaded
     */
    private static void reverseArray(Loader[] loaders) {
        for (int i = 0; i < loaders.length / 2; i++) {
            Loader temp = loaders[i];
            loaders[i] = loaders[loaders.length - 1 - i];
            loaders[loaders.length - 1 - i] = temp;
        }
    }
    
    /**
     * Determines if all steps (config, schema, data, inits) have to be 
     * performed.
     * 
     * @param line The command-line with all options and arguments
     * @return True if all options need to be performed
     */
    @Override
    public boolean hasAllOptions(CommandLine line) {
        return !(line.hasOption("config")
                || line.hasOption("schema")
                || line.hasOption("data")
                || line.hasOption("init"));
    }
    
    /**
     * Creates a new (empty) config object and loads config values from a file. 
     * Then retrieves a list of installed packages. RegistryConfig contains a 
     * list of package-keys of loaded packages.
     * 
     * If all steps need to be performed, it checks if there are no more missing
     * or conflicting packages. Missing means, that a package required by a 
     * soon-to-be-loaded-package, has not been loaded yet. Conflicting means, 
     * that a package which will soon be provided by a soon-to-be-loaded-package 
     * has already been loaded. Either way leads to a problem, therefore 
     * returning false.
     * @param loaders The loaders to the packages being loaded
     * @param all Weather all steps (config, schema, data, inits) must be
     *            performed
     * @return True if there are no missing or conflicting packages, otherwise
     *         false
     */
    public boolean noMissingAndConflictingPackages(Loader[] loaders, 
            boolean all) {
        RegistryConfig rc = new RegistryConfig();
        rc.load();
        List loaded = Arrays.asList(rc.getPackages());
        if (all) {
            List missing = new ArrayList();
            addTo(missing, InfoGetter.getRequiredPackages(loaders));
            missing.removeAll(InfoGetter.getProvidedPackages(loaders));
            missing.removeAll(loaded);
            List conflicts = new ArrayList(loaded);
            conflicts.retainAll(InfoGetter.getProvidedPackages(loaders));
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
        return true;
    }
    
    /**
     * [SUPPORT]
     * Adds the elements of the second list to the first list.
     * 
     * used in: noMissingOrConflictingPackages,
     *          checkInitializerDependencies
     *          
     * 
     * @param a The first list
     * @param b The second list
     */
    private static void addTo(List a, List b) {
        for (Iterator it = b.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (!a.contains(o)) {
                a.add(o);
            }
        }
    }
    
    /**
     * Saves the configurations made during the load process.
     * 
     * used in: rollbackConfig, Load.java
     * 
     * @param config The configurations
     * @return true on success, otherwise false.
     */
    public boolean saveConfig(Config config) {
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
        
    /**
     * Checks existence and accessibility of the database and does a rollback 
     * if necessary.
     * 
     * @return True on success, otherwise false
     */
    @Override
    public boolean checkDatabase() {
        Check checkdb = new CheckDB();
        checkdb.run(null);
        if (checkdb.getStatus() == null || 
            checkdb.getStatus().equals(Check.FAIL)) {
            return false;
        }
        return true;
    }

    /**
     * Sets back the configuration to the original packages. Goes through
     * all packages from the configuration-context and removes the ones 
     * contained in the list of packages which will be loaded.
     * 
     * @param config The configuration
     * @param packages The packages to be loaded
     * @return True on success, otherwise false
     */
    @Override
    public boolean rollbackConfig(Config config, List packages) {
        if (config == null) {
            config = new Config(new ConfigRegistry());
            config.load(System.err);
        }        
        Parameter param = config.getParameter("waf.config.packages");
        ParameterContext ctx = config.getContainer(param);
        String[] pkgs = (String[]) ctx.get(param);
        LinkedList original = new LinkedList();
        for (String pkg : pkgs) {
            boolean isnew = false;
            for (Object package1 : packages) {
                // Operator == compares object identity.
                // comparison here refers to package names, so an
                // object comparison will never be true.
                // instead: equals()
                // if (pkgs[i].toString() == packages.get(j).toString()) {
                if (pkg.equals(package1.toString())) {                        
                    isnew = true;
                }
            }
            if (!isnew) {
                original.add(pkg);
            }
        }
        String[] orig = new String[original.size()];
        for (int i = 0; i < original.size(); i++) {
            orig[i] = (String)original.get(i);
        }
        ctx.set(param, orig);
        return saveConfig(config);
    }
    
    /**
     * Lists the required and provided tables and checks if there are no more
     * missing or conflicting tables.
     * Missing means, that a table required by a soon-to-be-created-table, has
     * not been created yet. Conflicting means, that a table which will soon be
     * provided by a soon-to-be-created-table has already been created. Either
     * way leads to a problem, therefore returning false.
     * 
     * @param line The command-line with all options and arguments
     * @param loaders The loaders to the packages being loaded
     * @param conn The connection to the database
     * @param all Weather all steps (config, schema, data, inits) must be
     *            performed
     * @return True if there are no missing or conflicting tables, otherwise
     *         false
     */
    public boolean noMissingAndConflictingTables(CommandLine line, Loader[] 
            loaders, Connection conn, boolean all) {
        List required = new ArrayList();
        addTo(required, InfoGetter.getRequiredTables(loaders));
        List provided = new ArrayList();
        
        if (all || line.hasOption("schema")) {
            required.removeAll(InfoGetter.getProvidedTables(loaders));
            addTo(provided, InfoGetter.getProvidedTables(loaders));
        } else if (line.hasOption("data")) {
            addTo(required, InfoGetter.getProvidedTables(loaders));
        } 
        
        List missing = getMissing(conn, required);
        List conflicts = getConflicts(conn, provided);
        if (!missing.isEmpty()) {
            System.err.println("required tables: " + missing);
        }
        if (!conflicts.isEmpty()) {
            System.err.println(
                    "conflicting tables (already exist): " + conflicts);
        }
        if (conflicts.size() > 0 || missing.size() > 0) {
            return false;
        }
        return true;
    }
    
    /**
     * [SUPPORT]
     * Returns all tables, required by the tables in the given list, which
     * haven't been created yet in the database for the given connection, thus 
     * all missing tables.
     * 
     * used in: noMissingAndConflictingTables
     * 
     * @param conn The connection to the database
     * @param tables The list of tables to be installed
     * @return A list of missing tables
     */
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
    
    /**
     * [SUPPORT]
     * Returns all tables, provided by the tables in the given list, which have
     * already been created in the database for the given connection, thus 
     * all conflicting tables.
     * 
     * used in: noMissingAndConflictingTables
     * 
     * @param conn The connection to the database
     * @param tables The list of tables to be installed
     * @return A list of conflicting tables
     */
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
    
    /**
     * Checks the initializer dependencies set in the ".load"-file.
     * 
     * @param loaders A list of loaders to the corresponding packages
     *                to-be-loaded
     * @param sessionName Name of the session
     * @param type The load-type
     * @return true on success, otherwise false
     */
    @Override
    public boolean checkInitializerDependencies(final Loader[] loaders, 
            String sessionName, LoadType type) {
        final List required = new ArrayList();
        final List provided = new ArrayList();
        addTo(required, InfoGetter.getRequiredInitializers(loaders));
        required.removeAll(InfoGetter.getProvidedInitializers(loaders));
        addTo(provided, InfoGetter.getProvidedInitializers(loaders));
        
        final Session boot = session(sessionName);
        final List missing = getMissing(boot, required);
        final List existing = getExisting(boot, provided);
        
        if (!missing.isEmpty()) {
            System.err.println("required initializers: " + missing);
            return false;
        }

        if (type==LoadType.LOAD) {
            // Beim laden 
            if (!existing.isEmpty()) {
                System.err.println("already existing initializers, "
                        + "thus conflicting: " + existing);
                return false;
            }
        } else if (type==LoadType.UNLOAD) {
            if (existing.isEmpty()) {
                System.err.println("not existing initializers,"
                        + "thus not unloadable: " + existing);
                return false;
            } 
        }
        return true;
    }
    
    /**
     * [SUPPORT]
     * Returns the session to the given name, if existing. If not, opens a new
     * Session for the database-connection with the given name.
     * 
     * used in: checkInitializerDependencies
     * 
     * @return A session for the database-connection
     */
    private static Session session(String name) {
        Session ssn = SessionManager.getSession(name);
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
            ssn = SessionManager.open(name, root, source);
        }
        return ssn;
    }
    
    /**
     * [SUPPORT]
     * Returns a list of initializers, required by the initializers in the
     * given list, which haven't been initialized yet, thus are missing.
     * 
     * used in: checkInitializerDependencies
     * 
     * @param ssn The session for the db-connection
     * @param inits List of initializers
     * @return List of missing initializers
     */
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

    /**
     * [SUPPORT]
     * Returns all initializers, provided by initializers in the given list, 
     * which have already been initialized, thus all existing initializers.
     * 
     * used in: checkInitializerDependencies
     * 
     * @param ssn The session for the db-connection
     * @param inits List of initializers
     * @return List of existing initializers
     */
    private static List getExisting(Session ssn, List inits) {
        List existing = new ArrayList();
        for (Iterator it = inits.iterator(); it.hasNext(); ) {
            String init = (String) it.next();
            OID oid = new OID(ssn.getMetadataRoot().getObjectType(INIT), init);
            if (ssn.retrieve(oid) != null) {
                existing.add(init);
            }
        }
        return existing;
    }
    
        
    /**
     * Hidden class to get information from the loaders
     */
    private static class InfoGetter {
        //Enum for the information type wished to retrieve.
        private static enum InfoType {
            REQ_TABLE, REQ_INITIALIZER, REQ_PACKAGE, 
            PROV_TABLE, PROV_INITIALIZER, PROV_PACKAGE;
        }

        /**
         * Returns the required packages to the given loaders and is used,
         * when checking for missing or conflicting packages.
         * 
         * @param loaders List of loaders
         * @return List of required packages
         */
        private static List getRequiredPackages(Loader[] loaders) {
            return get(loaders, InfoType.REQ_PACKAGE);
        }

        /**
         * Returns the provided packages to the given loaders and is used,
         * when checking for missing or conflicting packages.
         * 
         * @param loaders List of loaders
         * @return List of provided packages
         */
        private static List getProvidedPackages(Loader[] loaders) {
            return get(loaders, InfoType.PROV_PACKAGE);
        }

        /**
         * Returns the required tables to the given loaders and is used when
         * checking for missing or conflicting tables.
         * 
         * @param loaders List of loaders
         * @return List of required tables
         */
        private static List getRequiredTables(Loader[] loaders) {
            return get(loaders, InfoType.REQ_TABLE);
        }

        /**
         * Returns the provided tables to the given loaders and is used when
         * checking for missing or conflicting tables.
         * 
         * @param loaders List of loaders
         * @return List of provided tables
         */
        private static List getProvidedTables(Loader[] loaders) {
            return get(loaders, InfoType.PROV_TABLE);
        }

        /**
         * Returns the required initializers to the given loaders and is used,
         * when checking for missing or conflicting initializers.
         * 
         * supports: checkInitializerDependencies
         * 
         * @param loaders List of loaders
         * @return List of required initializers
         */
        private static List getRequiredInitializers(Loader[] loaders) {
            return get(loaders, InfoType.REQ_INITIALIZER);
        }

        /**
         * Returns the provided initializers to the given loaders and is used,
         * when checking for missing or conflicting initializers.
         * 
         * supports: checkInitializerDependencies
         * 
         * @param loaders List of loaders
         * @return List of provided initializers
         */
        private static List getProvidedInitializers(Loader[] loaders) {
            return get(loaders, InfoType.PROV_INITIALIZER);
        }

        /**
         * Main-Getter, to retrieve the informations provided by the ".load"-file
         * and stored in a loader.
         * 
         * @param loaders List of loaders of the packages to be loaded
         * @param informationType type of the information
         * @return 
         */
        private static List get(Loader[] loaders, InfoType infoType) {
            ArrayList result = new ArrayList();

            for (Loader loader : loaders) {
                LoaderInfo info = loader.getInfo();
                List c;
                switch (infoType) {
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
                        c.add(loader.getKey());
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "unknown type: " + infoType.toString());
                }
                addTo(result, c);
            }
            return result;
        }
    }
}