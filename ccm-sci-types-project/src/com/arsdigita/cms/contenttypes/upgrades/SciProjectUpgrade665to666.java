package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class SciProjectUpgrade665to666 extends Program {

    public SciProjectUpgrade665to666() {
        super("SciProjectUpgrade665to666", "1.0.0", "", true, true);
    }
    
    @Override
    protected void doRun(final CommandLine cmdLine) {
        new SciProjectBundleUpgrade().doUpgrade();
    }
    
    public static void main(final String[] args) {
        new SciProjectUpgrade665to666().run(args);
    }
    
}
