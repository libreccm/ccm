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
 */

package com.arsdigita.london.shortcuts;

import com.arsdigita.loader.PackageLoader;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;

import com.arsdigita.runtime.ScriptContext;

import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Loads the shortcuts application and type.
 *
 * @author Daniel Berrange
 * @version $Id: Loader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupShortcuts();
            }
        }.run();
    }

    private void setupShortcuts() {
        ApplicationType type = ApplicationType
            .createApplicationType("shortcuts",
                                   "CCM Shortcuts Admin",
                                   Shortcuts.BASE_DATA_OBJECT_TYPE);
        type.setDescription("CCM shortcuts administration");

        Application admin = Application.retrieveApplicationForPath("/admin/");

        Application app =
            Application.createApplication(type,
                                          "shortcuts",
                                          "CCM Shortcuts Admin",
                                          admin);
    }
}
