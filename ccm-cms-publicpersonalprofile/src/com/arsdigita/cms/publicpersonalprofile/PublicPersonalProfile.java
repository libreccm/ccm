package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfile extends Application {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfile";

    public PublicPersonalProfile(final DataObject dobj) {
        super(dobj);
    }

    public PublicPersonalProfile(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicPersonalProfile(final BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    @Override
    public String getServletPath() {
        return "/profiles/";
    }
}
