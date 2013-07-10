package com.arsdigita.cms.upgrade;

import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitBundle;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 * Adds a column {@code personsStr} to GenericOrganizationalUnit, including values. This column contains the names
 * of all members of an organizational unit as one string. See {@link GenericOrganizationalUnit} for more details.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PersonsStrColumn extends Program {

    public PersonsStrColumn() {
        super("PersonsStrColumn", "1.0.0", "");
    }

    public static final void main(final String[] args) {
        new PersonsStrColumn().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {

        new KernelExcursion() {
            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                final Session session = SessionManager.getSession();
                final TransactionContext transactionContext = session.getTransactionContext();

                transactionContext.beginTxn();

                final DataCollection orgaUnitBundles = session.retrieve(
                        GenericOrganizationalUnitBundle.BASE_DATA_OBJECT_TYPE);
                while (orgaUnitBundles.next()) {
                    createPersonsStr(orgaUnitBundles.getDataObject());
                }

                transactionContext.commitTxn();

            }

        }.run();

    }

    public void createPersonsStr(final DataObject dobj) {
        final GenericOrganizationalUnitBundle orgaunitBundle =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.newInstance(dobj);
        final GenericOrganizationalUnitPersonCollection persons = orgaunitBundle.getPersons();
        final StringBuilder builder = new StringBuilder();

        while (persons.next()) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(persons.getSurname());
            builder.append(", ");
            builder.append(persons.getGivenName());
        }

        final String personsStr = builder.toString();

        final ItemCollection instances = orgaunitBundle.getInstances();

        GenericOrganizationalUnit instance;
        while (instances.next()) {
            instance = (GenericOrganizationalUnit) instances.getDomainObject();
            instance.set("personsStr", personsStr);
        }
    }

}
