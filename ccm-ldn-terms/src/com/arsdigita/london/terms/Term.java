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

import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

/**
 * Instances of this class represent entries in a domain
 * of terms.
 *
 * Although terms are currently modelled using the
 * categorization service, it is not neccessarily going
 * remain this way, hence the getModel() method is
 * protected.
 */
public class Term extends ACSObject {

    private static final Logger s_log = Logger.getLogger(Term.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.london.terms.Term";

    public static final String UNIQUE_ID = "uniqueID";
    public static final String IN_ATOZ = "inAtoZ";
    public static final String SHORTCUT = "shortcut";

    public static final String DOMAIN = "domain";
    public static final String MODEL = "model";

    public static final String NAME = MODEL + "." + Category.NAME;
    public static final String DESCRIPTION = MODEL + "." + Category.DESCRIPTION;

    Term() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected Term(String type) {
        super(type);
    }

    Term(DataObject dobj) {
        super(dobj);
    }

    /**
     * @see #create(String, String, boolean, String, Domain)
     */
    public static Term create(Integer uniqueID,
            String name,
            boolean inAtoZ,
            String shortcut,
            Domain domain) {
        return create(String.valueOf(uniqueID), name, inAtoZ, shortcut, domain);
    }
    
    /**
     * Creates a new term within a domain. All
     * parameters are required except for shortcut
     * @param uniqueID the unique identifier for the term
     * @param name the name of the term
     * @param inAtoZ whether it is relevant for an A-Z listing
     * @param shortcut the url shortcut for the domain
     * @param domain the domain containing this term
     * @return the newly created term
     */
    public static Term create(String uniqueID,
                              String name,
                              boolean inAtoZ,
                              String shortcut,
                              Domain domain) {
        Term term = new Term();

        term.set(MODEL, new Category());

        term.setUniqueID(uniqueID);
        term.setDomain(domain);
        term.setName(name);

        term.setInAtoZ(inAtoZ);
        term.setShortcut(shortcut);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Created term " + term.getID() + " with unique id " +
                        uniqueID + " and name " + name + " in domain " +
                        domain);
        }

