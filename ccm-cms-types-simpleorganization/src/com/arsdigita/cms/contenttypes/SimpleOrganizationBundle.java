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
public class SimpleOrganizationBundle extends GenericOrganizationalUnitBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SimpleOrganizationBundle";

    public SimpleOrganizationBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public SimpleOrganizationBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }
    
    public SimpleOrganizationBundle(final BigDecimal id) 
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public SimpleOrganizationBundle(final DataObject dobj) {
        super(dobj);
    }
    
    public SimpleOrganizationBundle(final String type) {
        super(type);
    }
    
    public SimpleOrganization getOrganization() {
        return (SimpleOrganization) getPrimaryInstance();
    }
    
    public SimpleOrganization getOrganization(final String language) {
        return (SimpleOrganization) getInstance(language);
    }
}
