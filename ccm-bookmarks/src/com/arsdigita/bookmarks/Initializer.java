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

package com.arsdigita.bookmarks;

import com.arsdigita.bookmarks.ui.BookmarkPortlet;
import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.kernel.*;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

import org.apache.log4j.Logger;


/**
 *
 * @author <a href="mailto:jparsons@redhat.com">Jim Parsons</a>
 */
public class Initializer extends CompoundInitializer {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger.getLogger
        (Initializer.class);

    /**
     * 
     */
    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-bookmarks.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }


    /**
     * 
     * @param e 
     */
    @Override
    public void init(DomainInitEvent e) {
	    s_log.info("Bookmarks app is initializing using .init(DomainInitEvent e)");
        super.init(e);

        /* Register object instantiator for Bookmarks Application   */
        e.getFactory().registerInstantiator(
            Bookmarks.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Bookmarks(dataObject);
                }
            });

        /* Register object instantiator for Bookmarks Portlet   */
        e.getFactory().registerInstantiator(
                BookmarkPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new BookmarkPortlet(dataObject);
                    }
                });

    }

}
