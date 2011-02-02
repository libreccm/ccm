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
public class SciPublications extends Application {

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.scipublications.SciPublications";

    public SciPublications(DataObject dobj)  {
        super(dobj);
    }

    public SciPublications(OID oid) throws DataObjectNotFoundException{
        super(oid);
    }

    public SciPublications(BigDecimal key) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    @Override
    public String getServletPath() {
        return "/scipublications/";
    }

}
