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
public class PublicPersonalProfiles extends Application {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfile";
    private final static PublicPersonalProfileConfig config =
                                               new PublicPersonalProfileConfig();

    static {
        config.load();
    }

    public PublicPersonalProfiles(final DataObject dobj) {
        super(dobj);
    }

    public PublicPersonalProfiles(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicPersonalProfiles(final BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static PublicPersonalProfileConfig getConfig() {
        return config;
    }

    @Override
    public String getServletPath() {
        return "/profiles/";
    }
}