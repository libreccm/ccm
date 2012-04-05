package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationGenericOrganizationalsUnitCollection
        extends DomainCollection {

    public PublicationGenericOrganizationalsUnitCollection(
            final DataCollection dataCollection) {
        super(dataCollection);

        addOrder("title");
    }

    public GenericOrganizationalUnit getOrganizationalUnit() {
        //return (GenericOrganizationalUnit) DomainObjectFactory.newInstance(
        //        m_dataCollection.getDataObject());
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
    }
    
    public GenericOrganizationalUnit getOrganizationalUnit(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }

    public BigDecimal getID() {
         return getOrganizationalUnit().getID();
    }
    
    public String getTitle() {
        //return (String) m_dataCollection.getDataObject().get(ContentPage.TITLE);
        return getOrganizationalUnit().getTitle();
    }
    
    public String getAddendum() {
        return (String) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.ADDENDUM);
    }

    /*public GenericOrganizationalUnitContactCollection getContacts() {
        return new GenericOrganizationalUnitContactCollection(
                (DataCollection) m_dataCollection.get(
                GenericOrganizationalUnit.CONTACTS));
    }

    public GenericOrganizationalUnitPersonCollection getPersons() {
        return new GenericOrganizationalUnitPersonCollection(
                (DataCollection) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.PERSONS));
    }

    public GenericOrganizationalUnitSuperiorCollection getSuperiorOrgaUnits() {
        return new GenericOrganizationalUnitSuperiorCollection(
                (DataCollection) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.SUPERIOR_ORGAUNITS));
    }

    public GenericOrganizationalUnitSubordinateCollection getSubordinateOrgaUnits() {
        return new GenericOrganizationalUnitSubordinateCollection(
                (DataCollection) m_dataCollection.getDataObject().get(
                GenericOrganizationalUnit.SUBORDINATE_ORGAUNITS));
    }*/
}
