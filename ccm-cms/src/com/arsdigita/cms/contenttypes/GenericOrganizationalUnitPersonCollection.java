package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonCollection extends DomainCollection {

    public static final String ORDER = "link.person_order asc";
    public static final String PERSON_ROLE = "link.role_name";
    public static final String PERSON_ORDER = "link.person_order";

    public GenericOrganizationalUnitPersonCollection(
            DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Gets the name of the role of this orgaunit-person link
     */
    public String getRoleName() {
        return (String) m_dataCollection.get(PERSON_ROLE);
    }

    public String getPersonOrder() {
        String retVal = ((BigDecimal) m_dataCollection.get(PERSON_ORDER)).
                toString();

        if (retVal == null || retVal.isEmpty()) {
            retVal = String.valueOf(this.getPosition());
        }

        return retVal;
    }

    public GenericPerson getPerson() {
        return new GenericPerson(m_dataCollection.getDataObject());
    }
}
