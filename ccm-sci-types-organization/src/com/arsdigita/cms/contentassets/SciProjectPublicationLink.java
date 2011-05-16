package com.arsdigita.cms.contentassets;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectPublicationLink extends RelatedLink {
    
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contentassets.SciProjectPublicationLink";
    
    public SciProjectPublicationLink() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    public SciProjectPublicationLink(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public SciProjectPublicationLink(OID oid) {
        super(oid);
    }
    
    public SciProjectPublicationLink(DataObject dobj) {
        super(dobj);
    }
    
    public SciProjectPublicationLink(String type) {
        super(type);
    }
    
    
}
