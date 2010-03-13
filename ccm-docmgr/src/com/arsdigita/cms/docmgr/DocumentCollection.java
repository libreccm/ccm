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

package com.arsdigita.cms.docmgr;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;


/**
 *
 * @author Crag Wolfe
 */
public class DocumentCollection extends DomainCollection {

    private static final Logger s_log = Logger.getLogger
        (DocumentCollection.class);

    public DocumentCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Get the current item as a Document domain object.
     *
     * @return a Document domain object.
     * @post return != null
     */
    public DomainObject getDomainObject() {
        DomainObject domainObject = getDocument();

        Assert.exists(domainObject);

        return domainObject;
    }
    /**
     * Get the current item as a Document domain object.
     *
     * @return a Document domain object.
     * @post return != null
     */
    public Document getDocument() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Document doc = Document.retrieveDocument(dataObject);

        Assert.exists(doc);

        return doc;
    }

}
