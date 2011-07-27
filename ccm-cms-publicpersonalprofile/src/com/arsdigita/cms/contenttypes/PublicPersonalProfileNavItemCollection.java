package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavItemCollection extends DomainCollection {
    
    private Filter keyFilter = null;
    private Filter languageFilter = null;
    
    public PublicPersonalProfileNavItemCollection() {
        super(SessionManager.getSession().retrieve(PublicPersonalProfileNavItem.BASE_DATA_OBJECT_TYPE));
    }
    
    public PublicPersonalProfileNavItemCollection(
            final DataCollection dataCollection) {
        super(dataCollection);
    }
    
    public PublicPersonalProfileNavItem getNavItem() {
        return new PublicPersonalProfileNavItem(m_dataCollection.getDataObject());
    }
    
    public final void addKeyFilter(final String key) {
        keyFilter = this.addEqualsFilter(PublicPersonalProfileNavItem.KEY, 
                                           key);
    }
    
    public boolean removeKeyFilter(final String key) {
        boolean retVal = false;
        
        retVal = this.removeFilter(keyFilter);
        if (retVal == true) {
            keyFilter = null;
        } 
        return retVal;
    }
    
    public final void addLanguageFilter(final String language) {
        languageFilter = this.addEqualsFilter(PublicPersonalProfileNavItem.KEY, 
                                           language);
    }
    
    public boolean removeLanguageFilter(final String language) {
        boolean retVal = false;
        
        retVal = this.removeFilter(languageFilter);
        if (retVal == true) {
            languageFilter = null;
        } 
        return retVal;
    }
    
    public void removeAllFilters() {
        this.removeAllFilters();
    }
    
    public final String getKey() {
        if (this.isBeforeFirst()) {
            this.next();
        } 
        
        return (String) get(PublicPersonalProfileNavItem.KEY);
    }
    
    public final String getLanguage() {
        if (this.isBeforeFirst()) {
            this.next();
        } 
        
        return (String) get(PublicPersonalProfileNavItem.LANG);
    }
    
    public PublicPersonalProfileNavItem getNavItem(final String key,
                                                   final String language) {
        if (!(this.isBeforeFirst()) && key.equals(this.getKey()) && language.equals(this.getLanguage())) {
            return this.getNavItem();
        } else {
            this.rewind();
            
            while(this.next()) {
                if ( key.equals(this.getKey()) && language.equals(this.getLanguage())) {
                    return this.getNavItem();
                }
            }
        }
        
        return null;
    }
        
}
