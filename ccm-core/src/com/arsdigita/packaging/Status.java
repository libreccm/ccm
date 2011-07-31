/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.packaging;

import com.arsdigita.runtime.RegistryConfig;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.runtime.Startup;

/**
 * Status
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 * @version $Id: Status.java 736 2005-09-01 10:46:05Z sskracic 
 */
class Status extends Command {

    /**
     * Constructor
     */
    public Status() {
        super("status", "Report on the status of a CCM instance");
    }

    /**
     * Invoked from the central tool "MasterTool" to perform the status check.
     */
    public boolean run(String[] args) {
        String[] packages;
        if (args.length == 0) {
            RegistryConfig rc = new RegistryConfig();
            rc.load();
            packages = rc.getPackages();
        } else {
            packages = args;
        }

        if (packages.length == 0) { return true; }

        Checklist[] checks = new Checklist[packages.length];
        for (int i = 0; i < packages.length; i++) {
            checks[i] = Checklist.get(packages[i]);
        }

        boolean passed = true;
        for (int i = 0; i < checks.length; i++) {
            if (checks[i] != null) {
                passed &= checks[i].run
                    (Checklist.SCHEMA, new ScriptContext(null, null));
            }
        }
        if (!passed) { return false; }

        new Startup().run();
        Session ssn = SessionManager.getSession();

        for (int i = 0; i < checks.length; i++) {
            if (checks[i] != null) {
                passed &= checks[i].run
                    (Checklist.DATA, new ScriptContext(ssn, null));
            }
        }
        if (!passed) { return false; }

        for (int i = 0; i < checks.length; i++) {
            if (checks[i] != null) {
                passed &= checks[i].run
                    (Checklist.STARTUP, new ScriptContext(ssn, null));
            }
        }
        if (!passed) { return false; }

        return true;
    }

}
