package com.arsdigita.cms.contentassets;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentPublicationLink extends RelatedLink {
    
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contentassets.SciDepartmentPublicationLink";
    
    public SciDepartmentPublicationLink() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    public SciDepartmentPublicationLink(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public SciDepartmentPublicationLink(OID oid) {
        super(oid);
    }
    
    public SciDepartmentPublicationLink(DataObject dobj) {
        super(dobj);
    }
    
    public SciDepartmentPublicationLink(String type) {
        super(type);
    }
    
}
