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

/**
 * <p>
 * Specialized variant of {@link GenericPerson} for members of scientific
 * organizations. Adds two fields to the fields of <code>GenericPerson</code>:
 * </p>
 * <dl>
 * <dt><code>associatedMember</code></dt>
 * <dd>Is the member an associated member?</dd>
 * <dt><code>formerMember</code></dt>
 * <dd>Is the member a former member?</dd>
 * </ul>
 *
 * @author Jens Pelzetter
 */
public class SciMember extends GenericPerson {
   
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciMember";
    private static final SciOrganizationConfig s_config =
                                               new SciOrganizationConfig();

    static {
        s_config.load();
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
