package com.arsdigita.london.cms.freeform.asset;

import java.math.BigDecimal;
import com.arsdigita.persistence.OID;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.cms.TextAsset;


/**
 * @author slater@arsdigita.com
 * 
 * For holding all binary assets referred to by the FreeformContentPages
 * Use {@link setContent} and {@link writeContent} to access content
 *
 **/

public class FreeformTextAsset extends TextAsset {

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.cms.freeform.asset.FreeformTextAsset";
    private static final String CONTENT = "content";

    /**
     * Default constructor. This creates a new text asset.
     **/
    public FreeformTextAsset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FreeformTextAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>FreeformTextAsset.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FreeformTextAsset(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public FreeformTextAsset(DataObject obj) {
        super(obj);
    }

    public FreeformTextAsset(String type) {
        super(type);
    }


    /* abstract implementation */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    
    // This is a temporary fix to a null value being inserted into
    // acs_object.display_name.
    @Override
    public void beforeSave() {
        String displayName = getDisplayName();
        set(com.arsdigita.kernel.ACSObject.DISPLAY_NAME, displayName);

        super.beforeSave();          
    }

}
