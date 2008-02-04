/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class UserHomeFolderMap extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.UserHomeFolderMap";

    private static final Logger s_log =
        Logger.getLogger(UserHomeFolderMap.class);

    public static final String USER_ID = "userID";
    public static final String SECTION_ID = "sectionID";
    public static final String HOME_FOLDER = "homeFolder";
    public static final String HOME_SECTION = "homeSection";
    public static final String HOME_FOLDER_USER = "homeFolderUser";
    
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public UserHomeFolderMap(DataObject dataObj) {
        super(dataObj);
    }
    public UserHomeFolderMap() {
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
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public UserHomeFolderMap(String typeName) {
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
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public UserHomeFolderMap(ObjectType type) {
        super(type);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism using the
     * specified OID.
     *
     * @param oid the OID for the retrieved
     * <code>DataObject</code>
     *
     * @see com.arsdigita.domain.ObservableDomainObject#ObservableDomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public UserHomeFolderMap(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public BigDecimal getUserID() {
        return (BigDecimal) get(USER_ID);
    }

    public void setUserID(BigDecimal userID) {
        set(USER_ID,userID);
    }

    public BigDecimal getSectionID() {
        return (BigDecimal) get(SECTION_ID);
    }

    public void setSectionID(BigDecimal sectionID) {
        set(SECTION_ID,sectionID);
    }

    public Folder getHomeFolder() {
	DataObject object = (DataObject)get(HOME_FOLDER);
	if ( object == null ) {
	    return null;
	}
	return new Folder(object);
    }

    public void setHomeFolder(Folder folder) {
	setAssociation(HOME_FOLDER,folder);
    }

    public ContentSection getHomeSection() {
	DataObject object = (DataObject)get(HOME_SECTION);
	if ( object == null ) {
	    return null;
	}
	return new ContentSection(object);
    }

    public void setHomeSection(ContentSection section) {
	setAssociation(HOME_SECTION,section);
    }

    public User getHomeFolderUser() {
	DataObject object = (DataObject)get(HOME_FOLDER_USER);
	if ( object == null ) {
	    return null;
	}
	return new User(object);
    }

    public void setHomeFolderUser(User user) {
	setAssociation(HOME_FOLDER_USER,user);
    }

    public static UserHomeFolderMap findUserHomeFolderMap(User user, ContentSection section) {
	DataCollection maps = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
	maps.addEqualsFilter(USER_ID,user.getID().toString());
	maps.addEqualsFilter(SECTION_ID,section.getID().toString());

	UserHomeFolderMap map = null;
	if ( maps.next() ) {
	    map = new UserHomeFolderMap(maps.getDataObject());
	}
	maps.close();

	return map;
    }

    public static UserHomeFolderMap findOrCreateUserHomeFolderMap(User user,ContentSection section) {
	UserHomeFolderMap map = findUserHomeFolderMap(user,section);
	if ( map == null ) {
	    map = new UserHomeFolderMap();
	    map.setHomeFolderUser(user);
	    map.setHomeSection(section);
	}

	return map;
    }
}
