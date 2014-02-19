package com.arsdigita.navigation.cms;

import com.arsdigita.categorization.Category;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.navigation.NavigationModel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;

/**
 * An extension to the {@link CMSDataCollectionDefinition}. With this definition it is possible to filter an object
 * list using a second category system/terms domain. This class is designed to be used together with the 
 * {@link CategorisedDataCollectionRenderer} which displays the objects in a list with several sections. 
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CategorisedDataCollectionDefinition extends CMSDataCollectionDefinition{ 
    
    private String termDomain;
    
    public String getTermDomain() {
        return termDomain;
    }
    
    public void setTermDomain(final String termDomain) {
        this.termDomain = termDomain;
    }
                
    @Override
    protected void applyFilters(final DataCollection objects, final NavigationModel model) {
        super.applyFilters(objects, model);
        
        final Domain domain = Domain.retrieve(termDomain);
        final Category rootCat = domain.getModel();
        
        //final FilterFactory filterFactory = objects.getFilterFactory();
        final Filter filter = objects.addInSubqueryFilter(getCategorizedObjectPath("id"), "com.arsdigita.categorization.objectIDsInSubtree");
        filter.set("categoryID", rootCat.getID());
        
        objects.addOrder("parent.categories.link.sortKey");
        //objects.addOrder("title desc");
    }
    
}
