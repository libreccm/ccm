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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.db.DbHelper;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.runtime.LegacyInitializer;
import com.arsdigita.runtime.RuntimeConfig;

import org.apache.log4j.Logger;

/**
 * The CMS PublishT0File initializer.
 *
 * Initializer is invoked by the add-method in the CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy &lt;pb@zes.uni-bremen.de&gt;
 * @version $Id: Initializer.java 1428 2007-01-24 12:39:56Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    // Creates a s_logging category with name = to the full name of class
    private static Logger s_log = Logger.getLogger(Initializer.class);


    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        // Left over from CMS initializer
        // add(new PDLInitializer
        //     (new ManifestSource
        //      ("ccm-cms.pdl.mf",
        //       new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

        add(new LegacyInitializer("com/arsdigita/cms/publishToFile/enterprise.init"));

    }

    /**
     * An empty implementation of {@link Initializer#init(DataInitEvent)}.
     *
     * @param evt The data init event.
     */
    public void init(DataInitEvent evt) {
    }

    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     * In the future: This starts up the search threads according to the values in the
     * properties file
     */
    public void init(DomainInitEvent e) {

    }

    /**
     * An empty implementation of {@link Initializer#init(LegacyInitEvent)}.
     *
     * @param evt The legacy init event.
     */
    public void init(LegacyInitEvent evt) {}

    /**
     * Implementation of the {@link Initializer#close()} method.
     *
     * This implementation proceeds through the list of sub
     * initializers in order and invokes the close()
     * method of each sub initializer in turn.
     *
     * @param evt The legacy init event.
     **/
    public void close(ContextCloseEvent evt) {
        s_log.info("publishToFile.Initializer.destroy() invoked");

        QueueManager.stopWatchingQueue();

        s_log.info("publishToFile.Initializer.destroy() completed");
    }

}
