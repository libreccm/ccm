/*
 * Copyright (c) 2011 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
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
            "com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfiles";
    private final static PublicPersonalProfileConfig config = new PublicPersonalProfileConfig(); // PublicPersonalProfileConfig.getConfig();

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

    @Override
    public String getServletPath() {
        return "/profiles/";
    }

    public static PublicPersonalProfileConfig getConfig() {
        return config;
    }
}
