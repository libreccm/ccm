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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;


// Support for Logging.

/**
 * The PublishedLink class is used to create objects that access
 * a single row in the publish_to_fs_links table.
 *
 * @author <a href="mailto:teeters@arsdigita.com">Jeff Teeters</a>
 * @version 1.0
 **/
class PublishedLink extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
      "com.arsdigita.cms.publishToFile.PublishedLink";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    // Creates a s_logging category with name = to the full name of class
    private static Logger s_log =
        Logger.getLogger( PublishedLink.class.getName() );


    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "PublishedLink".
     **/
    public PublishedLink() { // throws DataObjectNotFoundException {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public PublishedLink(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }


    /**
     * Constructor. Creates a new DomainObject instance to encapsulate a given
     * data object.
     *
     * @param dataObject The data object to encapsulate in the new domain 
     * object.
     * @see com.arsdigita.persistence.Session#retrieve(String)
     **/
    public PublishedLink(DataObject dataObject) {
        super(dataObject);
    }


    /***
     * Save information about published link using
     * PublishedLink object (publish_to_fs_links table).
     * @param sourceId - id of source file (file containing link).
     * @param targetId - id of target file (link referenced).
     * @param isChild  - '1' if child (i.e. this item should be deleted
     *   if all sources referencing it are deleted), '0' otherwise.
     * @return The id of record saved in publish_to_fs_files table.
     ***/
    static void saveLinkInfo(BigDecimal sourceId, BigDecimal targetId, boolean isChild)
        throws PublishToFileException {
        try {
            PublishedLink link = alreadyHaveLink(sourceId, targetId);
            if (link == null) {
                // new link
                link = new PublishedLink();
                BigDecimal id = QueueEntry.generateID();
                link.setId(id);
                link.setSource(sourceId);
                link.setTarget(targetId);
            } else {
                s_log.warn("Found previously existing link.  Source=" + sourceId +
                           " target=" + targetId + " previous isChild=" +
                           link.getIsChild() + " new isChild=" + isChild);
                // previous link exists.  Normally this should not occur.
                // Not clear how to set child flag.  For now, set child flag
                // to value for link being added.
            }
            link.setIsChild(new Boolean(isChild));
            link.save();
        } catch (SQLException e) {
            throw new PublishToFileException("Unable to save PublishedLink: " +
                                             " sourceId=" + sourceId + " targetId=" + targetId +
                                             " error=" +e.getMessage());
        }
    }


    /***
     * Check if file was already published.  If so return the PublishedFile object.
     * If not, return null.
     ***/
    static PublishedLink alreadyHaveLink(BigDecimal sourceId, BigDecimal targetId)
        throws SQLException {
        PublishedLink link = null;
        DataQuery query = SessionManager.getSession().
            retrieveQuery("com.arsdigita.cms.publishToFile.checkIfAlreadyHaveLink");
        query.setParameter("source", sourceId);
        query.setParameter("target", targetId);
        if (query.next()) {
            // item found, get the object
            DataObject dobj = (DataObject) query.get("publishedLink");
            link = new PublishedLink(dobj);
        }
        query.close();
        return link;
    }



    private final static String ID = "id";
    private final static String SOURCE = "source";
    private final static String TARGET = "target";
    private final static String IS_CHILD = "isChild";

    public void setId(BigDecimal id) {
        set(ID, id);
    }

    public void setSource(BigDecimal source) {
        set(SOURCE, source);
    }

    public void setTarget(BigDecimal target) {
        set(TARGET, target);
    }

    public void setIsChild(Boolean isChild) {
        set(IS_CHILD, isChild);
    }

    //accessors
    public BigDecimal getId() {
        return (BigDecimal)get(ID);
    }

    public BigDecimal getSource() {
        return (BigDecimal)get(SOURCE);
    }

    public BigDecimal getTarget() {
        return (BigDecimal)get(TARGET);
    }

    public Boolean getIsChild() {
        return (Boolean) get(IS_CHILD);
    }
}
