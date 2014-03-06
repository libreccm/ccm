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


import com.arsdigita.db.Sequences;
import com.arsdigita.web.Web;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.versioning.VersionedACSObject;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is the abstract parent class of {@link File} and {@link
 * Folder}.  It provides an implementation of the {@link Resource}
 * interface for a database-backed virtual filesystem.
 *
 * This implementation stores file in Oracle exclusively.
 * All versioning API is by inheritance from VersionedACSObject
 * (which will eventually
 * also include access logging - so wait for VersionedACSObject)
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 * @version $Revision: #18 $ $Date: 2004/08/17 $ $Author: dennis $
 * @version $Id: ResourceImpl.java  pboy $
 */
public abstract class ResourceImpl extends VersionedACSObject
                                   implements Resource {

    /** Logger instance for debugging support.  */
    protected static Logger s_log = Logger.getLogger(ResourceImpl.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.docrepo.ResourceImpl";
    public static final String PARENT = "parent";

    /**
     * 
     * @return
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of BASE_DATA_OBJECT_TYPE.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.metadata.ObjectType
     */
    protected ResourceImpl() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>typeName</i>.
     *
     * @param typeName The name of the <code>ObjectType</code> of the
     * contained <code>DataObject</code>.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     */
    protected ResourceImpl(String typeName) {
        super(typeName);
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
    protected ResourceImpl(ObjectType type) {
        super(type);
    }

    /**
     * Creates a new resource. Properties of this object are not made
     * persistent until the <code>save()</code> method is called.
     */
    protected ResourceImpl(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Creates a resource given the ID
     *
     * @param id the BigDecimal ID
     */
    protected ResourceImpl(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a resource given the OID
     *
     * @param oid the Object OID
     *
     */
    protected ResourceImpl(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Creates a named resource of the specified type.
     */
    protected ResourceImpl(String type,
                           String name,
                           String description) {
        this(type);
        setName(name);
        setDescription(description);
    }

    /**
     * Creates a named resource of the specified type with a given parent.
     */
    protected ResourceImpl(String type,
                           String name,
                           String description,
                           ResourceImpl parent) {
        this(type,name,description);
        setParent(parent);
    }

    /**
     * Creates an empty resource with the given parent.
     */
    protected ResourceImpl(String type,
                           ResourceImpl parent) {
        this(type);
        setParent(parent);
    }

    private boolean m_wasNew;

    protected void beforeSave() {
        m_wasNew = isNew();
        /**
         * Update the path for this resource before saving.  It should be
         * sufficient to update the path only when the name changes, but
         * the current implementation is conservative about path updates.
         */
        final boolean pathChanged = isPropertyModified(Repository.PARENT) 
                                    || isPropertyModified(Repository.NAME);

        if (pathChanged) {
            String oldPath = null;
            if (!isNew()) {
                oldPath = getPath();
            }
            final Resource parent = getParent();
            if (parent != null) {
                final String parentPath = parent.getPath();
                setPath(parentPath + SEPARATOR + getName());
            } else {
                setPath(SEPARATOR + getName());
            }
            if (oldPath != null) {
                updateChildren(oldPath);
            }
        }
        if(isNew()) {
            User user = Web.getWebContext().getUser();
            if (user != null) {
                setCreationUser(user);
                setLastModifiedUser(user);
            }         
            java.util.Date date = new java.util.Date();
            setCreationDate(date);
            setLastModifiedDate(date);
            setCreationIP();
        } else {
            User user = Web.getWebContext().getUser();
            if (user != null){
                setLastModifiedUser(user);
            }            

            setLastModifiedDate(new java.util.Date());
            setLastModifiedIP();

        }

        super.beforeSave();
    }

    protected void afterSave() {
        super.afterSave();
                
        if (m_wasNew  && !isRoot()) {
            Object obj = getParent();

            if (obj instanceof ACSObject) {
                PermissionService.setContext(this, (ACSObject) obj);
            }
            new KernelExcursion() {
                protected void excurse() {
                    Party currentParty = Kernel.getContext().getParty();
                    setParty(Kernel.getSystemParty());
                    PermissionService.grantPermission
                        (new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                  ResourceImpl.this,
                                                  currentParty));
                }
            }.run();

        }

    }

    /**
     * Deletes a resource.  Throws a {@link ResourceNotEmptyException}
     * if the resource still contains children.
     */
    public void delete() throws ResourceNotEmptyException {
        // We mangle names here so that a deleted "foo" doesn't stop
        // the user from adding a new "foo".
        //
        // XXX: This is not a pleasing way to fix this, but the truly
        // right thing to do is fix versioning, not doc repos's use of
        // versioning.

        String name = getName();
        String suffix = "";

        try {
            suffix = "-" + Sequences.getNextValue();
        } catch (SQLException se) {
            throw new PersistenceException(se.getMessage());
        }

        int suffixLength = suffix.length();
        int nameLength = name.length();

        if (nameLength > suffixLength) {
            setName(name.substring(0, nameLength - suffixLength) + suffix);
        } else {
            setName(name + suffix);
        }

        save();

        try {
            super.delete();
        } catch (PersistenceException ex) {
            throw new ResourceNotEmptyException(ex.getMessage());
        }
    }

    /**
     * Update the denormalized path for each child of this resource.
     */

    private void updateChildren(String oldPath) {
        String path = getPath();
        s_log.debug("Running updateChildren for ID " + getID() + " on path " + path);
        // Execute the data operation to update all children
        Session session = SessionManager.getSession();
        DataOperation op = session.retrieveDataOperation
            ("com.arsdigita.docrepo.updateChildren");
        // with byline Version BASE_DATA_OBJECT_TYPE =
        //                   "com.arsdigita.docs.ResourceImpl"
        // so all retrieveQueries might have be to corrected.
        //  ("com.arsdigita.docs.updateChildren");
        // remove comment after intensive testing!
        op.setParameter("rootPath", path);
        op.setParameter("oldPath", oldPath);
        op.setParameter("oldRootPathLength", new Integer(oldPath.length()+1));
        op.setParameter("parentResource", getID());
        op.execute();
        op.close();
    }


    public String getName() {
        return (String) get(Repository.NAME);
    }

    public void setName(String name) {
        set(Repository.NAME, name);
    }

    public String getDescription() {
        return (String) get(Repository.DESCRIPTION);
    }

    public void setDescription(String description) {
        set(Repository.DESCRIPTION, description);
    }

    public Resource getParent() {
        DataObject parent = (DataObject)get(PARENT);
        if (parent != null) {
            return (Resource)DomainObjectFactory.newInstance(parent);
        } else {
            return null;
        }
    }

    /**
     * Set the parent of this resource.
     *
     * @param parent the parent of this resource
     */
    public void setParent(Resource parent) {
        set(PARENT, parent);
    }


    protected BigDecimal getParentResourceID() {
        Resource parent = getParent();
        if (parent != null) {
            return parent.getID();
        } else {
            return null;
        }
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public String getPath() {
        String path = (String) get(Repository.PATH);
        return path;
    }

    private void setPath(String path) {
        set(Repository.PATH, path);
    }

    public abstract boolean isFolder();

    public abstract boolean isFile();

    public URL toURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the path corresponding to a given resource.
     */
    protected static String retrievePath(BigDecimal id) {
        DataCollection collection = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter(ID, id);

        String ids = null;
        if (collection.next()) {
            ids = (String)collection.get(Repository.PATH);
            collection.close();
        } else {
            // this means that the id is not valid so there is no path
            return "";
        }

        StringBuffer ancestors = new StringBuffer();
        if (ids == null) {
            // this should not happen
            return ancestors.toString();
        }

        collection = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        Filter filter = collection.addFilter(Repository.PATH + " <= :ancestors");
        filter.set("ancestors", ids);
        filter = collection.addFilter
            (Repository.PATH + " = substr(:path, 1, length(" + Repository.PATH +"))");
        filter.set("path", ids);

        collection.addOrder(Repository.PATH);

        while (collection.next()) {
            ancestors.append(SEPARATOR + collection.get(Repository.NAME));
        }

        return ancestors.toString();
    }

    /**
     * Verifies that the given string corresponds to a valid relative
     * path name.
     * @return 0 for a system-valid parthname, otherwise error codes
     * defined in InvalidNameException.
     * @see InvalidNameException
     */
    protected static int isValidPath(String path) {
        if (path.length() == 0) {
            return InvalidNameException.ZERO_CHARACTERS_ERROR;
        }

        // check for leading slash
        if (path.charAt(0) == SEPARATOR_CHAR) {
            return InvalidNameException.LEADING_FILE_SEPARATOR_ERROR;
        }

        // check for trailing separator
        if (path.charAt(path.length()-1) == SEPARATOR_CHAR) {
            return InvalidNameException.TRAILING_FILE_SEPARATOR_ERROR;
        }

        // tokenize and validate each token
        StringTokenizer st = new StringTokenizer(path, SEPARATOR);
        while (st.hasMoreTokens()) {
            String name = st.nextToken().trim();
            if (name.length() == 0) {
                return InvalidNameException.ZERO_CHARACTERS_ERROR;
            }
            int nec;
            if ((nec = isValidName(name)) != 0 ) {
                return nec;
            }
        }

        return 0;
    }

    /**
     * Verifies that the given string corresponds to a valid absolute
     * path name.
     */
    protected static boolean isAbsolutePath(String path) {
        if (path.length() == 0) {
            return false;
        }

        return (path.trim().charAt(0) == SEPARATOR_CHAR &&
                0 == isValidPath(path.substring(1)));
    }

    /**
     * Verifies that the string only contains valid characters for
     * resource names.  The following are allowed:
     *
     *    [a-z][A-Z][0-9][-., ]
     *
     * In addition, names cannot begin with ".", i.e. we do NOT
     * support file names like ".profile" at the moment.
     * The current implementation does not allow international
     * characters for resource names.
     *
     * @return 0 for a system-valid resource name, otherwise error codes
     * defined in InvalidNameException.
     * @see InvalidNameException
     */
    protected static int isValidName(String name) {
        Perl5Util util = new Perl5Util();

        // check invalid characters at start of name
        String INVALID_START_PATTERN = "/^[.]+/";
        if (util.match(INVALID_START_PATTERN, name)) {
            return InvalidNameException.LEADING_CHARACTER_ERROR;
        }

        // check invalid characters internal to name
        String INVALID_NAME_PATTERN = "/[^a-zA-Z0-9\\_\\.\\-\\ ]+/";
        if (util.match(INVALID_NAME_PATTERN, name)) {
            return InvalidNameException.INVALID_CHARACTER_ERROR;
        }

        return 0;
    }

    /**
     * Returns a canonical path by removing leading or trailing
     * separator characters as needed.
     */
    public static String getCanonicalPath(String path)
        throws InvalidNameException {

        String errMsg = "Unable to construct canonical path for: ";

        if (null == path) {
            throw new InvalidNameException(errMsg + path);
        }

        int start;
        if (path.charAt(0) == SEPARATOR_CHAR) {
            start = 1;
        } else {
            start = 0;
        }

        int end;
        int length = path.length();
        if (path.charAt(length-1) == SEPARATOR_CHAR) {
            end = length-1;
        } else {
            end = length;
        }

        String canonicalPath = path.substring(start,end);
        int pec;
        if ( (pec=isValidPath(canonicalPath)) != 0) {
            throw new InvalidNameException(pec);
        }

        return canonicalPath;
    }

    public BigDecimal getResourceID() {
        return (BigDecimal)getID();
    }

    /**
     * Retrieve the ID of a child resource using a relative path.
     * This method is useful for checking the existence of a named
     * child without the overhead of instantiating that child.
     *
     * @return the BigDecimal ID of the resource with the specified
     * relative path
     */
    public BigDecimal getResourceID(String path)
        throws DataObjectNotFoundException, InvalidNameException {
        int pec;
        if ( (pec = isValidPath(path)) != 0) {
            throw new InvalidNameException(pec);
        }

        String absPath = getPath() + SEPARATOR + path;
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.docrepo.getResourceByPath");
        // with byline Version BASE_DATA_OBJECT_TYPE =
        //                   "com.arsdigita.docs.ResourceImpl"
        // so all retrieveQueries might have be to corrected.
        //  ("com.arsdigita.docs.getResourceByPath");
        // remove comment after intensive testing!
        query.setParameter("targetPath", absPath);

        if (query.next()) {
            BigDecimal id = (BigDecimal) query.get(ID);
            query.close();
            return id;
        } else {
            throw new DataObjectNotFoundException
                ("No resource with path " + absPath);
        }
    }

    /**
     * Return the list of declared property names for a Resource.
     * This list only includes the properties that should be
     * duplicated during a copy operation.
     */
    protected Vector getPropertyNames() {
        Vector names = new Vector();
        names.addElement(Repository.NAME);
        names.addElement(Repository.DESCRIPTION);
        names.addElement(Repository.IS_FOLDER);
        return names;
    }

    @Override
    public java.util.Date  getLastModifiedDate() {
       java.util.Date date =  (java.util.Date)get("lastModifiedDate");
       return date;
    }

    public void  setLastModifiedDate(java.util.Date date) {
       set("lastModifiedDate",date);
    }

    @Override
    public java.util.Date  getCreationDate() {
       java.util.Date date =  (java.util.Date)get("creationDate");
       return date;
    }

    public void  setCreationDate(java.util.Date date) {
       set("creationDate",date);
    }

    @Override
    public User getCreationUser() {
       DataObject dobj = (DataObject)get("creationUser");
       if(dobj == null) {
         throw new DataObjectNotFoundException("Creation User not found");
       }
       User user = (User)DomainObjectFactory.newInstance(dobj);
       return user;
    }

    public void setCreationUser(User user) {
       set("creationUser", user);
    }

    @Override
    public User getLastModifiedUser() {
       DataObject dobj = (DataObject)get("lastModifiedUser");
       if(dobj == null) {
         throw new DataObjectNotFoundException("Last User not found");
       }
       User user = (User)DomainObjectFactory.newInstance(dobj);
       return user;
    }

    public void setLastModifiedUser(User user) {
       set("lastModifiedUser", user);
    }

    @Override
    public String getCreationIP() {
        String ip = (String)get("creationIP");
        return ip;
    }

    public void setCreationIP() {
        String ip;
        HttpServletRequest req = Web.getRequest();
            if(req == null)
              ip = "127.0.0.1";
            else
              ip = req.getRemoteAddr();
        set("creationIP",ip);
    }

    @Override
    public String getLastModifiedIP() {
        String ip = (String)get("lastModifiedIP");
        return ip;
    }

    public void setLastModifiedIP() {
        String ip;
        HttpServletRequest req = Web.getRequest();
            if(req == null)
              ip = "127.0.0.1";
            else
              ip = req.getRemoteAddr();
        set("lastModifiedIP",ip);
    }

    /**
     * Copy the generic properties of a Resource. Uses an explicit
     * list of properties to copy as determined by {@link
     * getPropertyNames}.
     */
    protected static void copy(ResourceImpl src, ResourceImpl dest) {
        Vector props = src.getPropertyNames();
        for (int i = 0; i < props.size(); i++) {
            String name = (String) props.elementAt(i);
            dest.set(name, src.get(name));
        }
    }

    public Resource copyTo(Resource parent) {
        return copyTo(getName(), parent);
    }

    public Resource copyTo(String name) {
        return copyTo(name, getParent());
    }

    public abstract Resource copyTo(String name, Resource parent);


    /**
     * Copy method implemented by extensions of this class.  This
     * method is protected because it isn't type safe, even though the
     * BigDecimal ID of the parent resource is all that we really need
     * to complete a copy.
     */
    //protected abstract Resource copyTo(String name, BigDecimal parent);
}
