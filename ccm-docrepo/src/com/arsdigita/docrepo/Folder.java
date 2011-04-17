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
package com.arsdigita.docrepo;


import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;
import java.util.StringTokenizer;

/**
 * Represents a Folder in the document repository application.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 * @version $Id: Folder.java  pboy $
 */
public class Folder extends ResourceImpl implements Constants {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.docrepo.Folder";

    /**
     * Constructor.
     */
    public Folder() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Creates a new folder object.
     */
    public Folder(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Creates a new folder by retrieving it based on ID.
     *
     * @param id - the BigDecimal ID
     */
    public Folder(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a new folder by retrieving it based on OID.
     *
     * @param oid - the Object oID
     */
    public Folder(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Folder(String objectTypeString) {
        super(objectTypeString);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by <i>type</i>.
     *
     * @param type The <code>ObjectType</code> of the contained
     * <code>DataObject</code>.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     */
    protected Folder(ObjectType type) {
        super(type);
    }


    /**
     * Creates a named folder with a description.
     *
     * @param name the name of the folder
     * @param description the description of the folder contents (may
     * be null)
     */
    public Folder(String name, String description) {
        super(BASE_DATA_OBJECT_TYPE, name, description);
    }


    /**
     * Creates a sub folder inside a given parent folder.
     *
     * @param name the name of the folder
     * @param description the description of the folder contents (may
     * be null)
     */
    public Folder(String name, String description, Folder parent) {
        super(BASE_DATA_OBJECT_TYPE);
        if (parent.hasResource(name)) {
            throw new ResourceExistsException("A resource named " +
                    name +
                    " exists in the parent folder " +
                    parent.getName());
        }
        setParent(parent);
        setName(name);
        setDescription(description);
    }

    @Override
    protected void beforeSave() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("folder before save");
        }
        set(IS_FOLDER, Boolean.TRUE);
        super.beforeSave();
    }

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public void setParent(Resource parent) {
        if (parent instanceof Folder) {
            final String parentPath = parent.getPath();
            final String path = getPath();
            if (s_log.isDebugEnabled()) {
                s_log.debug("folder set parent Parent path: " +
                           parentPath +
                           " folder path: " +
                           path);
            }
            if (path != null && parentPath.startsWith(path)) {
                throw new ResourceException(
                              "The parent is a child of this folder. "
                              + "Parent path: " + parentPath
                              + " folder path: " + path);
            }

        }

        super.setParent(parent);
    }

    /**
     * Retrieves the named sub folder by searching from the current folder
     * along the given path.  Returns the child folder or throws a
     * DataObjectNotFoundException if the subfolder does not exist.
     *
     * @return child folder if and only if it exists as a sub folder
     * with the correct path
     */
    public Folder retrieveFolder(String path) throws DataObjectNotFoundException,
                                                     InvalidNameException {
        return new Folder(getResourceID(path));
    }

    /**
     *
     * @param path
     * @return
     * @throws DataObjectNotFoundException
     * @throws InvalidNameException
     */
    public File retrieveFile(String path) throws DataObjectNotFoundException,
                                                 InvalidNameException {
        return new File(getResourceID(path));
    }

    /**
     * 
     * @param name
     * @return
     */
    public boolean hasResource(String name) {
        DataAssociation da = (DataAssociation) get("immediateChildren");
        DomainCollection resources = new DomainCollection(da);
        resources.addEqualsFilter(NAME, name);
        try {
            return resources.next();
        } finally {
            resources.close();
        }
    }


    /**
     * Creates the sub folder named by path, including any necessary
     * but nonexistent parent folders.
     */
    public Folder createFolders(final String path)
        throws InvalidNameException,
               ResourceExistsException
    {
        int pec;
        if ( (pec = isValidPath(path)) != 0 ) {
            throw new InvalidNameException(pec);
        }

        // Build up the array of sub folders to search for.  First we
        // just extract the set of path elements from the path
        // string.  If the path is "a/b/c", this will produce:
        //
        // name[0] = a
        // name[1] = b
        // name[2] = c

        StringTokenizer st = new StringTokenizer(path,SEPARATOR);
        int pathElementCount = st.countTokens();

        String name[] = new String[pathElementCount];
        for (int i = 0; i < pathElementCount; i++) {
            name[i] = st.nextToken();
        }

        // Build the array of sub paths.  If the original path is
        // "a/b/c", this will produce:
        //
        // subPath[0] = /a
        // subPath[1] = /a/b
        // subPath[2] = /a/b/c

        String subPath[] = new String[pathElementCount];
        for (int i = 0; i < pathElementCount; i++) {
            StringBuffer buf = new StringBuffer();
            for (int j = 0; j <= i; j++) {
                buf.append(SEPARATOR);
                buf.append(name[j]);
            }
            subPath[i] = buf.toString();
        }

        // Now we check for the existence of each Folder starting from
        // the end until we find one that exists.

        Folder folder[] = new Folder[pathElementCount];
        int lastPathElementIndex = pathElementCount - 1;
        int retrievedFolderIndex = -1;
        for (retrievedFolderIndex = lastPathElementIndex;
             retrievedFolderIndex >= 0;
             retrievedFolderIndex--) {
            try {
                folder[retrievedFolderIndex] =
                    retrieveFolder(subPath[retrievedFolderIndex].substring(1));
                break;
            } catch (DataObjectNotFoundException ex) {
                // continue retrieving
            }
        }

        // Verify that we did NOT retrieve the first folder.

        if (retrievedFolderIndex == lastPathElementIndex) {
            throw new ResourceExistsException
                ("createFolders: folder exists:" + path);
        }

        // We now loop back and create each parent folder.

        Folder parent = (retrievedFolderIndex < 0) ?
            this : folder[retrievedFolderIndex];

        for (int i = retrievedFolderIndex + 1; i < pathElementCount; i++) {
            folder[i] = new Folder(name[i], null, parent);
            folder[i].save();
            parent = folder[i];
        }

        return folder[lastPathElementIndex];
    }

    /**
     * 
     * @return
     */
    public boolean isFolder() {
        return true;
    }

    /**
     * 
     * @return
     */
    public boolean isFile() {
        return false;
    }

    /**
     * Returns the display name for a folder, which is equivalent to
     * calling getName().
     */
    public String getDisplayName() {
        return getName();
    }

    /**
     * Copy this folder to a new location.  Recursively copies all
     * children from the original resource location to the new
     * resource location.
     */
    public Resource copyTo(String name, Resource parent) {
        Folder dest = new Folder();

        copy(this,dest);

        dest.setName(name);
        dest.setParent(parent);
        dest.save();

        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.docs.getDirectChildren");
        query.setParameter("parentID", getID());

        while (query.next()) {

            OID oid = new OID(ResourceImpl.BASE_DATA_OBJECT_TYPE,
                              query.get("id"));

            try {
                ResourceImpl orig = (ResourceImpl)
                    DomainObjectFactory.newInstance(oid);
                orig.copyTo(dest);
            } catch (DataObjectNotFoundException ex) {
                throw new ResourceException(ex);
            }
        }

        return dest;
    }
}
