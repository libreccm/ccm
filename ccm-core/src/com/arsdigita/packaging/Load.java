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
import com.arsdigita.runtime.InteractiveParameterReader;
import com.arsdigita.runtime.ConfigRegistry;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.Runtime;
import com.arsdigita.util.JavaPropertyReader;
import com.arsdigita.util.jdbc.Connections;
import com.arsdigita.util.parameter.CompoundParameterReader;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * PackageTool worker class, implements the "load" command.
 *  
 * It is called by class MasterTols and loads the database schema and initial 
 * content.
 * 
 * MasterTool provides the following parameters (usually provided by an
 * invokation script 'ccm')
 * 
 * ccm load   PACKAGE-KEYS    [options]
 * PACKAGE-KEYS one or more space separated names of modules (package-key, e.g.
 *              ccm-cms-types-event) which should be loaded into database and
 *              configuration registry
 * Options:     [-usage]  	Display a usage message for load command 
 *              [-help|--help] 	Display a help message for load command
 *              [--packagekeys-file FILE] Reads list of packages to load from
 *                                        File (in addition to command line)
 *              [--schema]  Loads just the schema for a package into the
 *                          database, no data, no initializer
 *              [--data]    Loads just data into the database, schema must exist
 *                          already, initializers are not recorded
 *              [--init]    Records the initializer and classes into database
 *              [--config]  Creates entries in the registry (configuration repo)
 *                          if set prevents any of the three data load steps
 *                          described before to be executed!
 *              [--interactive]  Ask interactively for config key values
 *              [--parameters KEY=VALUE ...] configuration parameter from
 *                                           command line. 
 *              [--parameter-file FILE]   Alternativly reads config parameters
 *                                        from FILE (multiple entries allowed). 
 *              [--recursive]	Recursively load required packages
 * 
 * If neither of the options --config --schema --data --init is set, all four
 * steps are performed (i.e. the usual loading step), e.g. 
 * <code> ccm  load [ListOfPackages]</code>. Regarding configuration, if no 
 * additional configuration options are given (either on command line or a
 * configuration file) the packages are just added to registry.properties 
 * (list of installed packages) and using eventually build in defaults (otherwise 
 * fails) during runtime.
 * If one of the four  parameters is set, none of the other tasks is executed
 * implicitely but each desired step of the four must be explicitely specified!
 * 
 * If new package(s) should just be loaded into the datbase without touching the
 * configuration registry, --schema  --data  --init must be performed.
 * <code>ccm  load  --schema --data --init   [newPackage(s)]</code>
 * May be necessary for an update which contains new modules already configured
 * in the registry but missing in the (old) database.
 *
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #3 $ $Date: 2015/04/27 $
 */
class Load extends Command implements LoadCenter {

    private static final LoadCenterDelegate delegate = new LoadCenterDelegate();
    
    private static final Logger logger = Logger.getLogger(Load.class);
    private static final Options OPTIONS = getOptions();

    //Initializes all available option-flags in an option-set
    static {
        logger.debug("Static initalizer starting...");
        OPTIONS.addOption
            (OptionBuilder
             .hasArg()
             .withLongOpt("packagekeys-file")
             .withArgName("FILE")
             .withDescription("Use PACKAGE_KEYS from FILE instead of command line")
             .create());
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
        logger.debug("Static initalizer finished.");
    }

    /**
     * Standard constructor, super class provides basic functions as name, 
     * short description, usage and help message.
     * 
     */
    public Load() {
        super("load", "Load a CCM package");
    }

