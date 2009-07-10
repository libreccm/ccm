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
 *
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.DataSet;
import com.redhat.persistence.common.Path;
import org.apache.log4j.Logger;

/**
 * DataCollectionImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 **/
class DataCollectionImpl extends DataQueryImpl implements DataCollection {

    public final static String versionId = "$Id: DataCollectionImpl.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    DataCollectionImpl(Session ssn, DataSet ds) {
        super(ssn, ds);
    }

    public ObjectType getObjectType() {
        return C.fromType(getSession().getMetadataRoot(), getTypeInternal());
    }

    public DataObject getDataObject() {
        Path p = Path.get(null);
        p = resolvePath(p);
        return (DataObject) m_cursor.get(p);
    }

    /**
     * @deprecated
     **/
    public void setParameter(String p, Object o) {
        super.setParameter(p, o);
    }

    /**
     * @deprecated
     **/
    public Object getParameter(String p) {
        return super.getParameter(p);
    }

    public boolean contains(OID oid) {
        throw new Error("not implemented");
    }

    public boolean contains(DataObject data) {
        throw new Error("not implemented");
    }
}
