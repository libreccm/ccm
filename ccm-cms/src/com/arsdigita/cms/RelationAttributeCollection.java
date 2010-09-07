/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;

/**
 *
 * @author quasi
 */
public class RelationAttributeCollection extends ACSObjectCollection {

    public static String ATTRIBUTE = RelationAttribute.ATTRIBUTE;
    public static String KEY = RelationAttribute.KEY;
    public static String LANGUAGE = RelationAttribute.LANGUAGE;

    private Filter m_attributeFilter = null;
    private Filter m_keyFilter = null;
    private Filter m_languageFilter = null;

    public RelationAttributeCollection() {
        super(SessionManager.getSession().retrieve(RelationAttribute.BASE_DATA_OBJECT_TYPE));
    }

    public RelationAttributeCollection(String attribute) {
        this();
        this.addAttributeFilter(attribute);
    }

    public RelationAttributeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>CategoryLocalization</code>.
     *
     * @return a <code>CategoryLocalization</code> for the current position in the
     * collection.
     **/
    public RelationAttribute getRelationAttribute() {
        return (RelationAttribute) getDomainObject();
    }

    @Override
    public ACSObject getACSObject() {
        return getRelationAttribute();
    }


    // Modify filter
    public void addAttributeFilter(String attribute) {
        m_attributeFilter = this.addEqualsFilter(ATTRIBUTE, attribute);
    }

    public boolean removeAttributeFilter(String attribute) {
        boolean retVal = false;
        retVal = this.removeFilter(m_attributeFilter);
        if(retVal == true) {
            m_attributeFilter = null;
        }
        return retVal;
    }

    public void addKeyFilter(String key) {
        m_keyFilter = this.addEqualsFilter(KEY, key);
    }

    public boolean removeKeyFilter(String key) {
        boolean retVal = false;
        retVal = this.removeFilter(m_keyFilter);
        if(retVal == true) {
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
        if(retVal == true) {
            m_languageFilter = null;
        }
        return retVal;
    }

    public void removeAllFilters() {
        this.removeAllFilters();
    }

    // Accessors
    public String getName() {
        return getRelationAttribute().getName();
    }

    public String getDescription() {
        return getRelationAttribute().getDescription();
    }
}
