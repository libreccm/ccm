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
package com.arsdigita.simplesurvey;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @see Survey
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: ResponseCollection.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class ResponseCollection extends DomainCollection {

    protected ResponseCollection(DataCollection dataCollection) {
        super(dataCollection);
    }
    
    public Response getResponse() {
        DataObject dataObject = m_dataCollection.getDataObject();
 
        Response response = Response.retrieve(dataObject);

        Assert.exists(response, Response.class);

        return response;
    }
}
