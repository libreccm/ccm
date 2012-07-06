package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciInstituteUpgrade664to665 extends Program {
    
    public SciInstituteUpgrade664to665() {
        super("SciInstituteUpgrade664to665", "1.0.0", "", true, true);
    }

    @Override
    protected void doRun(CommandLine cmdLine) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void main(final String[] args) {
        new SciInstituteUpgrade664to665().run(args);
    }
    
    
    
}
