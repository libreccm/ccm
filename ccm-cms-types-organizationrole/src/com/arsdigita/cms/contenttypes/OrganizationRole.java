package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;

import java.math.BigDecimal;
import org.apache.log4j.Logger;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRole extends ContentPage {

    public static final String ROLENAME = "rolename";

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationRole";

    public static final Logger logger = Logger.getLogger(OrganizationRole.class);

    public OrganizationRole() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public OrganizationRole(BigDecimal id) throws DataObjectNotFoundException {
        super(id);
    }

    public OrganizationRole(OID oid) {
        super(oid);
    }

    public OrganizationRole(DataObject obj) {
        super(obj);
    }
    
    public OrganizationRole(String type) {
        super(type);
    }
    
    public String getRoleName() {
        return (String) get(ROLENAME);
    }
    
    public void setRoleName(String rolename) {
        logger.error(String.format("Setting rolename to %s", rolename));
        set(ROLENAME, rolename);
    }

}
