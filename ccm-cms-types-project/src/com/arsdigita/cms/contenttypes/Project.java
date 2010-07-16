package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class Project extends GenericOrganizationalUnit{

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Project";
    
    public Project() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Project(BigDecimal id ) throws DataObjectNotFoundException{
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Project(OID oid) {
        super(oid);
    }

    public Project(DataObject obj) {
        super(obj);
    }

    public Project(String type) {
        super(type);
    }

}
