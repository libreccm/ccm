/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 * Base initializer for all WAF applications. Extend this class to
 * implement your own initializer. Runs each initializer in a
 * KernelExcursion with system privileges.
 *
 * @author Richard Li
 */
public abstract class BaseInitializer
    implements com.arsdigita.initializer.Initializer {

    public void startup() throws InitializationException {
        if (Kernel.getSystemParty() == null) {
            final DatabaseTransaction transaction = new DatabaseTransaction();

            transaction.begin();

            setupSystemParty();

            transaction.end();
        }

        KernelExcursion rootExcursion = new KernelExcursion() {
                public void excurse() {
                    setTransaction(new DatabaseTransaction());
                    setEffectiveParty(Kernel.getSystemParty());
                    doStartup();
                }
            };
        rootExcursion.run();
    }

    public void shutdown() throws InitializationException {
        KernelExcursion rootExcursion = new KernelExcursion() {
                public void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    doShutdown();
                }
            };
        rootExcursion.run();
    }

    public abstract Configuration getConfiguration();

    /**
     * Code that should be executed at initializer startup should go
     * inside this method.
     */
    protected abstract void doStartup();

    /**
     * Code that should be executed at initializer shutdown should go
     * inside this method.
     */
    protected abstract void doShutdown();

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
