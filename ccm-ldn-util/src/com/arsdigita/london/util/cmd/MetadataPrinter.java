package com.arsdigita.london.util.cmd;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Association;
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.Model;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class MetadataPrinter extends Program {

    public MetadataPrinter() {
        super("MetadataPrinter", "1.0.0", "");
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();

        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("MetadataPrinter $oid");
            System.exit(-1);
        }

        final OID oid = OID.valueOf(args[0]);
        final Session session = SessionManager.getSession();
        final DataObject dobj = session.retrieve(oid);

        final ObjectType objType = dobj.getObjectType();
        printObjectTypeData(objType);

        System.out.println("");

        final DomainObject obj = DomainObjectFactory.newInstance(oid);
        if (obj instanceof ContentPage) {
            final ContentPage page = (ContentPage) obj;
            final ContentBundle bundle = page.getContentBundle();
            final OID bundleOid = bundle.getOID();
            System.out.printf("Bundle OID: %s\n\n", bundleOid.toString());
            final DataObject bundleDobj = session.retrieve(bundleOid);            
            printObjectTypeData(bundleDobj.getObjectType());
        }

    }

    public static void main(final String[] args) {
        new MetadataPrinter().run(args);
    }

    private void printObjectTypeData(final ObjectType objType) {
        System.out.printf("ObjectType: %s\n", objType.getName());
        System.out.println("Properties: ");
        Iterator propIter = objType.getProperties();
        while (propIter.hasNext()) {
            printPropertyData((Property) propIter.next());
        }
    }

    private void printPropertyData(final Property property) {
        System.out.printf("\tName..............: %s\n", property.getName());
        System.out.printf("\tisAttribute.......: %b\n", property.isAttribute());
        System.out.printf("\tisRole............: %b\n", property.isRole());
        System.out.printf("\tisCollection......: %b\n", property.isCollection());
        System.out.printf("\tisNullable........: %b\n", property.isNullable());
        System.out.printf("\tisRequired........: %b\n", property.isRequired());
        System.out.printf("\tisComponent.......: %b\n", property.isComponent());
        System.out.printf("\tisComposite.......: %b\n", property.isComponent());
        System.out.printf("\tisKeyProperty.....: %b\n", property.isComponent());
        if (property.getAssociatedProperty() != null) {
            System.out.printf("\tassociatedProperty: %s\n",
                              property.getAssociatedProperty().getName());
        }
        System.out.printf("\tType..............:\n");
        final DataType dataType = property.getType();
        System.out.printf("\t\tName.........: %s\n", dataType.getName());
        System.out.printf("\t\tQualifiedName: %s\n", dataType.getQualifiedName());
        System.out.printf("\t\tisCompound...: %b\n", dataType.isCompound());
        System.out.printf("\t\tisSimple.....: %b\n", dataType.isSimple());

    }
}
