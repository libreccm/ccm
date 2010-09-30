package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartment extends GenericOrganizationalUnit {

    public static final String DESCRIPTION = "description";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciDepartment";

    public SciDepartment() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciDepartment(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciDepartment(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciDepartment(DataObject obj) {
        super(obj);
    }

    public SciDepartment(String type) {
        super(type);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }
}
