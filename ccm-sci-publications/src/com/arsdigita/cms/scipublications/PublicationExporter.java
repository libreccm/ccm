package com.arsdigita.cms.scipublications;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationExporter extends Application {

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.scipublications.PublicationExporter";

    public PublicationExporter(DataObject dobj)  {
        super(dobj);
    }

    public PublicationExporter(OID oid) throws DataObjectNotFoundException{
        super(oid);
    }

    public PublicationExporter(BigDecimal key) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    @Override
    public String getServletPath() {
        return "/scipublicationsexporter/";
    }

}
