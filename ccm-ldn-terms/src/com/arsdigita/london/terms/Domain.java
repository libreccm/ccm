/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.terms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;

import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DeleteCheckObserver;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.ObservableDomainObject;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;


/**
 * A domain is an abstract space containing a set
 * of terms.
 */
public class Domain extends ObservableDomainObject {
    
    public void delete() throws PersistenceException {
        Category model = getModel();
        super.delete();
        model.delete();
    }

    private static final Logger s_log = Logger.getLogger(Domain.class);

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.terms.Domain";

    public static final String KEY = "key";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String VERSION = "version";
    public static final String RELEASED = "released";
    public static final String TERMS = "terms";
    public static final String MODEL = "model";

    Domain() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    protected Domain(String type) {
        super(type);
    }
    
    Domain(DataObject dobj) {
        super(dobj);
    }

    public void initialize() {
        super.initialize();
        
        if (false && isNew()) {
            Category model = new Category();
            model.setAbstract(true);
            set(MODEL, model);
        }
    }
    
    /**
     * Creates a new domain object. All parameters are
     * required, except for description.
     *
     * @param key the unique key identifying the domain
     * @param url the unique URL defining the domain
     * @param title the short name of the domain
     * @param description the optional long description
     * @param version the version string for the domain
     * @param released the date on which the version was released
     * @return the newly created domain
     */
    public static Domain create(String key,
                                URL url,
                                String title,
                                String description,
                                String version,
                                Date released) {
        Domain domain = new Domain();
        domain.setKey(key);

        Category model = new Category();
        model.setAbstract(true);
        domain.set(MODEL, model);

        domain.setURL(url);
        domain.setTitle(title);
        domain.setDescription(description);
        domain.setVersion(version);
        domain.setReleased(released);

        return domain;
    }
    
    /**
     * Retrieve a domain based on its unique key
     * @param key the unique key of the domain
     * @return the domain corresponding to the key
     * @throws DataObjectNotFoundException if no matching domain is found
     */
    public static Domain retrieve(String key) {
        DataCollection domains = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        
        domains.addEqualsFilter(KEY, key);
        
        if (domains.next()) {
            Domain domain = (Domain)
                DomainObjectFactory.newInstance(domains.getDataObject());
            domains.close();
            return domain;
        }
        throw new DataObjectNotFoundException(
            "Domain with key " + key + " not found"
        );
    }

    /**
     * Finds a domain based on its url
     * @param url the location of the domain
     * @return the domain corresponding to the url
     * @throws DataObjectNotFoundException if no matching domain is found
     */
    public static Domain find(URL url) {
        DataCollection domains = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        
        domains.addEqualsFilter(URL, url.toString());
        
        if (domains.next()) {
            Domain domain = (Domain)
                DomainObjectFactory.newInstance(domains.getDataObject());
            domains.close();
            return domain;
        }
        throw new DataObjectNotFoundException(
            "Domain with url " + url + " not found"
        );
    }
    
    
    /**
     * Retrieves the collection of all terms in this
     * domain.
     */
    public DomainCollection getTerms() {
        DataCollection terms = (DataCollection)get(TERMS);
        return new DomainCollection(terms);
    }

    /**
     * @see #getTerm(String)
     */
    public Term getTerm(Integer uniqueID) {
        return getTerm(String.valueOf(uniqueID));
    }

    /**
     * Retrieves the term within this domain with
     * the corresponding unique identifier.
     *
     * @param uniqueID the id of the term to retrieve
     * @return the term matching the unique id.
     */
    public Term getTerm(String uniqueID) {
        DomainCollection terms = getTerms();
        terms.addEqualsFilter(Term.UNIQUE_ID, uniqueID);
        if (terms.next()) {
            Term term = (Term)terms.getDomainObject();
            terms.close();
            return term;
        } 
        throw new DataObjectNotFoundException(
            "Term " + uniqueID + " not found in domain " + getKey()
        );
    }
    
    /**
     * Retrieves the collection of terms in this domain
     * which are associated with the object.
     * @param obj the object to get terms for
     * @return the collection of terms for this object
     */
    public DomainCollection getDirectTerms(ACSObject obj) {
        DomainCollection terms = getTerms();
        terms.addEqualsFilter("model.childObjects.id", obj.getID());
        return terms;
    }
    
    /*
     * Generates the collection of terms in another domain
     * by matching against terms in this domain.
     *
     * @param obj the object to get terms for
     * @param domain the domain to get terms for
     * @return the collection of terms for this object
     */
    /*
    public DomainCollection getImpliedTerms(ACSObject obj,
                                            Domain domain) {
        // XXX implement me
    }
    */

    public Category getModel() {
        return (Category)DomainObjectFactory
            .newInstance((DataObject)get(MODEL));
    }

    
    private void setKey(String key) {
        Assert.exists(key, String.class);
        set(KEY, key);
    }

    /**
     * Retrieves the unique key for this domain
     * @return this domain's unique key
     */
    public String getKey() {
        return (String)get(KEY);
    }
    
    /**
     * Updates the unique URL defining this domain
     * @param url the new unique url for this domain
     */
    public void setURL(URL url) {
        Assert.exists(url, URL.class);
        set(URL, url.toString());
    }
    
