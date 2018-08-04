package com.arsdigita.london.cms.freeform.asset;

import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.cms.BinaryAsset;
import java.io.File;

/**
 * @author slater@arsdigita.com
 * 
 * For holding all binary assets referred to by the FreeformContentItems
 * Use {@link readByes} and {@link writeBytes} to get, set the binary data
 *
 **/
public class FreeformBinaryAsset extends BinaryAsset {

    private static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.cms.freeform.asset.FreeformBinaryAsset";
    private static final String CONTENT = "content";
    
    /**
     * Default constructor. This creates a new text asset.
     **/
    public FreeformBinaryAsset() {
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
    public FreeformBinaryAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>FreeformBinaryAsset.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FreeformBinaryAsset(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public FreeformBinaryAsset(DataObject obj) {
        super(obj);
    }

    public FreeformBinaryAsset(String type) {
        super(type);
    }

    /**
     * implementation of abstract method
     * 
     * @return 
     **/
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Basic implementation of abstract method. Use {@link
     * readBytes()} to read binary data.
     * 
     * @return 
     **/
    //protected byte[] getContent() {
    @Override
    public byte[] getContent() {
        return (byte[]) get(CONTENT);
    }

    /**
     * Basic implementation of abstract method. Use {@link
     * writeBytes()} to set binary data.
     **/
    @Override
    protected void setContent(byte[] content) {
        set(CONTENT, content);
    }

    /** 
     * Not implemented;  required by the interface
     * 
     * @param f
     **/
    @Override
    public void writeToFile(File f) {
        // do nothing
    }

}
