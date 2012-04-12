package com.arsdigita.atoz;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.web.Application;


public class AddAtoZ extends Program {

    private static final Logger LOG = Logger.getLogger(AddAtoZ.class);

    public AddAtoZ() {
        super("Add AtoZ instance", "1.0.0", "URL-FRAGMENT TITLE");
    }

    private void addAtoZ(String atozURL, String atozTitle) {
        if (!Application.isInstalled(AtoZ.BASE_DATA_OBJECT_TYPE, "/"+atozURL+"/")) {
            DomainObjectFactory.registerInstantiator(
                    AtoZ.BASE_DATA_OBJECT_TYPE, new DomainObjectInstantiator() {
                        public DomainObject doNewInstance(DataObject dataObject) {
                            return new AtoZ(dataObject);
                        }
                    });
            Application app = Application.createApplication(
                    AtoZ.BASE_DATA_OBJECT_TYPE, atozURL, atozTitle, null);
            app.save();
        } else {
            System.err.println(AtoZ.BASE_DATA_OBJECT_TYPE
                    + " already installed at " + atozURL);
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
                        if (args.length == 2) {
                            String atozURL = args[0];
                            String atozTitle = args[1];
                            if (atozURL != null && atozURL.length() != 0
                                    && atozTitle != null && atozTitle.length() != 0) {
                                addAtoZ(atozURL, atozTitle);
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
        new AddAtoZ().run(args);
    }

}
