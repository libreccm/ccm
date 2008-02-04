/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.packaging;

import com.arsdigita.runtime.Startup;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * <p>
 * Provides base framework for writing command line
 * programs that require the CCM context to be be
 * initialized. It also integrates the Apache Commons
 * CLI package for simple command line argument handling.
 * </p>
 * <p>
 * As an example usage scenario for this class, 
 * consider a tool that prints out the current
 * sitemap. This would be implemented as follows:
 * </p>
 * 
 * <pre>
 *public class SiteMapList extends Program {
 *    
 *    public SiteMapList() {
 *        super("Site Map Listing"
 *              "1.0.0",
 *              "");
 *    }
 *    
 *    protected void doRun(CommandLine cmdLine) {
 *        ApplicationCollection apps = 
 *            Application.retrieveAllApplications();
 *        apps.addOrder("primaryURL");
 *        
 *        while (apps.next()) {
 *            Application app = apps.getApplication();
 *            
 *            System.out.println(app.getPath() + " -> " +
 *                               app.getApplicationType().getTitle());
 *            
 *            if (isVerbose()) {
 *                System.out.println(" Description: " + 
 *                                   app.getApplicationType().getDescription());
 *            } 
 *            if (isDebug()) {
 *                System.out.println(" Class: " + 
 *                                   app.getClass());
 *            }
 *        }
 *    }
 *
 *    public static void main(String[] args) {
 *        new SiteMapList().run(args);
 *    }
 *}
 * </pre>
 * 
 */
public abstract class Program {
    
    private String m_name;
    private String m_version;
    private String m_usage;
    private boolean m_verbose = false;
    private boolean m_debug = false;
    private boolean m_startup = true;

    private Options m_options;
    
    /**
     * Creates a new program. The conventions for the 
     * usage parameter follow the GNU style guidelines.
     * For example, if there are multiple source files
     * and one destination, it would be "SOURCE... DEST".
     * Standard WAF startup procedures will be performed
     * before the doRun method is executed.
     * @param name the program name
     * @param version the version string
     * @param usage for any non-option command line arguments
     */
    public Program(String name,
                   String version,
                   String usage) {
        this(name, version, usage, true);
    }

    /**
     * Creates a new program. The conventions for the 
     * usage parameter follow the GNU style guidelines.
     * For example, if there are multiple source files
     * and one destination, it would be "SOURCE... DEST"
     * @param name the program name
     * @param version the version string
     * @param usage for any non-option command line arguments
     * @param startup true to perform standard WAF startup
     */
    public Program(String name,
                   String version,
                   String usage,
                   boolean startup) {
        m_name = name;
        m_version = version;
        m_usage = usage;
        m_startup = startup;
        m_options = new Options();
        
        m_options.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("help")
             .withDescription("Print this message")
             .create('?'));
        m_options.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("version")
             .withDescription("Print version information")
             .create('V'));
        m_options.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("verbose")
             .withDescription("Generate verbose output")
             .create('v'));
        m_options.addOption
            (OptionBuilder
             .hasArg(false)
             .withLongOpt("debug")
             .withDescription("Generate debugging output")
             .create('D'));
    }

    /**
     * Retrieves the version for this program
     * @return the version string
     */
    public String getVersion() {
        return m_version;
    }

    /**
     * Retrieves the program name
     * @return the program name
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * Retrieves the usage for non-option command
     * line arguments
     * @return the command usage
     */
    public String getUsage() {
        return m_usage;
    }
    
    /**
     * Retrieves the command line options configuration
     * @return the command line options
     */
    public Options getOptions() {
        return m_options;
    }
    
    /**
     * Determines if the program should generate verbose
     * output. Verbose output is information that is  
     * useful to end users of the program.
     * @return true if verbose output is required
     */
    public boolean isVerbose() {
        return m_verbose;
    }
    
    /**
     * Determines if the program should generate debug
     * output. Debug output is information that is useful
     * to programmers or system administrators.
     * @return true if debug output is required
     */
    public boolean isDebug() {
        return m_debug;
    }
    
    /**
     * Runs the program. First off all the command line 
     * arguments are first parsed. If the --help or 
     * --version arguments were specified then this
     * information is output and execution stops.
     * Otherwise, the CCM startup process is invoked
     * and then finally the doRun method is run.
     * @param args the command line arguments to parse
     */
    public final void run(String[] args) {
        CommandLine cmdLine = null;
        try {
            cmdLine = new GnuParser().parse(m_options, 
                                            args);
        } catch (ParseException ex) {
            help(System.err);
            System.exit(1);
        }
        
        if (cmdLine.hasOption("?")) {
            help(System.out);
            System.exit(0);
        } else if (cmdLine.hasOption("V")) {
            System.out.println(m_version);
            System.exit(0);
        }
        
        m_verbose = cmdLine.hasOption("v");
        m_debug = cmdLine.hasOption("D");
                
        try {
            if (m_startup) {
                Startup startup = new Startup();
                startup.run();
            }

            doRun(cmdLine);

            System.exit(0);
        } catch (Throwable t) {
            System.err.println("Error: " + t.getClass() + 
                               ":" + t.getMessage());
            if (isDebug()) {
                ProgramErrorReport report = 
                    new ProgramErrorReport(t, args);
                report.logit();
            } else if (isVerbose()) {
                t.printStackTrace(System.err);
            }
            System.exit(1);
        }
    }

    /**
     * This method should be implemented by subclasses
     * to do whatever processing they require.
     * @param cmdLine the parsed command line
     */
    protected abstract void doRun(CommandLine cmdLine);

    /**
     * Sends a command line help usage message to the
     * output stream
     * @param os the destination stream
     */
    public void help(OutputStream os) {
        HelpFormatter fmt = new HelpFormatter();
        PrintWriter w = new PrintWriter(os);
        fmt.printHelp(w, 80, 
                      "ccm-run " + getClass().getName() + 
                      " [OPTION]... " + getUsage(), 
                      "\nOptions:", 
                      getOptions(), 
                      2, 4, null, false);
        w.flush();
    }
}
