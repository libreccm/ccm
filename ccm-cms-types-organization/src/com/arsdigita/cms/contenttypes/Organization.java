package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class Organization extends GenericOrganizationalUnit {

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Organization";
    
    public Organization() {
        super(BASE_DATA_OBJECT_TYPE);
    }
    
    public Organization(BigDecimal id)
        throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
        
   public Organization(OID oid) {
       super(oid);
   }

   public Organization(DataObject obj) {
       super(obj);
   }

   public Organization(String type) {
       super(type);
   }

   /*@Override
    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }*/
    
}
