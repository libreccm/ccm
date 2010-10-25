/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class RelationAttributeCollection extends DomainCollection {

    public static String ATTRIBUTE = RelationAttribute.ATTRIBUTE;
    public static String KEY = RelationAttribute.KEY;
    public static String LANGUAGE = RelationAttribute.LANGUAGE;
    public static String NAME = RelationAttribute.NAME;
    public static String DESCRIPTION = RelationAttribute.DESCRIPTION;
    private Filter m_attributeFilter = null;
    private Filter m_keyFilter = null;
    private Filter m_languageFilter = null;

    public RelationAttributeCollection() {
        super(SessionManager.getSession().retrieve(
                RelationAttribute.BASE_DATA_OBJECT_TYPE));
    }

    public RelationAttributeCollection(String attribute) {
        this();
        this.addAttributeFilter(attribute);
        this.addOrder(KEY + ", " + LANGUAGE);
    }

    public RelationAttributeCollection(String attribute, String key) {
        this(attribute);
        this.addKeyFilter(key);
    }

    public RelationAttributeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>RelationAttribute</code>.
     *
     * @return a <code>RelationAttribute</code> for the current position in the
     * collection.
     **/
    public RelationAttribute getRelationAttribute() {
        return new RelationAttribute(m_dataCollection.getDataObject());
    }

    // Modify filter
    public final void addAttributeFilter(String attribute) {
        m_attributeFilter = this.addEqualsFilter(ATTRIBUTE, attribute);
    }

    public boolean removeAttributeFilter(String attribute) {
        boolean retVal = false;
        retVal = this.removeFilter(m_attributeFilter);
        if (retVal == true) {
            m_attributeFilter = null;
        }
        return retVal;
    }

    public final void addKeyFilter(String key) {
        m_keyFilter = this.addEqualsFilter(KEY, key);
    }

    public boolean removeKeyFilter(String key) {
        boolean retVal = false;
        retVal = this.removeFilter(m_keyFilter);
        if (retVal == true) {
            m_keyFilter = null;
        }
        return retVal;
    }

    public void addLanguageFilter(String language) {
        m_languageFilter = this.addEqualsFilter(LANGUAGE, language);
    }

    public boolean removeLanguageFilter(String language) {
        boolean retVal = false;
        retVal = this.removeFilter(m_languageFilter);
        if (retVal == true) {
            m_languageFilter = null;
        }
        return retVal;
    }

    public void removeAllFilters() {
        this.removeAllFilters();
    }

    // Accessors
    public final String getKey() {
        return (String) get(KEY);
    }

    public final String getLanguage() {
        return (String) get(LANGUAGE);
    }

    // Get RelationAttribute in desired language
    public RelationAttribute getRelationAttribute(String key, String language) {

        // First, test the current element
        if(!this.isBeforeFirst() && key.equals(this.getKey()) && language.equals(this.getLanguage())) {

            return this.getRelationAttribute();

        } else {

            // Rewind the collection and search for a matching element
            this.rewind();
            while (this.next()) {
                if (this.getKey().equals(key) && this.getLanguage().equals(
                        language)) {
                    return this.getRelationAttribute();
                }
            }
        }

        // Nothing found
        return null;
    }

    public String getName() {
        return getRelationAttribute().getName();
    }

    public String getDescription() {
        return getRelationAttribute().getDescription();
    }

    // Tests
    public boolean hasLanguage(String language) {

        boolean retVal = false;
        this.addLanguageFilter(language);
        if (this.size() > 0) {
            retVal = true;
        }
        this.removeLanguageFilter(language);

        return retVal;
    }
}
