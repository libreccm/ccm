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
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.web.Application;

import java.math.BigDecimal;

import org.apache.log4j.Logger;


/**
 * Application domain class for the CMS module user entry page (content-center)
 *
 * @author pb
 * @version $Id: Workspace.java $
 */
public class Workspace extends Application {

    private static final Logger s_log = Logger.getLogger(ContentSection.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.Workspace";
    public static final String PACKAGE_KEY = "content-center";
    public static final String INSTANCE_NAME = "Content Center";
    public static final String DISPATCHER_CLASS =
                               "com.arsdigita.cms.dispatcher.ContentCenterDispatcher";
    public final static String STYLESHEET = 
                               "/packages/content-section/xsl/content-center.xsl";

    /**
     * Constructor
     * @param oid
     * @throws DataObjectNotFoundException
     */
    public Workspace(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Workspace(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public Workspace(DataObject dataObject) {
        super(dataObject);
    }

}
