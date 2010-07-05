/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 *
 * @author jensp
 */
public class GenericOrganizationalUnitContactCollection extends DomainCollection {

    public static final String ORDER = "link.contact_order asc";
    public static final String CONTACT_TYPE = "link.contact_type";
    public static final String CONTACT_ORDER = "link.contact_order";


    public GenericOrganizationalUnitContactCollection(DataCollection dataCollection) {
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
