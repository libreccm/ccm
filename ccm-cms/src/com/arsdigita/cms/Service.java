/*
 * Copyright (C) 2010 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
// import com.arsdigita.persistence.DataAssociation;
// import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.web.Application;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

//  WORK IN PROGRESS !!

/**
 * Application domain class for the CMS Service application, a CMS module which
 * is used by the Content Management System as a store for global resources
 * and assets.
 *
 * @author pb
 * @version $Id: Service.java $
 */
public class Service extends Application {

    private static final Logger s_log = Logger.getLogger(ContentSection.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.Service";
    public static final String PACKAGE_KEY = "cms-service";
    public static final String INSTANCE_NAME = "CMS Service";
    public static final String DISPATCHER_CLASS =
                               "com.arsdigita.cms.dispatcher.ServiceDispatcher";
    // Service has no direct user interface, therefore no styesheet
    // public final static String STYLESHEET =
    //                         "/packages/content-section/xsl/content-center.xsl";

    /**
     * Constructor
     * @param oid
     * @throws DataObjectNotFoundException
     */
    public Service(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Service(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public Service(DataObject dataObject) {
        super(dataObject);
    }

}
