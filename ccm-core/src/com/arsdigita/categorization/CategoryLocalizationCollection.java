/*
 * CategoryLocalizationCollection.java
 *
 * Created on 19. Januar 2008, 13:24
 *
 * Author: Quasimodo
 */

package com.arsdigita.categorization;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 * Represents a collection of categoryLocalizations.
 *
 * <p>Instances of this class are produced by various methods in {@link
 * Category} and other classes. See, for example, {@link Category#getChildren()}
 * or {@link Category#getDescendants()}.</p>
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Revision: #15 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public class CategoryLocalizationCollection extends ACSObjectCollection {
    
    public CategoryLocalizationCollection(Category category) {
        super(category.getLocalizations().getDataCollection());
    }
    
    public CategoryLocalizationCollection(DataCollection dataCollection) {
        super(dataCollection);
    }
    
    /**
     * Returns the locale of the categoryLocalization.
     *
     * @return the category locale.
     * @see Category#getLocale()
     */
    public String getLocale() {
        return getCategoryLocalization().getLocale();
    }
    
    /**
     * Returns the name of the category.
     *
     * @return the category name.
     * @see Category#getName()
     */
    public String getName() {
        return getCategoryLocalization().getName();
    }
    
    /**
     * Returns the description.
     *
     * @return the description
     * @see Category#getDescription()
     */
    public String getDescription() {
        return getCategoryLocalization().getDescription();
    }
    
    /**
     * Returns the URL.
     *
     * @return the URL
     * @see Category#getURL()
     */
    public String getURL() {
        return getCategoryLocalization().getURL();
    }
    
    /**
     *  Determines the current state of the category.
     *
     *  @return <code>true</code> if the category is enabled; <code>false</code>
     *  otherwise.
     * @see Category#isEnabled()
     */
    public boolean isEnabled() {
        return getCategoryLocalization().isEnabled();
    }
    
    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>CategoryLocalization</code>.
     *
     * @return a <code>CategoryLocalization</code> for the current position in the
     * collection.
     **/
    public CategoryLocalization getCategoryLocalization() {
        return (CategoryLocalization) getDomainObject();
    }
    
    public ACSObject getACSObject() {
        return getCategoryLocalization();
    }

    /**
     * Search for the requested localization in the Collection
     *
     * @return result of the search. If true, the CollectionCursor is set to the position of the requested locale.
     */
    public boolean localizationExists(String locale) {
        
        // 
        if(!m_dataCollection.isEmpty() && locale != "") {
        
            // First check, if we are already at the right position. This will speed up repeated access for the same locale
            if(this.getPosition() > 0 && this.getCategoryLocalization().getLocale().equals(locale)) return true;
        
            // Nope, so we have to start a search
            this.rewind();
            while(this.next()) {
              if(this.getCategoryLocalization().getLocale().equals(locale)) return true;
            }

        }
        
        // Not found
        return false;
    }
    
    /**
     * Sorts the category collection by the category sort key.
     *
     * @see CategorizedCollection#sort(boolean)
     **/
    public final void sort(boolean ascending) {
        if ( ascending ) {
            addOrder("link.sortKey asc");
        } else {
            addOrder("link.sortKey desc");
        }
    }
}