        return term;
    }

    /**
     *  Creates a new term using an existing model category.
     */
    static Term create(Category cat,
                       String uniqueID,
                       boolean inAtoZ,
                       String shortcut,
                       Domain domain) {
        Term term = new Term();
        term.set(MODEL, cat);
        term.setUniqueID(uniqueID);
        term.setDomain(domain);
        term.setName(cat.getName());
        term.setInAtoZ(inAtoZ);
        term.setShortcut(shortcut);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Created term " + term.getID() + " with unique id " +
                        uniqueID + " and name " + cat.getName() + " in domain " +
                        domain + " using model category: " + cat);
        }
        return term;
    }


    private void setUniqueID(String uniqueID) {
        Assert.exists(uniqueID, String.class);
        set(UNIQUE_ID, uniqueID);
    }

    /**
     * Retrieves the unique identifier for this term.
     */
    public String getUniqueID() {
        return (String)get(UNIQUE_ID);
    }

    /**
     * Updates the name of this term
     * @param name the term's new name
     */
    public void setName(String name) {
        Assert.exists(name, String.class);
        getModel().setName(name);
        getModel().setURL(cleanURL(name));
    }

    /**
     * Retrieves the name of this term
     * @return the name of the term
     */
    public String getName() {
        return getModel().getName();
    }

    /**
     * Updates the description of this term
     * @param description the term's new description
     */
    public void setDescription(String description) {
        Assert.exists(description, String.class);
        getModel().setDescription(description);
    }

    /**
     * Retrieves the description of this term
     * @return the description of the term
     */
    public String getDescription() {
        return getModel().getDescription();
    }

    /**
     * Update the flag indicating whether this
     * term is suitable for inclusion in an A-Z
     * @param inAtoZ the new value for the flag
     */
    public void setInAtoZ(boolean inAtoZ) {
        set(IN_ATOZ, Boolean.valueOf(inAtoZ));
    }

    /**
     * Determines whether the term is suitable
     * for inclusion in an A-Z
     */
    public boolean isInAtoZ() {
        return ((Boolean)get(IN_ATOZ)).booleanValue();
    }

    /**
     * Update the shortcut for this term
     * @param the new value for the shortcut
     */
    public void setShortcut(String shortcut) {
        set(SHORTCUT, shortcut);
    }

    /**
     * Retrieves the URL fragment forming a shortcut
     * to this term
     */
    public String getShortcut() {
        return (String)get(SHORTCUT);
    }

    private void setDomain(Domain domain) {
        Assert.exists(domain, Domain.class);
        setAssociation(DOMAIN, domain);
    }

    /**
     * Retrieves the domain containing this term
     * @return the domain containing this term
     */
    public Domain getDomain() {
        return (Domain)DomainObjectFactory
            .newInstance((DataObject)get(DOMAIN));
    }

    public Category getModel() {
        return (Category)DomainObjectFactory
            .newInstance((DataObject)get(MODEL));
    }

    /**
     * Is this term a non-preferred term (synonym)?
     * @return <code>true</code> if this term has at least one preferred term, otherwise <code>false</code>.
     */
    public boolean isNonPreferredTerm() {
        return !isPreferredTerm();
    }

    /**
     * Is this term a preferred term ?
     * @return <code>true</code> if this term has no preferred term, otherwise <code>false</code>.
     */
    public boolean isPreferredTerm() {
        DomainCollection dc = getPreferredTerms();
        try
        {
            return dc.isEmpty();
        }
        finally {
            dc.close();
        }
    }

    /**
     * Adds a narrower term to this term
     * @param term the narrower term
     * @param isDefault whether this is the default broader term
     */
    public void addNarrowerTerm(Term term,
                                boolean isDefault,
                                boolean isPreferred) {
        //Assert.isTrue(term.getDomain().equals(getDomain()),
        //             "narrower term is in this domain");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding narrower term " + term + " to " +
                        this + " isDefault?" + isDefault);
        }

        getModel().addChild(term.getModel());
        term.getModel().setEnabled(isPreferred);

        if (isDefault) {
            term.getModel().setDefaultParentCategory(getModel());
        }
    }
    
    /**
     * Removes a narrower term from this term
     * @param term the narrower term to remove
     */
    public void removeNarrowerTerm(Term term) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing narrower term " + term + " from " +
                        this);
        }

        getModel().removeChild(term.getModel());
    }

    /**
     * Retrieves the collection of narrower terms
     * @return a collection of narrower terms
     */
    public DomainCollection getNarrowerTerms() {
        DomainCollection terms = getDomain().getTerms();
        terms.addEqualsFilter("model.parents.id", getModel().getID());
        terms.addEqualsFilter("model.parents.link.relationType", "child");
        return terms;
    }


    /**
     * Retrieves the collection of narrower terms
     * for which this term is the default parent
     * @return a collection of narrower terms
     *
     */
    public DomainCollection getDefaultNarrowerTerms() {
        DomainCollection terms = getNarrowerTerms();
        terms.addEqualsFilter("model.parents.link.isDefault", Boolean.TRUE);
        return terms;
    }


    /**
     * Retrieves the collection of broader terms
     * @return a collection of broader terms
     */
    public DomainCollection getBroaderTerms() {
        DomainCollection terms = getDomain().getTerms();
        terms.addEqualsFilter("model.related.id", getModel().getID());
        terms.addEqualsFilter("model.related.link.relationType", "child");
        return terms;
    }

    /**
     * Retrieves the default broader term
     * @return the default broader term
     */
    public Term getBroaderTerm() {
        DomainCollection terms = getBroaderTerms();
        terms.addEqualsFilter("model.related.link.isDefault", Boolean.TRUE);
        if (terms.next()) {
            Term term = (Term)terms.getDomainObject();
            terms.close();
            return term;
        }
        throw new DataObjectNotFoundException(
            "No default broader term for " + getID());
    }

    /**
     * Adds a related term to this term
     * @param term the related term
     */
    public void addRelatedTerm(Term term) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding related term " + term + " to " +
                        this);
        }

        getModel().addRelatedCategory(term.getModel());
    }

    /**
     * Removes a related term to this term
     * @param term the related term
     */
    public void removeRelatedTerm(Term term) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing related term " + term + "from " +
                        this);
        }

        getModel().removeRelatedCategory(term.getModel());
    }

    /**
     * Retrieves the related terms within this terms
     * domain
     * @return the related terms in this domain
     */
    public DomainCollection getRelatedTerms() {
        DomainCollection terms = getDomain().getTerms();
        terms.addEqualsFilter("model.parents.id", getModel().getID());
        terms.addEqualsFilter("model.parents.link.relationType", Category.RELATED);
        return terms;
    }

    /**
     * Retrieves the related terms within this terms
     * domain
     * @param domain the domain to retrieve terms in
     * @return the related terms in the other domain
     */
    public DomainCollection getRelatedTerms(Domain domain) {
        DomainCollection terms = domain.getTerms();
        terms.addEqualsFilter("model.parents.id", getModel().getID());
        terms.addEqualsFilter("model.parents.link.relationType", Category.RELATED);
        return terms;
    }

    /**
     * Retrieve the related terms within any domain
     * @return the related terms in all domains
     */
    public DomainCollection getAllRelatedTerms() {
        DataCollection terms = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        terms.addEqualsFilter("model.parents.id", getModel().getID());
        terms.addEqualsFilter("model.parents.link.relationType", Category.RELATED);
        return new DomainCollection(terms);
    }

    /**
     * Adds a preferred term to this synonym term 
     * @param term the preferred term
     */
    public void addPreferredTerm(Term term) {
        // XXX currently isDefault attribute is ignored for synonyms
        // some "search terms" in LGCL/IPSV have multiple preferred terms,
        // unclear which one to take as a preferred choice
        getModel().addPreferredCategory(term.getModel());
    }

    /**
     * Removes a preferred term
     * @param term the preferred term
     */
    public void removePreferredTerm(Term term) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing preferred term " + term + "from " +
                        this);
        }
        getModel().removeRelatedCategory(term.getModel());
    }

    /**
     * Retrieves the preferred terms.
     * Empty when the current term is not a synonym. 
     * @return a collection of preferred terms
     */
    public DomainCollection getPreferredTerms() {
        DomainCollection terms = getDomain().getTerms();
        terms.addEqualsFilter("model.parents.id", getModel().getID());
        terms.addEqualsFilter("model.parents.link.relationType", Category.PREFERRED);
        return terms;
    }

    /**
     * Retrieves the non-preferred terms.
     * Empty when the current term has no synonyms. 
     * @return a collection of non-preferred terms
     */
    public DomainCollection getNonPreferredTerms() {
        DomainCollection terms = getDomain().getTerms();
        terms.addEqualsFilter("model.related.id", getModel().getID());
        terms.addEqualsFilter("model.related.link.relationType", Category.PREFERRED);
        return terms;
    }

    /**
     * Classifies an object against this term
     * @param obj the object to classify
     */
    public void addObject(ACSObject obj) {
        getModel().addChild(obj);
    }

    /**
     * Unclassifies an object against this term
     * @param obj the object to unclassify
     */
    public void removeObject(ACSObject obj) {
        getModel().removeChild(obj);
    }


    private String cleanURL(String name) {
        Perl5Util perl5 = new Perl5Util();
        return perl5.substitute("s/[^a-zA-Z0-9_]/-/g", name).toLowerCase();
    }

}
