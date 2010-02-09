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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.DomainInitEvent;
import org.apache.log4j.Logger;

/**
 * The CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: AddressInitializer.java 1597 2007-07-10 16:27:26Z p_boy $
 */
public class AddressInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(AddressInitializer.class);

    public AddressInitializer() {
        super("ccm-cms-types-address.pdl.mf",
              Address.BASE_DATA_OBJECT_TYPE);
    }

    public void init(DomainInitEvent evt) {
        super.init(evt);
        
        // register the DomainObjectInstantiator from ISO Country
        DomainObjectInstantiator instIsocountry =
            new DomainObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new IsoCountry(dataObject);
                }
                public DomainObjectInstantiator resolveInstantiator(DataObject obj) {
                    return this;
                }
            };
        DomainObjectFactory.registerInstantiator(
            IsoCountry.BASE_DATA_OBJECT_TYPE,
            instIsocountry
        );
    }

    public String[] getStylesheets() {
        return new String[] { "/static/content-types/com/arsdigita/cms/contenttypes/Address.xsl" };
    }
}
