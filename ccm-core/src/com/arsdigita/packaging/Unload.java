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
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.Runtime;
import com.arsdigita.util.jdbc.Connections;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.apache.log4j.Logger;

/**
 * PackageTool worker class, implements the "unload" command.
 *  
 * It is called by class MasterTols and unloads the database schema and initial 
 * content.
 * 
 * MasterTool provides the following parameters (usually provided by an
 * invokation script 'ccm')
 * 
 * ccm unload   PACKAGE-KEYS    [options]
 * PACKAGE-KEYS one or more space separated names of modules (package-key, e.g.
 *              ccm-cms-types-event) which should be loaded into database and
 *              configuration registry
 * Options:     [-usage]  	Display a usage message for load command 
 *              [-help|--help] 	Display a help message for load command
 *              [--packagekeys-file FILE] Reads list of packages to load from
 *                                        File (in addition to command line)
 *              [--schema]      Loads just the schema for a package into the
 *                              database, no data, no initializer
 *              [--data]        Loads just data into the database, schema must 
 *                              exist already, initializers are not recorded
 *              [--init]        Records the initializer and classes into database
 *              [--config]      Removes entries in the registry (configuration 
 *                              repo) if set prevents any of the three data 
 *                              load steps described before to be executed!
 *              [--recursive]	Recursively load required packages
 *
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #11 $ $Date: 2015/04/27 $
 */
class Unload extends Command implements LoadCenter {
    
    private static final LoadCenterDelegate delegate = new LoadCenterDelegate();
    
    private static final Logger logger = Logger.getLogger(Unload.class);
    private static final Options OPTIONS = new Options();
    
    //for OLD unloadConfig Method
    private static final Set EXCLUDE = new HashSet();
    
