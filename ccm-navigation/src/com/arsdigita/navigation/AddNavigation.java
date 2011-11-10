package com.arsdigita.navigation;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.web.Application;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

public class AddNavigation extends Program {

    private static final Logger LOG = Logger.getLogger(AddNavigation.class);

    public AddNavigation() {
        super("Add Navigation instance", "1.0.0", "URL-FRAGMENT TITLE DOMAIN-KEY");
    }

    private void addNavigation(String navURL, String navTitle, String defaultDomain) {
        if (!Application.isInstalled(Navigation.BASE_DATA_OBJECT_TYPE, "/"+navURL+"/")) {
            
            DomainObjectFactory.registerInstantiator(
                    Navigation.BASE_DATA_OBJECT_TYPE, new DomainObjectInstantiator() {
                        public DomainObject doNewInstance(DataObject dataObject) {
                            return new Navigation(dataObject);
                        }
                    });
                    /* Create Instance beyond root (4. parameter null)       */
                    Application app = Application.createApplication(
                    Navigation.BASE_DATA_OBJECT_TYPE, navURL, navTitle, null);
            app.save();
            Domain domain = Domain.retrieve(defaultDomain);
            domain.setAsRootForObject(app, null);
        } else {
            System.err.println(Navigation.BASE_DATA_OBJECT_TYPE
                    + " already installed at " + navURL);
            System.exit(1);
        }
    }

    protected void doRun(final CommandLine cmdLine) {
        new Transaction() {
            public void doRun() {
                new KernelExcursion() {
                    public void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());
                        String[] args = cmdLine.getArgs();
                        if (args.length == 3) {
                            String navURL = args[0];
                            String navTitle = args[1];
                            String domainKey = args[2];
                            if (navURL != null && navURL.length() != 0
                                    && navTitle != null && navTitle.length() != 0
                                    && domainKey != null && domainKey.length() != 0) {
                                addNavigation(navURL, navTitle, domainKey);
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
        }.run();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new AddNavigation().run(args);
    }

}
