package com.arsdigita.cms.contentassets;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationPublicationLink extends RelatedLink {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contentassets.SciOrganizationPublicationLink";

    public SciOrganizationPublicationLink() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciOrganizationPublicationLink(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciOrganizationPublicationLink(OID oid) {
        super(oid);
    }
    
    public SciOrganizationPublicationLink(DataObject dobj) {
        super(dobj);
    }

    public SciOrganizationPublicationLink(String type) {
        super(type);
    }
}
