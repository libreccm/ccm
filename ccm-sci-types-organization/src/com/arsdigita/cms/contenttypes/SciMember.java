/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * A concrete class extending {@link GenericPerson}. Does not add any new
 * fields, it just makes the internal type <code>GenericPerson</code> usable
 * without any other modules.
 *
 * @author Jens Pelzetter
 */
public class SciMember extends GenericPerson {

    private static final Logger logger = Logger.getLogger(SciMember.class);
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciMember";
    private static final SciOrganizationConfig s_config =
                                               new SciOrganizationConfig();

    static {
        logger.debug("Static initalizer starting...");
        s_config.load();
        logger.debug("Static initalizer starting...");
    }

    public SciMember() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciMember(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciMember(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciMember(DataObject dobj) {
        super(dobj);
    }

    public SciMember(String type) {
        super(type);
    }

    public static SciOrganizationConfig getConfig() {
        return s_config;
    }    
}
