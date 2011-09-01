package com.arsdigita.cms.dabin;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.packaging.Program;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PersonImporter extends Program {

    private Properties config;
    private ContentSection section;
    private Folder memberFolder;
    private Folder authorsFolder;
    private Folder miscFolder;

    public PersonImporter() {
        this(true);
    }

    public PersonImporter(final boolean startup) {
        super("PersonImporter",
              "0.1.0",
              "configFile",
              startup);

    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        final String args[];
        String mySqlHost;
        String mySqlUser;
        String mySqlPassword;
        String mySqlDb;

        System.out.println("");
        System.out.println("");
        System.out.println("PersonImporter is starting...");

        //Get command line arguments...
        args = cmdLine.getArgs();

        if (args.length != 1) {
            System.out.println("Invalid number of arguments.");
            help(System.err);
            System.exit(-1);
        }

        config = new Properties();
        try {
            config.loadFromXML(new FileInputStream(args[0]));
        } catch (FileNotFoundException ex) {
            System.err.printf("Configuration file '%s' not found:\n", args[0]);
            ex.printStackTrace(System.err);
            System.exit(-1);
        } catch (IOException ex) {
            System.err.printf("Failed to read configuration file '%s' "
                              + "not found:\n",
                              args[0]);
            ex.printStackTrace(System.err);
            System.exit(-1);
        }

        mySqlHost = config.getProperty("mysql.host", "localhost");
        mySqlUser = config.getProperty("mysql.user");
        mySqlPassword = config.getProperty("mysql.password");
        mySqlDb = config.getProperty("mysql.db");

        throw new UnsupportedOperationException("Not supported yet.");
    }
}
