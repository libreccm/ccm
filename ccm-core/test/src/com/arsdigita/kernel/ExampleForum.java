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
package com.arsdigita.kernel;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;


/**
 * Example class: "forum" domain object
 *
 * @author Oumi Mehrotra 
 * @version 1.0
 **/
public class ExampleForum extends ACSObject {


    public static final String BASE_DATA_OBJECT_TYPE =
        "examples.Forum";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public ExampleForum(DataObject forumData) {
        super(forumData);
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "Forum".
     *
     * @see ACSObject#ACSObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ExampleForum() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>typeName</i>.
     *
     * @param typeName the name of the <code>ObjectType</code> of the
     * contained <code>DataObject</code>
     *
     * @see ACSObject#ACSObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ExampleForum(String typeName) {
        super(typeName);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by <i>type</i>.
     *
     * @param type the <code>ObjectType</code> of the contained
     * <code>DataObject</code>
     *
     * @see ACSObject#ACSObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public ExampleForum(ObjectType type) {
        super(type);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid the <code>OID</code> for the retrieved
     * <code>DataObject</code>
     *
     * @see ACSObject#ACSObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public ExampleForum(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Returns a display name for this Forum.
     *
     * @see ACSObject#getDisplayName()
     */
    public String getDisplayName() {
        return (String) get("name");
    }

    public void setDisplayName(String name) {
        set("name", name);
    }

    public PackageInstance getPackageInstance() {
        return new PackageInstance((DataObject) get("packageInstance"));
    }

    public void setPackageInstance(PackageInstance packageInstance) {
        setAssociation("packageInstance", packageInstance);
    }

    // The default getContainer and isContainerModified implementations
    // should work, but to make debugging easier, I am leaving these
    // domain-specific implementations here, but commented out.
    /*
      protected ACSObject getContainer() {
      return getPackageInstance();
      }

      protected boolean isContainerModified() {
      return isPropertyModified("packageInstance");
      }
    */

    public ExampleMessageCollection getMessages() {
        return new ExampleMessageCollection((DataAssociation)get("messages"));
    }

}
