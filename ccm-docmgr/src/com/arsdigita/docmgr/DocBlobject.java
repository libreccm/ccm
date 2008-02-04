package com.arsdigita.docmgr;


import java.math.BigDecimal;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;







public class DocBlobject extends DomainObject {


    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.docs.DocBlobject";


    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public DocBlobject() {
    super(BASE_DATA_OBJECT_TYPE);
    }

    protected void initialize() {
        super.initialize();
        try {
            if(isNew()) {
              set("id",Sequences.getNextValue());  
            }
          } catch (java.sql.SQLException e) { //s_log.error here 
          }
    }

    /**
     * Creates a new Doc Blob by retrieving it from the underlying data
     * object.
     *
     * @param dataObject the dataObject corresponding to this file
     */
    public DocBlobject(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Creates a new File by retrieving it based on ID.
     *
     * @param id - the ID of this file in the database
     */
    public DocBlobject(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a new DocBlobject by retrieving it based on OID.
     *
     * @param oid - the OID of this file
     */
    public DocBlobject(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
   
    public byte[] getContent() {
        return (byte[])get("content");
    }

    public void setContent(byte[] content) {

        set("content",content);

    }

}
