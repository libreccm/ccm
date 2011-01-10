/*
 * Copyright (C) 2010 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
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


package com.arsdigita.portal;

import com.arsdigita.domain.DomainObject;
// import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;

import org.apache.log4j.Logger;


/**
 * Initializes the core portal package.
 *
 * Initializer is invoked by the add-method in the core initializer.
 *
 * @author pb
 * @version $Id: $
 */
// Not shure if this initializeris realy needed. In a short test - commenting
// the initializer out - made no difference. (pboy-2011-01-09)
public class Initializer extends CompoundInitializer {

    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    // Currently no configuration options for portlets available
    //private static PortalConfig s_conf= PortalConfig.getConfig();

    /**
     *
     */
    public Initializer() {
    }

        /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     */
    public void init(DomainInitEvent e) {
        s_log.debug("publishToFile.Initializer.init(DomainInitEvent) invoked");

        // Recursive invokation of init, is it really necessary??
        // On the other hand:
        // An empty implementations prevents this initializer from being executed.
        // A missing implementations causes the super class method to be executed,
        // which invokes the above added LegacyInitializer.
        // If super is not invoked, various other cms sub-initializer may not run.
        super.init(e);

        /*      From old Initializer system
        DomainObjectFactory.registerInstantiator
            (Portal.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Portal(dataObject);
                }
            });
        */
        e.getFactory().registerInstantiator
            (Portal.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(DataObject dataObject) {
                     return new Portal(dataObject);
                 }
             });

        s_log.debug("publishToFile.Initializer.init(DomainInitEvent) completed");
    }
}
