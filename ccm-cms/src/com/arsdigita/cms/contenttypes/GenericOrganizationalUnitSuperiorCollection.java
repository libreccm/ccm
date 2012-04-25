package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitSuperiorCollection extends DomainCollection {

    public static final String ASSOCTYPE = "assocType";
    public static final String SUPERIOR_ORGAUNIT_ORDER =
                               "superiorOrgaUnitOrder";
    public static final String LINK_ASSOCTYPE = "link." + ASSOCTYPE;
    public static final String LINK_SUPERIOR_ORGAUNIT_ORDER =
                               "link." + SUPERIOR_ORGAUNIT_ORDER;

    public GenericOrganizationalUnitSuperiorCollection(
            final DataCollection dataCollection) {
        super(dataCollection);
        m_dataCollection.addOrder(LINK_SUPERIOR_ORGAUNIT_ORDER + " asc");
    }

    public String getAssocType() {
        return (String) m_dataCollection.get(LINK_ASSOCTYPE);
    }

    public void setAssocType(final String assocType) {
        final DataObject link = (DataObject) get("link");
        link.set(ASSOCTYPE, assocType);
    }

    public Integer getSuperiorOrder() {
        return (Integer) m_dataCollection.get(LINK_SUPERIOR_ORGAUNIT_ORDER);
    }

    public void setSuperiorOrder(final Integer order) {
        final DataObject link = (DataObject) get("link");
        link.set(SUPERIOR_ORGAUNIT_ORDER, order);
    }

    public void swapWithNext(final GenericOrganizationalUnit orgaunit) {
        if (orgaunit == null) {
            throw new IllegalArgumentException(
                    "Parameter orgaunit is null. Can't swap position with null");
        }

        int currentPos = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        rewind();
        while (next()) {
            currentPos = getPosition();
            currentIndex = getSuperiorOrder();
            if (orgaunit.equals(getGenericOrganizationalUnit())) {
                break;
            }
        }

        if (next()) {
            nextIndex = getSuperiorOrder();
        } else {
            throw new IllegalArgumentException("The provided organizational "
                                               + "unit is the last one in the collection, so there is no "
                                               + "next object to swap with.");
        }

        rewind();

        while (this.getPosition() != currentPos) {
            next();
        }

        setSuperiorOrder(nextIndex);
        next();
        setSuperiorOrder(currentIndex);
        rewind();

        normalizeOrder();
    }

    public void swapWithPrevious(final GenericOrganizationalUnit orgaunit) {
        if (orgaunit == null) {
            throw new IllegalArgumentException(
                    "Parameter orgaunit is null. Can't swap position with null");
        }

        int previousPos = 0;
        int previousIndex = 0;
        int currentPos = 0;
        int currentIndex = 0;

        rewind();
        while (next()) {
            currentPos = getPosition();
            currentIndex = getSuperiorOrder();

            if (orgaunit.equals(getGenericOrganizationalUnit())) {
                break;
            }

            previousPos = currentPos;
            previousIndex = currentIndex;
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException("The provided contact is the "
                                               + "first one in this collection, so there is no previous "
                                               + "one to switch with.");
        }

        rewind();
        while (getPosition() != previousPos) {
            this.next();
        }

        setSuperiorOrder(currentIndex);
        next();
        setSuperiorOrder(previousIndex);
        rewind();

        normalizeOrder();
    }

    private void normalizeOrder() {
        this.rewind();

        int i = 1;
        while (this.next()) {
            setSuperiorOrder(i);
            i++;
        }
        this.rewind();
    }

    public GenericOrganizationalUnitBundle getGenericOrganizationalUnitBundle() {
        return (GenericOrganizationalUnitBundle) DomainObjectFactory.newInstance(
                m_dataCollection.getDataObject());
    }

    public GenericOrganizationalUnit getGenericOrganizationalUnit() {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
    }
    
    public GenericOrganizationalUnit getGenericOrganizationalUnit(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }

    public OID getOID() {
        return m_dataCollection.getDataObject().getOID();
    }

    public String getName() {
        return (String) m_dataCollection.get(ContentItem.NAME);
    }

    public String getTitle() {
        return getGenericOrganizationalUnit().getTitle();
    }

    public String getAddendum() {
        return getGenericOrganizationalUnit().getAddendum();
    }
}
