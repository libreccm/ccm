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

package com.arsdigita.cms.portlet;

import com.arsdigita.db.DbHelper;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.RuntimeConfig;

import org.apache.log4j.Logger;

/**
 * The CMS portlet initializer.
 *
 * Initializer is invoked by the add-method in the CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy &lt;pb@zes.uni-bremen.de&gt;
 * @version $Id: $
 */
public class Initializer extends CompoundInitializer {

    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    // Currently no configuration options for portlets available
    //private static PortalConfig s_conf= PortalConfig.getConfig();

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

    }

//  /**
//   * An empty implementation of {@link Initializer#init(DataInitEvent)}.
//   *
//   * @param evt The data init event.
//   */
//  public void init(DataInitEvent evt) {
//  }

    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     */
    public void init(DomainInitEvent e) {
        s_log.debug("publishToFile.Initializer.init(DomainInitEvent) invoked");

        // Recursive invokation of init, is it really necessary??
        // On the other hand:
        // An empty implementations prevents this initializer from being executed.
        // A missing implementations causes the super class method to be executed,
        // which invokes the above added LegacyInitializer.
        // If super is not invoked, various other cms sub-initializer may not run.
        super.init(e);

        // ContentDirectoryPortlet, currently not used.
        // Portlet of the same name is active in ccm-ldn-portal
        // ContentDirectoryPortlet();
        ContentDirectoryPortlet.initPortlet();

        // ContentItemPortlet
        ContentItemPortlet.initPortlet();

        // ContentSectionsPortlet
        ContentSectionsPortlet.initPortlet();

        // TaskPortlet
        TaskPortlet.initPortlet();

    }

}
