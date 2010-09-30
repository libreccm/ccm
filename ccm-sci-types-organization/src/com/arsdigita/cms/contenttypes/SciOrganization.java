package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganization extends GenericOrganizationalUnit {

    public static final String DESCRIPTION = "description";
    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.contenttypes.SciOrganization";

    public SciOrganization() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciOrganization(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciOrganization(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciOrganization(DataObject obj) {
        super(obj);
    }

    public SciOrganization(String type) {
        super(type);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }
}
