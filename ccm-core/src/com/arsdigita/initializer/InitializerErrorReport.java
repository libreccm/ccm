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
package com.arsdigita.initializer;

import com.arsdigita.logging.ErrorReport;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This is an advanced servlet error report generator
 * which dumps practically all the information it can
 * find about the servlet request to the logs. It also
 * sets a request attribute containing the ACS Error Report
 * (guru meditation) code.
 */
public class InitializerErrorReport extends ErrorReport {
    
    private Initializer m_initializer;

    public InitializerErrorReport(Throwable throwable,
                                  Initializer initializer) {
        super(throwable);
        
        m_initializer = initializer;
        
        // Take great care such that if something goes
        // wrong while creating the error report, we don't
        // let the new exception propagate thus loosing the
        // one we're actually trying to report on.
        try {
            addInitializer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addInitializer() {
        if (m_initializer == null) {
            return;
        }

        ArrayList lines = new ArrayList();

        lines.add("Classname: "+ m_initializer.getClass().getName());

        addSection("Initializer summary", lines);
    }

    private void addConfig() {
        if (m_initializer == null) {
            return;
        }

        ArrayList lines = new ArrayList();
        
        Configuration config = m_initializer.getConfiguration();
        Iterator params = config.getParameterNames().iterator();
        while (params.hasNext()) {
            String name = (String)params.next();
            Object value = config.getParameter(name);
            
            lines.add(name + ": " + value);
        }

        addSection("Initializer parameters", lines);
    }
}
