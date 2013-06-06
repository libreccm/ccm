/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.globalization;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
// import com.arsdigita.london.util.Transaction;
import com.arsdigita.util.cmd.Program;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 *
 * @author pb
 */
public class MessageCatalogAddResource extends Program {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = 
                                Logger.getLogger(MessageCatalogAddResource.class);

    /**
     * Default Constructor
     */
    public MessageCatalogAddResource() {
        super("Add Resource Bundle to MesageCatalog", 
              "1.0.0", 
              "Fulle qualified bundle name");
    }

    /**
     * Utility method to read a property file and convert it to a MessageCatalog
     * entry.
     * 
     * @param bundleFile String with bundle's fully qualified file name
     */    
    private static void create(String bundleFile) {
        
    }

    /**
     * 
     * @param cmdLine 
     */
    protected void doRun(final CommandLine cmdLine) {
/*      new Transaction() {
            public void doRun() {
                new KernelExcursion() {
                    public void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());
                        String[] args = cmdLine.getArgs();
                        if (args.length == 3) {
                            String bundleName = args[0];
                        //  String navTitle = args[1];
                        //  String domainKey = args[2];
                            if (bundleName != null && bundleName.length() != 0
                               //   && navTitle != null && navTitle.length() != 0
                               //   && domainKey != null && domainKey.length() != 0) 
                               ) {
                                create(bundleName);
                            } else {
                                help(System.err);
                                System.exit(1);
                            }
                        } else {
                            help(System.err);
                            System.exit(1);
                        }
                    }
                }.run();
            }  
        }.run();  */
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new MessageCatalogAddResource().run(args);
    }


    
}