    /**
     * Retrieves the unique URL defining this domain
     * @return this domain's unique URL
     */
    public URL getURL() {
        String url = (String)get(URL);
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("Cannot parse url " + url, ex);
        }
    }
    
    /**
     * Updates the short title for this domain
     * @param title the new title
     */
    public void setTitle(String title) {
        Assert.exists(title, String.class);
        set(TITLE, title);
        getModel().setName(title);
    }
    
    /**
     * Retrieves the short title for this domain
     * @return this domain's title
     */
    public String getTitle() {
        return (String)get(TITLE);
    }

    /**
     * Updates the long description for this domain
     * @param description the domain's description
     */
    public void setDescription(String description) {
        set(DESCRIPTION, description);
        getModel().setDescription(description);
    }
    
    /** 
     * Retrieves the long description for this domain
     * @return the domain's description
     */
    public String getDescription() {
        return (String)get(DESCRIPTION);
    }
    
    /**
     * Updates the version key for this domain
     * @param the new version key
     */
    public void setVersion(String version) {
        Assert.exists(version, String.class);
        set(VERSION, version);
    }
    
    /**
     * Retrieves the version key for this domain
     * @return this domain's version key
     */
    public String getVersion() {
        return (String)get(VERSION);
    }

    /**
     * Updates the release date for this version
     * of the domain
     * @param released the new release date
     */
    public void setReleased(Date released) {
        Assert.exists(released, Date.class);
        set(RELEASED, released);
    }
    
    /**
     * Retrieves the release date for this version
     * of the domain
     * @return this domain's release date
     */
    public Date getReleased() {
        return (Date)get(RELEASED);
    }

    
    /**
     * Adds a root term to this domain
     * @param term the root term
     */
    public void addRootTerm(Term term) {
        Assert.isTrue(term.getDomain().equals(this),
                     "root term is in this domain");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding root term " + term + " to " + 
                        this);
        }

        getModel().addChild(term.getModel());
        term.getModel().setDefaultParentCategory(getModel());
    }
    
    public void removeRootTerm(Term term) {
        Assert.isTrue(term.getDomain().equals(this),
                     "root term is in this domain");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing root term " + term + " from " + 
                        this);
        }

        getModel().removeChild(term.getModel());
    }
    
    /**
     * Retrieves all the root terms
     * @return a collection of root terms
     */
    public DomainCollection getRootTerms() {
        DomainCollection terms = getTerms();
        terms.addEqualsFilter("model.parents.id", getModel().getID());
        terms.addEqualsFilter("model.parents.link.relationType", Category.CHILD);
        return terms;
    }
    
    /**
     * Retrieves all terms in the domain that have no children
     * @return a collection of leaf terms
     */
    public DomainCollection getLeafTerms() {
	DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.london.terms.LeafTerms");
	query.setParameter("domain", getKey());
	return new DomainCollection(new DataQueryDataCollectionAdapter(query, "leaf"));
    }
 
    /**
     * Retrieves any terms that are orphans
     */
    public DomainCollection getOrphanedTerms() {
        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.london.terms.OrphanTerms");
        query.setParameter("domain", getKey());
        return new DomainCollection(new DataQueryDataCollectionAdapter(query, "leaf"));
    }
    
    /**
     * Sets this domain as the root for an object
     * @param obj the object to set the root of
     * @param context the usage context
     */
    public void setAsRootForObject(ACSObject obj,
                                   String context) {
        Category.setRootForObject(obj,
                                  getModel(),
                                  context);
    }
    
    /**
     * Highly experimental. Don't use this
     */
    public DomainCollection getUseContexts() {
        DataCollection objs = SessionManager.getSession()
            .retrieve("com.arsdigita.categorization.UseContext");
        objs.addEqualsFilter("rootCategory.id",
                             getModel().getID());
        return new DomainCollection(objs);
    }
    
    /**
     * Retrieves a query summarizing the item count
     * for each term in the domain
     * @return the term item count summary
     */
    public TermItemCountQuery getTermItemCountSummary() {
        return new TermItemCountQuery(this);
    }

    /**
     * Retrieves the collection of terms in this domain
     * which are associated with the object directly or indirectly.
     * @param obj the object to get terms for
     * @return the collection of terms for this object
     */
    public DomainCollection getAllTerms(ACSObject obj) {
        DomainCollection terms = getTerms();
        terms.addEqualsFilter("model.roTransSubcats.childObjects.id", obj.getID());
        return terms;
    }

    /**
     * Ensure that the domain's terms are orphaned.
     */
    protected void beforeDelete() {
        Category category = getModel();
        DeleteCheckObserver.observe( category );

        category.deleteCategoryAndOrphan();
        super.beforeDelete();
    }

    public static Domain findByModel(Category rootCategory) {
        DataCollection domains = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        domains.addEqualsFilter(MODEL, rootCategory.getID());

        if (domains.next()) {
            Domain domain = (Domain) DomainObjectFactory.newInstance(domains.getDataObject());
            domains.close();
            return domain;
        }
        throw new DataObjectNotFoundException("Domain with model " + rootCategory + " not found");
    }
}
