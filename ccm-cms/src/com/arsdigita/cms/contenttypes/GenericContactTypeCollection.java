/*
 * GenericContactEntryCollection.java
 *
 * Created on 13. Mai 2009, 12:32
 *
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;

/**
 *
 * @author quasi
 */
public class GenericContactTypeCollection extends DomainCollection {

    /**
     * Creates a new instance of GenericContactEntryCollection
     */
    public GenericContactTypeCollection() {
        this((DataCollection) SessionManager.getSession().retrieve(GenericContactType.BASE_DATA_OBJECT_TYPE));
        this.addOrder("key, language");
    }

    public GenericContactTypeCollection(String key) {
        this();
        this.addEqualsFilter("key", key);
    }

    public GenericContactTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    public final String getKey() {
        return (String) getContactType().getKey();
    }

    public final String getLanguage() {
        return (String) getContactType().getLanguage();
    }

    public final String getName() {
        return (String) getContactType().getName();
    }

    public GenericContactType getContactType() {
        return new GenericContactType(m_dataCollection.getDataObject());
    }

    // Get ContactType in desired language
    public GenericContactType getContactType(String key, String language) {

        // First, test the current element
        if(this.getKey().equals(key) && this.getLanguage().equals(language)) {

            return this.getContactType();

        } else {

            // Rewind the collection and search for a matching element
            this.rewind();
            while(this.next()) {
                if(this.getKey().equals(key) && this.getLanguage().equals(language)){
                    return this.getContactType();
                }
            }
        }

        // Nothing found
        return null;
    }

    public void filterLanguage(String language) {
        this.addEqualsFilter("language", language);
    }

    public boolean hasLanguage(String language) {

        boolean retVal = false;
        Filter languageFilter = this.getFilterFactory().equals("language", language);

        this.addFilter(languageFilter);
        if(this.size() > 0) {
            retVal = true;
        }
        this.removeFilter(languageFilter);

        return retVal;
    }

}
