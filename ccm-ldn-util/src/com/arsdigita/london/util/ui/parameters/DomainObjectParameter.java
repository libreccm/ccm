/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.util.ui.parameters;


import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.util.Assert;

import com.arsdigita.bebop.parameters.ParameterModel;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

/**
 * A parameter model whcih unmarshals / marshals directly 
 * to/from domain objects. On the wire, the string 
 * representation of the OID, or the 'id' is used.
 */
public class DomainObjectParameter extends ParameterModel {

    private String m_objectType;

    public DomainObjectParameter(String name) {
        super(name);
    }

    public DomainObjectParameter(String name,
                                 String objectType) {
        super(name);
        m_objectType = objectType;

        ObjectType type = MetadataRoot.getMetadataRoot()
            .getObjectType(objectType);
        Assert.truth(type.isSubtypeOf(ACSObject.BASE_DATA_OBJECT_TYPE),
                     objectType + " is a subtype of " + 
                     ACSObject.BASE_DATA_OBJECT_TYPE);
    }

    public Object transformValue(HttpServletRequest request) {
        return transformSingleValue(request);
    }

    /**
     * Marshals a DomainObject to a String
     */
    public String marshal(Object value) {
        if (value == null) {
            return null;
        }

        if (m_objectType == null) {
            DomainObject dobj = (DomainObject)value;
            return dobj.getOID().toString();
        } else {
            ACSObject dobj = (ACSObject)value;
            Assert.truth(
                m_objectType.equals(dobj.getOID()
                                    .getObjectType()
                                    .getQualifiedName()),
                "Object type of " + dobj.getOID() + " equals " + m_objectType);
            return dobj.getID().toString();
        }
    }

    /**
     * Unmarshals a String representing an OID into
     * a DomainObject
     */
    public Object unmarshal(String encoded) {
        if (m_objectType == null) {
            OID oid = OID.valueOf(encoded);
            return DomainObjectFactory.newInstance(oid);
        } else {
            BigDecimal id = new BigDecimal(encoded);
            return DomainObjectFactory.newInstance(
                new OID(m_objectType,
                        id));
        }
    }
    
    /**
     * Returns DomainObject.class
     */
    public Class getValueClass() {
        return DomainObject.class;
    }


}
