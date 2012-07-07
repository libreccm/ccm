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
public class PublicPersonalProfileUpgrade662to663 extends Program {

    public PublicPersonalProfileUpgrade662to663() {
        super("PublicPersonalProfileBundleUpgrade662to663", "1.0.0", "", true, true);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        new PublicPersonalProfileBundleCreate().doUpgrade();
        new PublicPersonalProfileOwnerAssocUpgrade().doUpgrade();
        
        XMLContentTypeHandler handler = new XMLContentTypeHandler();
        XML.parseResource("/WEB-INF/content-types/com/arsdigita/cms/contenttypes/PublicPersonalProfile.xml", handler);
    }

    public static void main(final String[] args) {
        new PublicPersonalProfileUpgrade662to663().run(args);
    }

}
