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
package com.arsdigita.kernel.permissions;

import com.arsdigita.kernel.ACSObject;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;


/**
 * A class that represents a context heiracrchy of
 * <code>ACSObject</code>s to other <code>ACSObject</code>s where the
 * context is used for security inheritance.
 *
 * <p>
 * <font color="ff0000">
 * Note: This class will likely be removed later.  It is used internally
 * by the permissions service.
 * </font>
 *
 * @author Phong Nguyen
 * @author Oumi Mehrotra
 * @version 1.0
 *
 * @see com.arsdigita.kernel.ACSObject
 **/
final class ObjectContext extends DomainObject {

    public static final String versionId = "$Id: ObjectContext.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.permissions.ObjectContext";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "ObjectContext".
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    protected ObjectContext() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    protected ObjectContext(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Wrapper for {@link #ObjectContext(OID)} that uses the default
     * object type for object context.
     **/
    protected ObjectContext(Object id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }


    /**
     * Returns the <code>DataObject</code> of the context
     * <code>ACSObject</code> that this <code>ObjectContext</code>
     * refers to.
     *
     * @return The <code>DataObject</code> of the context
     * <code>ACSObject</code> that this <code>ObjectContext</code>
     * refers to.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.persistence.OID
     **/
    protected DataObject getContext() {
        DataObject dataObj = (DataObject) get("context");
        return dataObj;
    }

    /**
     * Returns the <code>OID</code> of the context
     * <code>ACSObject</code> that this <code>ObjectContext</code>
     * refers to.
     *
     * @return The <code>OID</code> of the context
     * <code>ACSObject</code> that this <code>ObjectContext</code>
     * refers to.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.persistence.OID
     **/
    protected OID getContextOID() {
        DataObject dataObj = (DataObject) get("context");
        if (dataObj != null) {
            return dataObj.getOID();
        }

        return null;
    }

    /**
     * Sets the context <code>ACSObject</code> that this
     * <code>ObjectContext</code> refers to.
     *
     * @param contextObject The <code>ACSObject</code> to use as the context.
     *
     * @see com.arsdigita.kernel.ACSObject
     **/
    protected void setContext(ACSObject contextObject) {
        if (contextObject == null) {
            // hack to fix a bug in persistence.
            // supposedly you have to get a role before you can set it
            // to null.
            getContext();
        }
        setAssociation("context", contextObject);
    }

    /**
     * Sets the context <code>ACSObject</code> with an
     * <code>OID</code> specified by <code>contextObjectOID</code>
     * that this <code>ObjectContext</code> refers to.
     *
     * @param contextOID The <code>OID</code> of the context <code>ACSObject</code>.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.persistence.OID
     **/
    protected void setContext(OID contextObjectOID) {
        if (contextObjectOID == null) {
            // hack to fix a bug in persistence.
            // supposedly you have to get a role before you can set it
            // to null.
            getContext();
            set("context", null);
        } else {
            set("context", getSession().retrieve(contextObjectOID));
        }
    }

    /**
     * Sets the <code>ACSObject</code> specified by
     * <code>acsObject</code> for this <code>ObjectContext</code> only
     * if this instance is new (has not been persisted).
     *
     * @param acsObject The <code>ACSObject</code> for this
     * <code>ObjectContext</code>.
     *
     * @see com.arsdigita.kernel.ACSObject
     **/
    protected void setObject(ACSObject acsObject) {
        if (isNew()) {
            set("objectId", acsObject.getID());

            set("object", acsObject);
        }
    }

    /**
     * Sets the <code>ACSObject</code> with an <code>OID</code>
     * specified by <code>objectOID</code> for this
     * <code>ObjectContext</code> only if this instance is new (has
     * not been persisted).
     *
     * @param objectOID The <code>OID</code> of the
     * <code>ACSObject</code> for this <code>ObjectContext</code>.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.persistence.OID
     **/
    protected void setObject(OID objectOID) {
        if (isNew()) {
            set("objectId", objectOID.get("id"));

            set("object", getSession().retrieve(objectOID));
        }
    }
}
