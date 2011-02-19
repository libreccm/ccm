/*
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package com.arsdigita.kernel;

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.GenericInitializer;
import com.arsdigita.webdevsupport.WebDevSupport;

import java.math.BigDecimal;

import org.apache.log4j.Logger;


/**
 * Initializes the kernel subpackage recurringly each system boot.
 *
 * @author pb
 */
public class Initializer extends GenericInitializer {

    /** Creates a s_logging category with name = to the full name of class  */
    public static final Logger s_log = Logger.getLogger(Initializer.class);


    /**
     * Implementation of the {@link Initializer#init(DomainInitEvent)}
     * method.
     *
     * @param evt The domain init event.
     */
    @Override
    public void init(DomainInitEvent evt) {
        s_log.debug("kernel security domain init begin.");

        // Steps carried over from the old style initializer / enterprise.ini

        /* ** ACSObject ** */
        // register instantiator for ACSObject data object type
        // OLD Initializer code
        // instantiator = new ACSObjectInstantiator();
        // DomainObjectFactory.registerInstantiator(ACSObject.BASE_DATA_OBJECT_TYPE,
        //                                          instantiator);
        evt.getFactory().registerInstantiator
            (ACSObject.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() );

        /* ** Party ** */
        // We use the same instantiator as for ACSObject because party is
        // abstract so we don't need to override doNewInstance().
        //DomainObjectFactory.registerInstantiator(Party.BASE_DATA_OBJECT_TYPE,
        //                                         instantiator);
        evt.getFactory().registerInstantiator
            (Party.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() );

        /* ** User ** */
        evt.getFactory().registerInstantiator
            (User.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(DataObject dobj) {
                     return new User(dobj);
                 }
             } );

        /* ** Group ** */
        // OLD IOnitializer code
        // instantiator = new ACSObjectInstantiator() {
        //         public DomainObject doNewInstance(DataObject dataObject) {
        //             return new Group(dataObject);
        //         }
        //     };
        // DomainObjectFactory.registerInstantiator(Group.BASE_DATA_OBJECT_TYPE,
        //                                          instantiator);
        evt.getFactory().registerInstantiator
            (Group.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(DataObject dobj) {
                     return new Group(dobj);
                 }
             } );

        /*** Role ***/
        // instantiator = new DomainObjectInstantiator() {
        //         public DomainObject doNewInstance(DataObject dataObject) {
        //             return new Role(dataObject);
        //         }
        //     };
        // DomainObjectFactory.registerInstantiator(Role.BASE_DATA_OBJECT_TYPE,
        //                                         instantiator);
        evt.getFactory().registerInstantiator
            (Role.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(DataObject dobj) {
                     return new Role(dobj);
                 }
             } );

        /* Register URLFinders with the URLService */
        // PackageInstance is the only kernel object type for which kernel
        // can provide a URLFinder.  Other object types could have
        // finders registered for them by other initializers (in UI packages).
        // For PackageInstance, urls are determined from the mount points on
        // the site map.
        URLService.registerFinder(PackageInstance.BASE_DATA_OBJECT_TYPE,
                                  new GenericURLFinder(""));

        if (Kernel.getSystemParty() == null) {
            final DatabaseTransaction transaction = new DatabaseTransaction();

            transaction.begin();

            setupSystemParty();

            transaction.end();
        }
        // READ-ONLY operation, during initializing a transaction should not
        // requirred.
        //TransactionContext txn = SessionManager.getSession()
        //                                       .getTransactionContext();
        //txn.beginTxn();

        s_log.debug("c.ad.kernel.Initializer: Initializing privilege descriptors...");
        // Initialize privilege descriptors used in permissions service
        // Recurring task, reads from database and stores in an internal Map
        // field.
        PrivilegeDescriptor.initialize();
        s_log.debug("Done.");

        //txn.commitTxn();

        s_log.debug("kernel security domain init completed");
    }

    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * @param evt The context init event.
     */
    @Override
    public void init(ContextInitEvent evt) {
        s_log.debug("kernel context init begin.");

        Boolean active = KernelConfig.getConfig().isWebdevSupportActive();
        if (Boolean.TRUE.equals(active)) {
            s_log.debug("Registering webdev listener");
            DeveloperSupport.addListener(WebDevSupport.getInstance());
        }

        s_log.debug("kernel context init completed");
    }

    /**
     *
     */
    // Should this be moved to central position for all Initializers?
    // E.g. Compound Initializer or at least waf(core) initializer
        private void setupSystemParty() {
        Party party;

        party = new Party
                (new OID(Party.BASE_DATA_OBJECT_TYPE,
                        new BigDecimal(PermissionManager.SYSTEM_PARTY))) {
            public String getName() {
                return "ACS System Party";
            }
        };

        party.disconnect();

        Kernel.setSystemParty(party);
    }

}
