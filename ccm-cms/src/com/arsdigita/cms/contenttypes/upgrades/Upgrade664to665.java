package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class Upgrade664to665 extends Program {

    public Upgrade664to665() {
        super("Upgrade664to665", "1.0.0", "", true, true);
    }
        
    @Override
    protected void doRun(CommandLine cmdLine) {
        new CreateContactBundles().doUpgrade();
        new CreateOrgaUnitBundles().doUpgrade();
        new CreatePersonBundles().doUpgrade();
        new GenericContactGenericPersonAssocUpgrade().doUpgrade();
        new GenericOrgaUnitGenericContactAssocUpgrade().doUpgrade();
        new GenericOrgaUnitGenericOrgaUnitAssocUpgrade().doUpgrade();
        new GenericOrgaUnitGenericPersonAssocUpgrade().doUpgrade();
        
    }
    
    public static void main(final String[] args) {
        new Upgrade664to665().run(args);
    }
    
}
