package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class Department extends GenericOrganizationalUnit {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.Department";

    public Department() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Department(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Department(OID oid) {
        super(oid);
    }

    public Department(DataObject obj) {
        super(obj);
    }

    public Department(String type) {
        super(type);
    }
}
