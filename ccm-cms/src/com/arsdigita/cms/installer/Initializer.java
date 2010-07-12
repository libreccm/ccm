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

package com.arsdigita.cms.installer;

import com.arsdigita.cms.LoaderConfig;
import com.arsdigita.runtime.CompoundInitializer;
// import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;

import org.apache.log4j.Logger;

// CURRENT STATUS:
// (Simple) Migration of the Old Initializer code of this package to the new
// initializer system. Current goal is a pure replacement with as less code
// changes as possible.
// In a second step a restructure of the code will be done.

/**
 * XXX Reformulate according to the code development!
 * 
 * Initializes the CMS package.
 *
 * <p>The main initializer for the Content Management System.</p>
 *
 * <p>Initializes the Content Management System, including the Content Center
 * and CMS Service applications and CMS Mime Types service. This class also
 * optionally initializes user-defined content types and user-defined content
 * sections.</p>
 *
 *
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 * @version $Id: $
 *
 */
public class Initializer extends CompoundInitializer {


    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    // private static PublishToFileConfig s_conf= PublishToFileConfig.getConfig();
    private static final LoaderConfig s_conf = new LoaderConfig();
//  LoaderConfig conf = LoaderConfig.getConfig();

//  As an example
//  private static final ContentSectionConfig s_config = new ContentSectionConfig();

//  static {
//      s_config.load();
//  }

    public Initializer() {
      //final String url = RuntimeConfig.getConfig().getJDBCURL();
      //final int database = DbHelper.getDatabaseFromURL(url);

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
    public void init(DomainInitEvent evt) {
        s_log.debug("CMS.installer.Initializer.init(DomainInitEvent) invoked");

        // Recursive invokation of init, is it really necessary??
        // On the other hand:
        // An empty implementations prevents this initializer from being executed.
        // A missing implementations causes the super class method to be executed,
        // which invokes the above added LegacyInitializer.
        // If super is not invoked, various other cms sub-initializer may not run.
        super.init(evt);

    /*
     * Imported from LegacyInitializer:
     * TASK: 
     * Check if CMS package type exists. If not, then:
     *
     * <ol>
     *   <li>create CMS package type</li>
     *   <li>create Workspace package type and instance</li>
     *   <li>create CMS Service package type and instance</li>
     * </ol>
     */


            // Update master object if upgrading from old versioning
            // XXX: shouldn't we just gut this section (and
            // VersioningUpgrader)? It is an upgrade fix from 5.1 or
            // earlier, and relying on VersionedACSObject is
            // deprecated
            // pb: But see: ContentItem.java, l. 1650 ff. (setVersion Recursively)
            // VersionUptrader is used by lifecycle.
            // pb begin
            // final boolean updateMaster =
            //     ((Boolean)m_conf.getParameter(UPDATE_MASTER)).booleanValue();
            // if (updateMaster) {
            //     VersioningUpgrader.updateMasterObject();
            // }
            // pb end

            // From comment in original enterprise.init file:
            // XXX: ItemDispatcher is no longer used. Is the following
            // still a valid enterprise.init parameter? Do we need to
            // set ContentSectionServlet.s_cacheItems instead of the
            // below (which is currently always true), or does this go
            // away entirely?
            // NB. true is default for ItemDispatcher!
            // This would be a typical domain init initialization task.
            // final boolean cacheItems =
            //     s_conf.
            // s_log.debug("Set cache items to " + cacheItems);
            // ItemDispatcher.setCacheItems(cacheItems);

            final String workspaceURL = s_conf.getWorkspaceURL();
            final String contentCenterMap = s_conf.getContentCenterMap();
            ContentCenterSetup centerSetup = new ContentCenterSetup(
                workspaceURL,
                contentCenterMap);

            centerSetup.run();


        s_log.debug("CMS.installer.Initializer.init(DomainInitEvent) completed");
    }
}
