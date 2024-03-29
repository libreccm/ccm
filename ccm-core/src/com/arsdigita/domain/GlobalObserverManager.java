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
 *
 */
package com.arsdigita.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The GlobalObserverManager class is a singleton class that allows observers
 * implementing the GlobalObserver interface to be registered.
 *
 * Once such observers are registered they have the opportunity to observe
 * every single observable domain object that is created. The
 * GlobalObserver.shouldObserve(DomainObject) method can be used by an observer
 * to select which objects to observe.
 * 
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: GlobalObserverManager.java 2070 2010-01-28 08:47:41Z pboy $
 */

public class GlobalObserverManager {

    private static final GlobalObserverManager s_manager =
        new GlobalObserverManager();


    /**
     * Returns the global observer manager. This is a singleton class that
     * allows the "global" observers, or observers of any
     */
    public static final GlobalObserverManager getManager() {
        s_log.debug("getInstance of GlobalObserverManager invoked.");
        return s_manager;
    }

    private static final Logger s_log = Logger.getLogger(GlobalObserverManager.class);

    private List m_observers = new ArrayList();

    private GlobalObserverManager() {}


    /**
     * Adds a new observer.
     *
     * @param doo the new observer
     */

    public void addObserver(GlobalObserver go) {
        s_log.debug("addObserver invoked.");
        m_observers.add(go);
    }


    /**
     * Returns the global observers.
     *
     * @return The observers for the given domain object.
     */
    Iterator getObservers() {
        return m_observers.iterator();
    }

}
