package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipStatus extends ACSObject {

    public static final Logger logger = Logger.getLogger(MembershipStatus.class);

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.MembershipStatus";
                                                       
    public static final String MEMBERSHIP_STATUS_NAME = "membershipStatusName";

    public MembershipStatus() {
        super(BASE_DATA_OBJECT_TYPE);
        logger.debug("Paramless constructor...");
    }

    public MembershipStatus(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
        logger.debug("id constructor...");
    }

    public MembershipStatus(OID id) throws DataObjectNotFoundException {
        super(id);
        logger.debug("oid constructor...");
    }

    public MembershipStatus(DataObject obj) {
        super(obj);
        logger.debug("obj constructor...");
    }

    public MembershipStatus(String type) {
        super(type);
        logger.debug("type constructor...");
    }

    public String getStatusName() {
        return (String) get(MEMBERSHIP_STATUS_NAME);
    }

    public void setStatusName(String typeName) {
        set(MEMBERSHIP_STATUS_NAME, typeName);
    }
}