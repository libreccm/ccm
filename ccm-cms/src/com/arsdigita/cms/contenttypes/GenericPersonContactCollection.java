/*
 * HealthCareFacilityContactCollection.java
 *
 * Created on 26. Juli 2009, 15:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 *
 * @author quasi
 */
public class GenericPersonContactCollection extends DomainCollection {
    
    public static final String ORDER = "link.contact_order asc";
    public static final String CONTACT_TYPE = "link.contact_type";
    public static final String CONTACT_ORDER = "link.contact_order";
    
    /**
     * Creates a new instance of GenericPersonContactCollection
     */
    public GenericPersonContactCollection(DataCollection dataCollection) {
        super(dataCollection);
        
        m_dataCollection.addOrder(ORDER);
    }
    
    // Get the contact type of the link
    public String getContactType() {
        return (String) m_dataCollection.get(CONTACT_TYPE);
    }
    
    // Get the contact order of the link
    public String getContactOrder() {
        String retVal = ((BigDecimal) m_dataCollection.get(CONTACT_ORDER)).toString();
        
        if(retVal == null || retVal.isEmpty()) {
            retVal = String.valueOf(this.getPosition());
        }
        
        return retVal;
    }
    
    public GenericContact getContact() {
        return new GenericContact(m_dataCollection.getDataObject());
    }
    
}
