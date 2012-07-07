package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.XMLContentTypeHandler;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.xml.XML;
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
        
        XMLContentTypeHandler handler = new XMLContentTypeHandler();
        XML.parseResource("/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciProject.xml", handler);
    }
    
    public static void main(final String[] args) {
        new SciProjectUpgrade665to666().run(args);
    }
    
}
