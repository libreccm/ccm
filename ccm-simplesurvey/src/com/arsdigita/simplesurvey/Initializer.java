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
package com.arsdigita.simplesurvey;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

import org.apache.log4j.Logger;

/**
 * Initializes ccm-simplesurvey at each system startup.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 759 2005-09-02 15:25:32Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    /**
     * Constructor. Delegates to the old initializer system.
     */
    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-simplesurvey.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

    }

    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     */
    @Override
    public void init(DomainInitEvent e) {
        s_log.debug("SimpleSurvey Initializer.init(DomainInitEvent) invoked");
        super.init(e);

        /* Register object instantiator for Workspace (Content Center)        */
        e.getFactory().registerInstantiator
            (SimpleSurvey.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(DataObject dobj) {
                     return new SimpleSurvey(dobj);
                 }
             } );

    }
}