    /**
     * Invoked from the central tool "MasterTool" to execute the load process.
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
        Loader[] loaders;
        try {
            loaders = getAllLoaders(line, packages, LoadType.LOAD);
        } catch (Error e) {
            System.err.println(e.getMessage());
            return false;
        }

        //Determines if all steps have to be performed.
        final boolean all = hasAllOptions(line);
        
        //Checks that there are no missing or conflicting packages.
        if (!noMissingAndConflictingPackages(loaders, all)) {
            return false;
        }

        //Gets all parameters set in a file or given to by the
        //agrument line and adds the parameters to the cpr.
        CompoundParameterReader cpr = new CompoundParameterReader();
        Properties parameters = new Properties();
        try {
            getAllParameters(line, parameters);
            cpr.add(new JavaPropertyReader(parameters));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        
        //Adds an appropriate entry to the cpr for interactive
        //parameter reading.
        if (line.hasOption("interactive")) {
            cpr.add(new InteractiveParameterReader(System.in, System.out));
        }

        Config config = null;
        
        try {
            //Initializes all packages in the registry and creates
            //a configuration.
            config = getConfig(line, loaders, config, all);
            //Collects the configuration-parameters and data-scripts
            //in the contexts
            ParameterMap contexts = getContexts(line, loaders, config, all);        

            //Loads the collected configuration-parameters and data-
            //scripts. Saves the configuration.
            if (!contexts.load(new JavaPropertyReader(parameters), System.err)) {
                return false;
            }

            //Creates a parameter-editor which guides through the steps 
            //of setting config key values.
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

            //All --schema and --data specific tasks.
            if (all || line.hasOption("schema") || line.hasOption("data")) {

                //Checks for existence and accessibility of a database.
                if (!checkDatabase()) {
                    rollbackConfig(config, packages);
                    return false;
                }

                //Opens/aquires a connection to the database.
                Connection conn = 
                        Connections.acquire(RuntimeConfig.getConfig().getJDBCURL());

                //Checks the schema, looks for missing or conflicting
                //tables and checks the initializers
                if (!checkSchema(line, loaders, all)
                    || !noMissingAndConflictingTables(line, loaders, conn, all)
                    || !checkInits(line, loaders, conn, all)) {
                    rollbackConfig(config, packages);
                    return false;
                }

                //Loads the schema.
                try {
                    loadSchema(line, loaders, conn, all);
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    rollbackConfig(config,packages);
                    return false;
                }

                //Checks and loads the data.
                if (ssn == null) {
                    new Runtime().startup();
                    ssn = SessionManager.getSession();
                }
                if (!checkAndLoadData(line, loaders, ssn, all, cpr)) {
                    rollbackConfig(config,packages);
                    return false;
                }
            }

            //All --init specific tasks.
            if (all || line.hasOption("init")) {
                if (ssn == null) {
                    new Runtime().startup();
                    ssn = SessionManager.getSession();
                }
                loadInits(loaders, ssn);
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            rollbackConfig(config,packages);
            return false;
        }
        
        return true;
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
    private boolean noMissingAndConflictingPackages(Loader[] loaders, 
            boolean all) {
        return delegate.noMissingAndConflictingPackages(loaders, all);
    }
    
    /**
     * Collects all parameters either set in a file or given per line-argument
     * and stores them in a parameter-variable.
     * 
     * @param line The command-line with all options and arguments
     * @param parameters The properties to store the collected parameters
     * @throws IOException 
     */
    private static void getAllParameters(CommandLine line, Properties parameters) 
            throws IOException {
        if (line.hasOption("parameter-file")) {
            String file = line.getOptionValue("parameter-file");
            InputStream fis = new FileInputStream(file);
            parameters.load(fis);
            fis.close();
        }
        if (line.hasOption("parameters")) {
            Properties params = argsToProperties(line.getOptionValues("parameters"));
            parameters.putAll(params);
        }
    }
    
