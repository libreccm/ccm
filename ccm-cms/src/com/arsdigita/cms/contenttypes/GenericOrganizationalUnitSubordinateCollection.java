package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitSubordinateCollection extends DomainCollection {

    public static final String ASSOCTYPE = "assocType";
    public static final String SUBORDINATE_ORGAUNIT_ORDER =
                               "subordinateOrgaUnitOrder";
    public static final String LINK_ASSOCTYPE = "link." + ASSOCTYPE;
    public static final String LINK_SUBORDINATE_ORGAUNIT_ORDER =
                               "link." + SUBORDINATE_ORGAUNIT_ORDER;

    public GenericOrganizationalUnitSubordinateCollection(
            final DataCollection dataCollection) {
        super(dataCollection);
        m_dataCollection.addOrder(LINK_SUBORDINATE_ORGAUNIT_ORDER + " asc");
    }

    public String getAssocType() {
        return (String) m_dataCollection.get(LINK_ASSOCTYPE);
    }

    public void setAssocType(final String assocType) {
        final DataObject link = (DataObject) get("link");
        link.set(ASSOCTYPE, assocType);
    }

    public Integer getSubordinateOrder() {
        return (Integer) m_dataCollection.get(LINK_SUBORDINATE_ORGAUNIT_ORDER);
    }

    public void setSubordinateOrder(final Integer order) {
        final DataObject link = (DataObject) get("link");
        link.set(SUBORDINATE_ORGAUNIT_ORDER, order);
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
            currentIndex = getSubordinateOrder();
            if (orgaunit.equals(getGenericOrganizationalUnit())) {
                break;
            }
        }

        if (next()) {
            nextIndex = getSubordinateOrder();
        } else {
            throw new IllegalArgumentException("The provided organizational "
                                               + "unit is the last one in the collection, so there is no "
                                               + "next object to swap with.");
        }

        rewind();

        while (this.getPosition() != currentPos) {
            next();
        }

        setSubordinateOrder(nextIndex);
        next();
        setSubordinateOrder(currentIndex);
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
            currentIndex = getSubordinateOrder();

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

        setSubordinateOrder(currentIndex);
        next();
        setSubordinateOrder(previousIndex);
        rewind();

        normalizeOrder();
    }

    private void normalizeOrder() {
        this.rewind();

        int i = 1;
        while (this.next()) {
            setSubordinateOrder(i);
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
    
     public GenericOrganizationalUnit getGenericOrganizationalUnit(
             final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());

        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }

    public BigDecimal getId() {
        return (BigDecimal) m_dataCollection.getDataObject().get(ACSObject.ID);
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
