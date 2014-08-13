package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class OrganizationBundle extends GenericOrganizationalUnitBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.OrganizationBundle";

    public OrganizationBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public OrganizationBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }
    
    public OrganizationBundle(final BigDecimal id) 
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public OrganizationBundle(final DataObject dobj) {
        super(dobj);
    }
    
    public OrganizationBundle(final String type) {
        super(type);
    }
    
    public Organization getOrganization() {
        return (Organization) getPrimaryInstance();
    }
    
    public Organization getOrganization(final String language) {
        return (Organization) getInstance(language);
    }
}