    /**
     * [SUPPORT]
     * Converts a list of parameter-arguments into properties.
     * 
     * used in: getAllParameters,
     *          Set.java -> run
     * 
     * @param args List of parameter arguments
     * @return A Properties-Object
     * @throws IOException
     */
    public static Properties argsToProperties(String[] args) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(baos);
        for (String arg : args) {
            w.write(arg);
            w.write("\n");
        }
        w.flush();
        Properties params = new Properties();
        params.load(new ByteArrayInputStream(baos.toByteArray()));
        return params;
    }
    
    /**
     * Initializes all package-keys in the registry and creates a specified
     * configuration.
     * 
     * @param line The command-line with all options and arguments
     * @param loaders The loaders to the packages being loaded
     * @param config The configuration-file
     * @param all Weather all steps (config, schema, data, inits) must be
     *            performed
     * @return A configuration for all packages
     */
    private static Config getConfig(CommandLine line, Loader[] loaders, Config 
            config, boolean all) {
        if (all || line.hasOption("config")) {
            ConfigRegistry reg = new ConfigRegistry();
            for (Loader loader : loaders) {
                if (!reg.getPackages().contains(loader.getKey())) {
                    reg.initialize(loader.getKey());
                }
            }
            config = new Config(reg);
        }
        return config;
    }
    
    
    
    /**
     * Collects the contexts in a parameter-map. First setting packages in
     * a parameter-context and adding this parameter-context to the parameter-
     * map. Second adding all data-scripts to the context in the parameter-
     * map.
     * 
     * @param line The command-line with all options and arguments
     * @param loaders The loaders to the packages being loaded
     * @param config The configuration-file
     * @param all Weather all steps (config, schema, data, inits) must be
     *            performed
     * @return The context in a parameter-map
     */
    private static ParameterMap getContexts(CommandLine line, Loader[] 
            loaders, Config config, boolean all) {
        ParameterMap contexts = new ParameterMap();
        if (all || line.hasOption("config")) {
            setParameterContext(loaders, config);
            //Adds the configuration to the context
            contexts.addContexts(config.getContexts());
        }
        if (all || line.hasOption("data")) {
            for (Loader loader : loaders) {
                //Adds the data-scripts to the context
                contexts.addContexts(loader.getScripts());
            }
        }
        return contexts;
    }
    
    /**
     * [SUPPORT]
     * Sets all packages from the loaders-list in a configuration-file to the 
     * key "waf.config.packages".
     * 
     * used in: getContexts
     * 
     * @param loaders The loaders to the packages being loaded
     * @param config The configuration-file
     */
    private static void setParameterContext(Loader[] loaders, Config config) {
        config.load(System.err);
        Parameter param = config.getParameter("waf.config.packages");
        ParameterContext ctx = config.getContainer(param);
        String[] pkgs = (String[]) ctx.get(param);
        for (Loader loader : loaders) {
            if (!contains(pkgs, loader.getKey())) {
                pkgs = concat(pkgs, new String[]{loader.getKey()});
            }
        }
        ctx.set(param, pkgs);
    }
    
    /**
     * [SUPPORT]
     * Checks if a String-array contains a certain String.
     * 
     * used in: setParameterContext
     * 
     * @param array The String-array
     * @param str The String to be checked of being contained by the array
     * @return True on success, otherwise false
     */
    private static boolean contains(String[] array, String str) {
        for (String s : array) {
            if (s.equals(str)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * [SUPPORT]
     * Concatenates two String-array to one.
     * 
     * used in: setParameterContext
     * 
     * @param a first String-array
     * @param b second String-array
     * @return The concatenated String-array
     */
    private static String[] concat(String[] a, String[] b) {
        if (a == null) { return b; }
        if (b == null) { return a; }
        String[] result = new String[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    /**
     * Saves the configurations made during the load process.
     * 
     * @param config The configurations
     * @return true on success, otherwise false.
     */
    private boolean saveConfig(Config config) {
        return delegate.saveConfig(config);
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
    private boolean noMissingAndConflictingTables(CommandLine line, Loader[] 
            loaders, Connection conn, boolean all) {
        return delegate.noMissingAndConflictingTables(line, loaders, conn, all);
    }
    
    /**
     * Checks if the table "inits" exists in the given database connection and 
     * then checks the initializer dependencies in the list of loaders from the 
     * soon-to-be-loaded packages.
     * 
     * @param line The command-line with all options and arguments
     * @param loaders The loaders to the packages being loaded
     * @param conn The connection to the database
     * @param all Weather all steps (config, schema, data, inits) must be
     *            performed
     * @return True on success, otherwise false
     */
    private boolean checkInits(CommandLine line, Loader[] loaders, Connection 
            conn, boolean all) {
        boolean success = true;
        if (PackageLoader.exists(conn, "inits") 
            && (line.hasOption("init") || all)) {
            success = checkInitializerDependencies(loaders, "loader", 
                    LoadType.LOAD);
        }
        return success;
    }
    
    /**
     * [SUPPORT]
     * Checks the initializer dependencies set in the ".load"-file.
     * 
     * used in: checkInits
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
     * Checks the schema of the packages.
     * 
     * @param line The command-line with all options and arguments
     * @param loaders The loaders to the packages being loaded
     * @param Weather all steps (config, schema, data, inits) must be
     *            performed
     * @return True on success, otherwise false
     */
    private boolean checkSchema(CommandLine line, Loader[] loaders, boolean all) {
        boolean passed = true;
        if (all || line.hasOption("schema")) {
            for (Loader loader : loaders) {
                passed &= loader.checkSchema();
            }
        }
        return passed;
    }
    
    /**
     * Loads all schemas of the packages into the database through the opened 
     * connection.
     * 
     * @param line The command-line with all options and arguments
     * @param loaders The loaders to the packages being loaded
     * @param conn The connection to the database
     * @param all Weather all steps (config, schema, data, inits) must be
     *            performed
     * @throws SQLException 
     */
    private void loadSchema(CommandLine line, Loader[] loaders, Connection conn, 
            boolean all) throws SQLException {
        if (all || line.hasOption("schema")) {
            for (Loader loader : loaders) {
                loader.loadSchema(conn);
            }
            conn.commit();
        }
    }
    
    /**
     * Checks and Loads the data of the packages into the database.
     * 
     * @param line The command-line with all options and arguments
     * @param loaders The loaders to the packages being loaded
     * @param ssn The session for the database-connection
     * @param all Weather all steps (config, schema, data, inits) must be
     *            performed
     * @return True on success, otherwise false
     */
    private boolean checkAndLoadData(CommandLine line, Loader[] loaders, Session
            ssn, boolean all, CompoundParameterReader cpr) {
        boolean passed = true;
        if (all || line.hasOption("data")) {
            for (Loader loader : loaders) {
                passed &= loader.checkData(ssn);
            }
            if (passed) {
                for (Loader loader : loaders) {
                    loader.loadData(ssn, cpr);
                }
            }
        }
        return passed;
    }
    
    /**
     * Records/Loads the initializers and classes into the database through the 
     * started session.
     * 
     * @param loaders The loaders to the packages being loaded
     * @param ssn The session for the database-connection
     */
    private void loadInits(Loader[] loaders, Session ssn) {
        for (Loader loader : loaders) {
            loader.loadInits(ssn);
        }    
    }
}
