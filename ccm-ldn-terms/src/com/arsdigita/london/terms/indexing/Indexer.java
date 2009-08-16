/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.indexing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.List;

import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.indexing.kea.IndexerService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.ibm.icu.util.Calendar;

/**
 * The domain indexer is used for keyphrase extraction of content items.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class Indexer extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.london.terms.indexing.Indexer";

    public static final String DOMAIN = "domain";
    public static final String FILTER = "filter";
    public static final String LAST_MODIFIED_DATE = BasicAuditTrail.LAST_MODIFIED_DATE;
    public static final String LAST_MODIFIED_USER = BasicAuditTrail.LAST_MODIFIED_USER;

    public static Indexer retrieve(Domain domain) {
        Assert.exists(domain);

        DataCollection dc = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        try {
            dc.addEqualsFilter(DOMAIN, domain.getKey());
            return (dc.next() ? new Indexer(dc.getDataObject()) : null);
        } finally {
            dc.close();
        }
    }

    public static Indexer create(Domain domain) {
        Assert.exists(domain);

        Indexer indexer = new Indexer();
        indexer.set(DOMAIN, domain);
        indexer.updateLastModified();
        return indexer;
    }

    protected Indexer() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    protected Indexer(String type) {
        super(type);
    }

    protected Indexer(DataObject dobj) {
        super(dobj);
    }

    public Domain getDomain() {
        return (Domain) DomainObjectFactory.newInstance((DataObject) get(DOMAIN));
    }

    public Object getFilter() {
        byte[] bytes = (byte[]) get(FILTER);
        if (bytes == null) {
            return null;
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return object;
        } catch (IOException e) {
            throw new RuntimeException("Failed to get " + FILTER + " from " + this.getOID(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to read " + FILTER + " from " + this.getOID(), e);
        }
    }

    public Timestamp getLastModifiedDate() {
        return (Timestamp) get(LAST_MODIFIED_DATE);
    }

    public Party getLastModifiedUser() {
        return (Party) get(LAST_MODIFIED_USER);
    }

    public void setFilter(Object filter) {
        if (filter == null) {
            set(FILTER, null);
        } else {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(filter);
                oos.flush();
                set(FILTER, baos.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException("Failed to set " + FILTER + " on " + this.getOID(), e);
            }
        }
    }

    public void setLastModifiedDate(Timestamp timestamp) {
        set(LAST_MODIFIED_DATE, timestamp);
    }

    public void setLastModifiedUser(Party user) {
        set(LAST_MODIFIED_USER, user);
    }

    /**
     * Training the indexer using the current content items assigned
     * to the domain.
     */
    public void train() {
        // TODO iterate through all languages used
        String language = "en";
        IndexerService service = new IndexerService();
        Object filter = service.train(getDomain(), language);
        setFilter(filter);
        updateLastModified();
    }

    /**
     * Propose an index for a content item. 
     * @param item the item to be indexed
     * @param maxTerms the maximum number of terms in the index
     * @return an index of terms from the domain
     */
    public List<RankedTerm> index(ContentItem item, int maxTerms) {
        IndexerService service = new IndexerService();
        Object filter = getFilter();
        if (filter == null) {
            throw new IllegalStateException("Indexer " + getOID() + " has not been trained");
        }
        List<RankedTerm> terms = service.controlledIndex(getFilter(), getDomain(), maxTerms, item);
        return terms;
    }

    /**
     * Update the last modified user and timestamp.
     */
    protected void updateLastModified() {
        setLastModifiedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        Party party = Kernel.getContext().getEffectiveParty();
        if (party == null) {
            party = Kernel.getSystemParty();
        }
        setLastModifiedUser(party);
    }
}
