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
package com.arsdigita.search;

import com.arsdigita.domain.GlobalObserverManager;
import com.arsdigita.runtime.DomainInitEvent;

import org.apache.log4j.Logger;

/**
 * Activates the appropriate search engine.
 *
 * There are no APIs intended for public use in this class.
 *
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 * @version $Id: $
 */
public class Initializer extends com.arsdigita.runtime.GenericInitializer {

    private static final Logger s_log = Logger.getLogger(Initializer.class);


    /**
     * Default (empty) Constructor
     */
    public Initializer() {
    }


    /**
     * An implementation of {@link Initializer#init(DomainInitEvent)}.
     *
     * @param evt The domain init event.
     **/
    public void init(DomainInitEvent evt) {
        s_log.debug("domain init procedure started ");

        if (Search.getConfig().getIndexerType().getObserver() != null) {
            s_log.info("registering observer for indexer: " +
                       Search.getConfig().getIndexerType());
            GlobalObserverManager gom = GlobalObserverManager.getManager();
            gom.addObserver(new SearchObserver());
        } else {
            s_log.info("Not registering a search observer");
        }

        s_log.debug("domain init procedure finished ");
    }
}
