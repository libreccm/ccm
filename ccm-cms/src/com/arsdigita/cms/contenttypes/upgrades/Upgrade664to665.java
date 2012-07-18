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
        
        //Reload authoring steps
        XMLContentTypeHandler handler = new XMLContentTypeHandler();
        XML.parseResource("/WEB-INF/content-types/GenericContact.xml", handler);
        XML.parseResource("/WEB-INF/content-types/GenericOrganizationalUnit.xml", handler);
        XML.parseResource("/WEB-INF/content-types/GenericPerson.xml", handler);
    }
    
    public static void main(final String[] args) {
        new Upgrade664to665().run(args);
    }
    
}
