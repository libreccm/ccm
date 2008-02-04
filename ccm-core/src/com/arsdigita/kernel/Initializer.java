/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;


import org.apache.log4j.Logger;

/**
 * Initializes the Kernel and bootstraps the rest of the system.
 *
 * @version $Revision: #39 $ $Date: 2004/08/16 $
 */
public class Initializer extends BaseInitializer {
    public static final String versionId =
        "$Id: Initializer.java 1169 2006-06-14 13:08:25Z fabrice $" +
        "$Author: fabrice $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Initializer.class);

    private Configuration m_conf = new Configuration();

    public Configuration getConfiguration() {
        return m_conf;
    }

    protected void doStartup() {
        setupDomainFactory();
        setupURLService();

        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();

        // Initialize privilege descriptors used in permissions service
        s_log.debug("Initializing privilege descriptors...");
        PrivilegeDescriptor.initialize();
        s_log.debug("Done.");

        txn.commitTxn();
    }

    private void setupDomainFactory() {
        DomainObjectInstantiator instantiator;

        /*** ACSObject ***/
        // register instantiator for ACSObject data object type
        instantiator = new ACSObjectInstantiator();
        DomainObjectFactory.registerInstantiator(ACSObject.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);

        /*** Party ***/
        // We use the same instantiator as for ACSObject because party is
        // abstract so we don't need to override doNewInstance().
        DomainObjectFactory.registerInstantiator(Party.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);

        /*** User ***/
        instantiator = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new User(dataObject);
                }
            };
        DomainObjectFactory.registerInstantiator
            (User.BASE_DATA_OBJECT_TYPE, instantiator);

        /*** Group ***/
        instantiator = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Group(dataObject);
                }
            };
        DomainObjectFactory.registerInstantiator(Group.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);

        /*** Role ***/
        instantiator = new DomainObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Role(dataObject);
                }
            };
        DomainObjectFactory.registerInstantiator(Role.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);
    }

    /* Register URLFinders with the URLService */
    private void setupURLService() {
        // PackageInstance is the only kernel object type for which kernel
        // can provide a URLFinder.  Other object types could have
        // finders registered for them by other initializers (in UI packages).
        // For PackageInstance, urls are determined from the mount points on
        // the site map.
        URLService.registerFinder(PackageInstance.BASE_DATA_OBJECT_TYPE,
                                  new GenericURLFinder(""));
    }

    protected void doShutdown() {
        // Empty
    }
}
