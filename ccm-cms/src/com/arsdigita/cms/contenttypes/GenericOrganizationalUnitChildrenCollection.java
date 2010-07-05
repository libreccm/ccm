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
public class GenericOrganizationalUnitChildrenCollection extends DomainCollection {

    public static final String ORDER = "link.contact_order asc";
    public static final String CHILDREN_ORDER = "link.children_order";

    public GenericOrganizationalUnitChildrenCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(ORDER);
    }

    public String getChildrenOrder() {
        String retVal = ((BigDecimal) m_dataCollection.get(CHILDREN_ORDER)).toString();

        if (retVal == null || retVal.isEmpty()) {
            retVal = String.valueOf(this.getPosition());
        }

        return retVal;
    }

    public GenericOrganizationalUnit getOrgaUnitChild() {
        return new GenericOrganizationalUnit(m_dataCollection.getDataObject());
    }
}
