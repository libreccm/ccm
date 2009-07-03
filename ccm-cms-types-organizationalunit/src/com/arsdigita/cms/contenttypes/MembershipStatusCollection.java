/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import org.apache.log4j.Logger;
/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipStatusCollection extends DomainCollection {

    private final static Logger logger = Logger.getLogger(MembershipStatusCollection.class);

    public static MembershipStatusCollection getMembershipStatusCollection() {
        logger.debug("Getting MembershipStatusCollection...");
        SessionManager.getSession().retrieve(MembershipStatus.BASE_DATA_OBJECT_TYPE);
        DataCollection statusCollection = SessionManager.getSession().retrieve(MembershipStatus.BASE_DATA_OBJECT_TYPE);
        return new MembershipStatusCollection(statusCollection);
    }

    private MembershipStatusCollection(DataCollection dataCollection) {
        super(dataCollection);
         logger.debug("MembershipStatusCollection constructor...");
    }

    @Override
    public DomainObject getDomainObject() {
        return new MembershipStatus(m_dataCollection.getDataObject());
    }

    public MembershipStatus getMembershipStatus() {
        return (MembershipStatus) getDomainObject();
    }

    public String getMembershipStatusName() {
        return getMembershipStatus().getStatusName();
    }

    public BigDecimal getMembershipStatusId() {
        return getMembershipStatus().getID();
    }
}
