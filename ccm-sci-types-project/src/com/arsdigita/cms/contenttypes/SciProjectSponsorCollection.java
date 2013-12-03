package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectSponsorCollection extends DomainCollection {

    public final static String SPONSOR_ORDER = "sponsorOrder";
    public final static String LINK_SPONSOR_ORDER = "link." + SPONSOR_ORDER;
    public final static String SPONSOR_FUNDING_CODE = "sponsorFundingCode";
    public final static String LINK_SPONSOR_FUNDING_CODE = "link." + SPONSOR_FUNDING_CODE;
    
    
    public SciProjectSponsorCollection(final DataCollection dataCollection) {
        super(dataCollection);

        addOrder(LINK_SPONSOR_ORDER);
    }

    public GenericOrganizationalUnit getSponsor() {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(
                m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
    }

    public GenericOrganizationalUnit getSponsor(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(
                m_dataCollection.getDataObject());
        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }

    public BigDecimal getID() {
        return getSponsor().getID();
    }

    public String getTitle() {
        return getSponsor().getTitle();
    }
    
    public Integer getSponsorOrder() {
        return (Integer) get(LINK_SPONSOR_ORDER);
    }
    
    public void setSponsorOrder(final Integer order) {
        final DataObject link = (DataObject) get("link");
        
        link.set(SPONSOR_ORDER, order);
    }
    
    public String getFundingCode() {
        return (String) get(LINK_SPONSOR_FUNDING_CODE);
    }

    public void setFundingCode(final String fundingCode) {
       final DataObject link = (DataObject) get("link");
       
       link.set(SPONSOR_FUNDING_CODE, fundingCode);
    }
    
    public void swapWithNext(final GenericOrganizationalUnit sponsor) {
        int currentPos = 0;
        int currentIndex = 0;
        int nextIndex = 0;
        
        rewind();
        while(next()) {
            currentPos = getPosition();
            currentIndex = getSponsorOrder();
            if (getSponsor().equals(sponsor)) {
                break;
            }
        }
        
        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    "The provided organisation is not an sponsor of this project.");
        }
        
        if (this.next()) {
            nextIndex = this.getSponsorOrder();
        } else {
            throw new IllegalArgumentException("The provided sponsor ist the last one in the "
                    + "collection, so there is no next object to switch with.");
        }
        
        this.rewind();
        
        while(getPosition() != currentPos) {
            this.next();
        }
        
        setSponsorOrder(nextIndex);
        next();
        setSponsorOrder(currentIndex);
        this.rewind();
        
        normalizeOrder();
    }
    
    public void swapWithPrevious(final GenericOrganizationalUnit sponsor) {
        int previousPos = 0;
        int previousIndex = 0;
        int currentPos = 0;
        int currentIndex = 0;
        
        rewind();
        while(next()) {
            currentPos = getPosition();
            currentIndex = getSponsorOrder();
            
            if (getSponsor().equals(sponsor)) {
                break;
            }
            
            previousPos = currentPos;
            previousIndex = currentIndex;
        }
        
        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    "The provided organisation is not an sponsor of this project.");
        }
        
        if (previousPos == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided sponsor is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }
        
        rewind();
        while(getPosition() != previousPos) {
            next();
        }
        
        setSponsorOrder(currentIndex);
        next();
        setSponsorOrder(previousIndex);
        rewind();
        
        normalizeOrder();
    }
    
    private void normalizeOrder() {
        rewind();
        
        int i = 1;
        while(next()) {
            setSponsorOrder(i);
            i++;
        }
        
        this.rewind();
    }
}
