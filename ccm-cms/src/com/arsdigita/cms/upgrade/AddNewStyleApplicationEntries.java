/*
 * Copyright (C) 2011 Peter Boy <pb@zes.uni-bremen.de>. All Rights Reserved.
 * Copyright (C) 2013 Jens Pelzetter <jens.pelzetter@scientificcms.org>. All Rights Reserved.
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
package com.arsdigita.cms.upgrade;

import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.Loader;
import com.arsdigita.cms.Service;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.web.ApplicationType;

import org.apache.commons.cli.CommandLine;

/**
 * Update from CCM version 6.6.1 to 6.6.2 where loader has been refactored to
 * use (new style) applications in package com.arsdigita.web instead of old
 * style applications using com.arsdigita.kernel.[Package* & SiteNode].
 *
 * Affected are the packages  CMS Workspace and Service. They are now loaded
 * using new style application classes {@see ContentCenter} and {@see Service}.
 *
 * The task at hand is to add the necessary table entries for CMS ContentCenter 
 * and Service to the tables application_types and applications.
 *
 * @author pb
 */
public class AddNewStyleApplicationEntries extends Program {

    /**
     /* Constructor constructs a program object which initializes the CCM
     * runtime system and enables concatenation, so a following SQL script
     * may be executed.
     */
    public AddNewStyleApplicationEntries() {
        super("AddNewStyleApplicationEntries", "1.0.0", "", true, true);
    }

    /**
     * The mandatory main method
     * @param args
     */
    public static void main(final String[] args) {
        new AddNewStyleApplicationEntries().run(args);
    }

    /**
     * Worker method. Adds new style application entries.
     * 
     * @param cmdLine
     */
    @Override
    public void doRun(final CommandLine cmdLine) {

        new KernelExcursion() {
            @Override
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                final Session session = SessionManager.getSession();
                final TransactionContext tc = session.getTransactionContext();
                tc.beginTxn();

                //  Update CMS Workspace
                final ApplicationType contentCenterAppType = Loader.
                        loadContentCenterApplicationType();
                Loader.setupDefaultContentCenterApplicationInstance(contentCenterAppType);

                //  Update CMS Service
                final ApplicationType serviceAppType  = Loader.loadServiceApplicationType();
                Loader.setupDefaultServiceApplicationInstance(serviceAppType);

                tc.commitTxn();
            }

        }.run();
    }

}
