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

package com.arsdigita.shortcuts;

import com.arsdigita.db.DbHelper;

import com.arsdigita.domain.DomainObject;

import com.arsdigita.kernel.ACSObjectInstantiator;

// import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.DataObject;

import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.ui.admin.ApplicationManagers;

/**
 * Initializes the shortcuts application
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    /**
     * Constructor
     */
    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-shortcuts.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    /**
     * Implementation of the init(DomainInitEvent) initializer.
     * @param e
     */
    @Override
    public void init(DomainInitEvent e) {
        super.init(e);

        e.getFactory().registerInstantiator(
            Shortcuts.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Shortcuts(dataObject);
                }
            });
        
        //Register the ApplicationManager implementation for this application
        ApplicationManagers.register(new ShortcutsAppManager());
    }


}