    //Initializes all option-flags.
    static {
        logger.debug("Static initalizer starting...");
        OPTIONS.addOption
            (OptionBuilder
             .hasArg()
             .withLongOpt("packagekeys-file")
             .withArgName("FILE")
             .withDescription(
                     "Use PACKAGE_KEYS from FILE instead of command line")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("config")
             .withDescription("Unload configuration")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("schema")
             .withDescription("Unload schema")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("data")
             .withDescription("Unload data")
             .create());
        OPTIONS.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("init")
             .withDescription("Unload initializers")
             .create());
//        OPTIONS.addOption
//            (OptionBuilder
//             .hasArg(false)
//             .withLongOpt("recursive")
//             .withDescription("Recursively load required packages")
//             .create());
        logger.debug("Static initalizer finished.");
    }

    //Initializes all excluded files.
    static {
        logger.debug("Static initalizer starting...");
        EXCLUDE.add("resin.conf");
        EXCLUDE.add("resin.pid");
        EXCLUDE.add("server.xml");
        logger.debug("Static initalizer finished.");
    }

    /**
     * Standard constructor, super class provides basic functions as name, 
     * short description, usage and help message.
     * 
     */
    public Unload() {
        super("unload", "Unload configuration");
    }

    /**
     * Invoked from the central tool "MasterTool" to execute the unload process.
     * 
     * @param args The parameters and option-flags
     * @return true if successful, false otherwise
     */
    @Override
    public boolean run(String[] args) {
        
        //Takes the option-set and the arguments and parses 
        //them into a command-line
        CommandLine line;
        try {
            line = new PosixParser().parse(OPTIONS, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return false;
        }

        //[--usage || --help]
        //If set, prints an info-output and returns the function
        //with true
        if (line.hasOption("usage") || line.hasOption("help")) {
            usage(OPTIONS, System.out, "PACKAGE-KEYS");
            return true;
        }
        
        //Gets all packages which will be unloaded and assures
        //that this list is not empty.
        List packages;
        try {
            packages = getAllPackages(line);
            if (packages.isEmpty()) {
                usage(OPTIONS, System.err, "PACKAGE-KEYS");
                return false;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        
        //Gets all unloaders corresponding to the packages which 
        //will be unloaded.
        Loader[] unloaders;
        try {
            unloaders = getAllLoaders(line, packages, LoadType.UNLOAD);
        } catch (Error e) {
            System.err.println(e.getMessage());
            return false;
        }
                
        //Determines if all steps have to be performed.
        final boolean all = hasAllOptions(line);
        
        //Checks for existence and accessibility of a database.
        if (!checkDatabase()) {
            return false;
        }
        
        Connection conn = Connections.acquire(
            RuntimeConfig.getConfig().getJDBCURL());
        new Runtime().startup();
        Session ssn = SessionManager.getSession();
        
        //Unload
        boolean result = true;
        if (all || line.hasOption("init")) {
            result &= unloadInits(conn, ssn, unloaders);
        }
        //TODO: Recursivly changing the reading of the ExternalLinkUnloader.java
        if (all || line.hasOption("data")) {
            result &= unloadData(ssn, unloaders);
        }
        //Finished
        if (all || line.hasOption("schema")) {
            result &= unloadSchema(conn, unloaders);
        }
        //Finished
        if (all || line.hasOption("config")) {
            //Removes the configurations for the packages to be unloaded.
            result &= unloadConfig(packages);
            //result &= unloadConfig(); --OLD
        }
        
        return result;
    }

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
        return delegate.getAllPackages(line);
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
        return delegate.getAllLoaders(line, packages, loadType);
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
        return delegate.hasAllOptions(line);
    }
    
    /**
     * Checks existence and accessibility of the database and does a rollback 
     * if necessary.
     * 
     * @return True on success, otherwise false
     */
    @Override
    public boolean checkDatabase() {
        return delegate.checkDatabase();
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
        return delegate.rollbackConfig(config, packages);
    }
    
    /**
     * Checks the initializer dependencies set in the ".load"-file.
     * 
     * @param loaders A list of loaders to the corresponding packages
     *               to-be-loaded
     * @param sessionName Name of the session
     * @param type The load-type
     * @return true on success, otherwise false
     */
    @Override
    public boolean checkInitializerDependencies(final Loader[] loaders, 
            String sessionName, LoadType type) {
        return delegate.checkInitializerDependencies(loaders, sessionName, type);
    }
    
    /**
     * Unloads the initializers.
     * 
     * @param ssn The session for the db-connection
     * @param unloaders The list of unloaders from the packages to be unloaded
     * @return true on success, otherwise false
     */
    private boolean unloadInits(Connection conn, Session ssn, Loader[] unloaders) {
        boolean passed = true;
        if (PackageLoader.exists(conn, "inits")) {
            passed &= checkInitializerDependencies(unloaders, "unloader", 
                    LoadType.UNLOAD);
            if (!passed) {
                return false;
            }
        }
        for (Loader unloader : unloaders) {
            unloader.unloadInits(ssn);
        }
        return true;
    }
    
    /**
     * Unloads the data.
     * 
     * @param ssn The session for the db-connection
     * @param unloaders The list of unloaders from the packages to be unloaded
     * @return true on success, otherwise false
     */
    private boolean unloadData(Session ssn, Loader[] unloaders) {
        boolean passed = true;
        for (Loader unloader : unloaders) {
            passed &= unloader.checkData(ssn);
            if (!passed) {
                return false;
            }
            unloader.unloadData(ssn);
        }
        return true;
    }
    
    /**
     * Unloads the schema.
     * 
     * @param conn The connection to the database
     * @param unloaders The list of unloaders from the packages to be unloaded
     * @return true on success, otherwise false
     */
    private boolean unloadSchema(Connection conn, Loader[] unloaders) {
        boolean passed = true;
        for (Loader unloader : unloaders) {
            passed &= unloader.checkSchema();
            if (!passed) {
                return false;
            }
            unloader.unloadSchema(conn);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Unloads the configuration by running a rollback.
     * 
     * @param packages The packages to be loaded
     * @return true on success, otherwise false
     */
    private boolean unloadConfig(List packages) {
        return rollbackConfig(null, packages);
    }
    
    /**
     * OLD VERSION
     * Unloads the configuration. Useful???????
     * 
     * @return true on success, otherwise false
     */
//    private boolean unloadConfig() {
//        File conf = CCMResourceManager.getConfigDirectory();
//        File[] files = conf.listFiles(new FileFilter() {
//            public boolean accept(File file) {
//                return !EXCLUDE.contains(file.getName());
//            }
//        });
//        
//        for (int i = 0; i < files.length; i++) {
//            Files.delete(files[i]);
//        }
//        return true;
//    }
}