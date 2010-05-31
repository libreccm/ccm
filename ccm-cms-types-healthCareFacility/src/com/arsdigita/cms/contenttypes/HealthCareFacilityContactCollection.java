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
public class HealthCareFacilityContactCollection extends DomainCollection {
    
    public static final String ORDER = "link.contact_order asc";
    public static final String CONTACT_TYPE = "link.contact_type";
    public static final String CONTACT_ORDER = "link.contact_order";
    
    /**
     * Creates a new instance of HealthCareFacilityContactCollection
     */
    public HealthCareFacilityContactCollection(DataCollection dataCollection) {
        super(dataCollection);
        
        m_dataCollection.addOrder(ORDER);
    }
    
        // Get the contact type of the link
    public String getContactType() {
        return (String) m_dataCollection.get(CONTACT_TYPE);
    }
    
    // Set the contact type of the link
//    public void setContactType(String contactType) {
//        getContact().set(CONTACT_TYPE, contactType);
//    }
    
    // Get the contact order of the link
    public String getContactOrder() {
        String retVal = ((BigDecimal) m_dataCollection.get(CONTACT_ORDER)).toString();
        
        if(retVal == null || retVal.isEmpty()) {
            retVal = String.valueOf(this.getPosition());
        }
        
        return retVal;
    }
    
    // Set the contact order of the link
//    public void setContactOrder(long contactOrder) {
//        getContact().set(CONTACT_ORDER, String.valueOf(contactOrder));
//    }

    public com.arsdigita.cms.contenttypes.GenericContact getContact() {
        return new com.arsdigita.cms.contenttypes.GenericContact(m_dataCollection.getDataObject());
    }
    
}
