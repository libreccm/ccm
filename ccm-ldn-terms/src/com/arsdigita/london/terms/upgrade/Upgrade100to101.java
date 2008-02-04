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

package com.arsdigita.london.terms.upgrade;


import com.arsdigita.london.util.Program;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.london.terms.Loader;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import org.apache.commons.cli.CommandLine;


public class Upgrade100to101 extends Program {
    
    public Upgrade100to101() {
        super("Upgrade 1.0.0 -> 1.0.1",
              "1.0.0",
              "");
    }

    protected void doRun(CommandLine cmdLine) {
        new Transaction() {
            public void doRun() {
                new KernelExcursion() {
                    public void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());
                        
                        Loader.setupApplication();
                    }
                }.run();
            }
        }.run();
    }

    public static void main(String[] args) {
        new Upgrade100to101().run(args);
    }
}
