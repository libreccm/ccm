/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;

/**
 *
 * @author Jim Parsons &lt;<a href="mailto:jparsons@redhat.com">jparsons@redhat.com</a>&gt;
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
    public DomainObject getDomainObject() {
        DomainObject domainObject = getResourceImpl();

        Assert.assertNotNull(domainObject);

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

        Assert.assertNotNull(rimpl);

        return rimpl;
    }

}
