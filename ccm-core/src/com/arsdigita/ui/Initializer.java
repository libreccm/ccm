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

package com.arsdigita.ui;

// import com.arsdigita.domain.DomainObject;
// import com.arsdigita.domain.DomainObjectInstantiator;
// import com.arsdigita.kernel.ACSObjectInstantiator;
// import com.arsdigita.persistence.DataObject;
// import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.ConfigError;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Initializes the core ui package.
 *
 * Initializer is invoked by the add-method in the core initializer.
 *
 * @author pb
 * @version $Id: $
 */
// public class Initializer extends CompoundInitializer {
public class Initializer extends com.arsdigita.runtime.GenericInitializer {

    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    /** Config object for the UI package   */
    private static UIConfig s_conf = UIConfig.getConfig();
    // s_log.debug("ui configuration loaded.");

    /**
     *
     */
    public Initializer() {
    }


    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * @param evt The context init event.
     */
    @Override
    public void init(ContextInitEvent evt) {
        s_log.debug("UI context initialization started.");

        List defaultLayout = (List) s_conf.getDefaultLayout();
        if (defaultLayout != null) {
            s_log.info("Processing default layout");
            SimplePage.setDefaultLayout(buildLayout(defaultLayout));
        }

        List apps = (List) s_conf.getApplicationLayouts();
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

        s_log.debug("UI context initialization completed");
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
                throw new ConfigError("cannot find component " + className);
            }

            s_log.info("Adding " + className + " to " + position);

            layout.addComponent(classObject,
                                position);
        }

        return layout;
    }

}
