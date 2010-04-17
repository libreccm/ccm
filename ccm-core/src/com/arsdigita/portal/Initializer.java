/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portal;

import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import org.apache.log4j.Logger;

/**
 *
 *
 * Initializes the portal package.
 *
 * @author Justin Ross
 * @version $Id: Initializer.java 738 2005-09-01 12:36:52Z sskracic $
 */
public class Initializer implements com.arsdigita.initializer.Initializer {

    private static Logger s_log = Logger.getLogger(Initializer.class);

    private Configuration m_conf = new Configuration();

    public Initializer() throws InitializationException {
        /* Empty */
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() {
        DomainObjectFactory.registerInstantiator
            (Portal.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Portal(dataObject);
                }
            });
    }

    public void shutdown() {
        /* Empty */
    }

}
