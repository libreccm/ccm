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
public class SciInstituteBundle extends GenericOrganizationalUnitBundle {
    
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciInstituteBundle";
    
    public SciInstituteBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);
        
        Assert.exists(primary, ContentItem.class);
        
        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);
        
        super.setName(primary.getName());        
    }
    
    public SciInstituteBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
    
    public SciInstituteBundle(final BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }               
    
    public SciInstituteBundle(final DataObject dobj) {
        super(dobj);
    }
    
    public SciInstituteBundle(final String type) {
        super(type);
    }
}
