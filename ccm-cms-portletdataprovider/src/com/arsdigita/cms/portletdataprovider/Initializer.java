/*
 * Copyright (c) 2014 Jens Pelzetter
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
 */package com.arsdigita.cms.portletdataprovider;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class Initializer extends CompoundInitializer {

    public Initializer(){
        super();
        final String jdbcUrl = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(jdbcUrl);

        add(new PDLInitializer(new ManifestSource("ccm-cms-portletdataprovider.pdl.mf",
                                                  new NameFilter(DbHelper.
            getDatabaseSuffix(database), "pdl"))));
   }

    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        DomainObjectFactory.registerInstantiator(
            PortletDataProvider.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(final DataObject dataObject) {
                    return new PortletDataProvider(dataObject);
                }

            });
    }

}
