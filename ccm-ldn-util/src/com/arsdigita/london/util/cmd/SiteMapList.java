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

package com.arsdigita.london.util.cmd;

import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.util.cmd.Program;

import org.apache.commons.cli.CommandLine;

public class SiteMapList extends Program {
    
    public SiteMapList() {
        super("Site Map List",
              "1.0.0",
              "");
    }
    
    protected void doRun(CommandLine cmdLine) {
        ApplicationCollection apps = 
            Application.retrieveAllApplications();
        apps.addOrder("primaryURL");
        
        while (apps.next()) {
            Application app = null;
            try {
                app = apps.getApplication();
            } catch (Throwable ex) {
                // Pending bz 113122
                continue;
            }
            
            System.out.println(app.getPath() + " -> " +
                               app.getApplicationType().getTitle());
            
            if (isVerbose()) {
                System.out.println(" Description: " + 
                                   app.getApplicationType().getDescription());
            } 
            if (isDebug()) {
                System.out.println(" Class: " + 
                                   app.getClass());
            }
        }
    }

    public static void main(String[] args) {
        new SiteMapList().run(args);
    }
}
