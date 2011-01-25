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
package com.arsdigita.ui;

import com.arsdigita.initializer.Configuration;
import org.apache.log4j.Logger;
import java.util.Iterator;
import java.util.List;

import com.arsdigita.initializer.InitializationException;


public class LegacyInitializer implements com.arsdigita.initializer.Initializer {

    private Configuration m_conf = new Configuration();
    private static final Logger s_log =
        Logger.getLogger(LegacyInitializer.class);

    public static final String DEFAULT_LAYOUT = "defaultLayout";
    public static final String APPLICATION_LAYOUTS = "applicationLayouts";

    public LegacyInitializer() {
        m_conf.initParameter
            (DEFAULT_LAYOUT,
             "The default layout for the SimplePage class",
             List.class);
        m_conf.initParameter
            (APPLICATION_LAYOUTS,
             "The customized layout for applications using the SimplePage class",
             List.class);

    }

    public void startup() throws InitializationException {
        s_log.info("Starting UI initializer");

        List defaultLayout = (List)m_conf.getParameter(DEFAULT_LAYOUT);
        if (defaultLayout != null) {
            s_log.info("Processing default layout");
            SimplePage.setDefaultLayout(buildLayout(defaultLayout));
        }

        List apps = (List)m_conf.getParameter(APPLICATION_LAYOUTS);
        if (apps != null) {
            Iterator i = apps.iterator();
            while (i.hasNext()) {
                List app = (List)i.next();

                String name = (String)app.get(0);
                List layout = (List)app.get(1);

                s_log.info("Processing layout for " + name);

                SimplePage.setLayout(name,
                                     buildLayout(layout));
            }
        }

        s_log.info("UI initializer completed");
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void shutdown() {
        // nada
    }

    private SimplePageLayout buildLayout(List desc) {
        SimplePageLayout layout = new SimplePageLayout();

        Iterator comps = desc.iterator();
        while (comps.hasNext()) {
            List comp = (List)comps.next();

            String position = (String)comp.get(0);
            String className = (String)comp.get(1);
            
            Class classObject;
            try {
                classObject =  Class.forName(className);
            } catch (ClassNotFoundException ex) {
                throw new InitializationException(
                    "cannot find component " + className, ex
                );
            }
            
            s_log.info("Adding " + className + " to " + position);

            layout.addComponent(classObject,
                                position);
        }

        return layout;
    }
}
