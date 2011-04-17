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
package com.arsdigita.docrepo;

//import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;

//import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jim Parsons &lt;jparsons@redhat.com&gt;
 */
public class ResourceImplCollection extends DomainCollection {

    private static final Logger s_log = Logger.getLogger
        (ResourceImplCollection.class);

    public ResourceImplCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Get the current item as a ResourceImpl domain object.
     *
     * @return a ResourceImpl domain object.
     * @post return != null
     */
    @Override
    public DomainObject getDomainObject() {
        DomainObject domainObject = getResourceImpl();

        // Assert.assertNotNull(domainObject);
        Assert.exists(domainObject);

        return domainObject;
    }
    /**
     * Get the current item as a ResourceImpl domain object.
     *
     * @return a ResourceImpl domain object.
     * @post return != null
     */
    public ResourceImpl getResourceImpl() {
        DataObject dataObject = m_dataCollection.getDataObject();

        File rimpl = File.retrieveFile(dataObject);

     // Assert.assertNotNull(rimpl);
        Assert.exists(rimpl);

        return rimpl;
    }

}
