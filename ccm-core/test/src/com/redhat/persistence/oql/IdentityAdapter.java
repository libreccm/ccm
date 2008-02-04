/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;

/**
 * IdentityAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public class IdentityAdapter extends Adapter {

    public final static String versionId = "$Id: IdentityAdapter.java 740 2005-09-02 10:13:54Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public Object getObject(ObjectType basetype, PropertyMap props) {
        return props;
    }

    public PropertyMap getProperties(Object obj) {
        return (PropertyMap) obj;
    }

    public ObjectType getObjectType(Object obj) {
        return ((PropertyMap) obj).getObjectType();
    }

}
