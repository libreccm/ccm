package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.XMLContentTypeHandler;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.xml.XML;
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
        new SciInstituteBundleUpgrade().doUpgrade();

        XMLContentTypeHandler handler = new XMLContentTypeHandler();
        XML.parseResource("/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciInsitute.xml", handler);
    }

    public static void main(final String[] args) {
        new SciInstituteUpgrade664to665().run(args);
    }

}
