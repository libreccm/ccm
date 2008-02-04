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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Host;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * The PublishedFile class is used to create or modify objects that access
 * a single row in the publish_to_fs_files table.
 *
 * @author <a href="mailto:teeters@arsdigita.com">Jeff Teeters</a>
 * @author <a href="mailto:sshinde@redhat.com">Shashin Shinde</a>
 * @author <a href="mailto:dlutter@redhat.com">David Lutterkort</a>
 * @version 1.0
 **/
class PublishedFile extends DomainObject {
  
    public static final String versionId = "$Id";
  
    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.cms.publishToFile.PublishedFile";

    private final static String ID = "id";
    private final static String HOST = "host";
    private final static String ITEM_TYPE = "itemType";
    private final static String ITEM_ID =  "itemId";
    private final static String DRAFT_ID = "draftId";
    private final static String FILE_NAME = "fileName";

    private static Logger s_log = Logger.getLogger(PublishedFile.class);

    protected PublishedFile() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected PublishedFile(String objectType) {
        super(objectType);
    }
  
    /**
     * Constructor. Creates a new DomainObject instance to encapsulate a given
     * data object.
     *
     * @param dataObject The data object to encapsulate in the new domain
     * object.
     * @see com.arsdigita.persistence.Session#retrieve(String)
     **/
    public PublishedFile(DataObject dataObject) {
        super(dataObject);
    }

    // FIXME: This method only exists because the CMS Asset class requires a
    // file to write output to. It would be much cleaner if an asset could be
    // written to any OutputStream; publishing an asset could then use the
    // getOutputStream method and would not need to expose an underlying
    // file.
    File getFile() {
        DestinationStub dest = PublishToFile.getDestination(getItemType());

        // fix when turning off p2fs for items
        if (dest == null) {
            return null;
        }

        File f = new File(dest.getFile(),
                          getFileName());
        if (s_log.isDebugEnabled()) {
            s_log.debug("published file path is " + f.getPath());
        }

        File p = f.getParentFile();
        if ( ! p.exists() ) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Making all directories leading up to " + 
                            "& including" + p.getPath());
            }
            p.mkdirs();
        }
        return f;
    }
  
    /**
     * Get an output stream to which the contents for this file should be
     * written. 
     *
     * @return the <code>OutputStream</code> to which the contents for the
     * published file should be written.
     */
    public OutputStream getOutputStream() 
        throws java.io.FileNotFoundException {
        return new FileOutputStream(getFile());
    }

    /**
     * Delete the file in the filessystem and in the database.
     */
    public void delete() {
        DestinationStub dest = PublishToFile.getDestination(getItemType());
    
        File f = getFile();
        f.delete();

        // work around persistence bug that prevents get of properties after
        // call to delete (ashah, 2003-06-11)
        String fileName = getFileName();
        Host host = getHost();

        super.delete();

        // See if the directory should be cleaned up
        long n = 0;
        // path is the name of a directory relative to the doc root, whereas
        // p is the same directory, but as an absolute path in the file system
        // In general, there is no directory corresponding to path ! [lutter]
        File path = new File(fileName).getParentFile();
        File p = f.getParentFile();
        do {
            Assert.assertTrue(p.isDirectory());
            DataCollection coll = SessionManager.getSession()
                .retrieve(BASE_DATA_OBJECT_TYPE);
            coll.addEqualsFilter(HOST + "." + Host.ID, host.getID());
            coll.addFilter(FILE_NAME + " like '" + path.getPath()+"%'");
            n = coll.size();
            if ( n == 0 ) {
                s_log.debug("Deleting emptied directory "
                            + path.getPath());
                File[] files = p.listFiles();
                for (int i=0; i < files.length; i++) {
                    s_log.debug("Deleting " + files[i].getPath());
                    files[i].delete();
                }
                if (p.compareTo(dest.getFile()) != 0) {
                    s_log.debug("Deleting " + p.getPath());
                    p.delete();
                } else {
                    s_log.debug("NOT Deleting " + p.getPath() + 
                                " because it is the doc root");
                }
            }
            path = path.getParentFile();
            p = p.getParentFile();
        } while ( path != null && p != null && n == 0 );
    }

    public static PublishedFile create(ContentItem item,
                                       String fileName,
                                       Host host) {
        Assert.truth(item != null, "item is not null");
        Assert.truth(item.isLive(), "item is live");
        Assert.truth(fileName != null, "fileName is not null");
        Assert.truth(host != null, "host is not null");
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("creating published file with item " + item.getOID() + 
                        " and fileName " + fileName + " on host " + host);
        }

        PublishedFile file = new PublishedFile();
        file.set(ID, QueueEntry.generateID());
        file.set(ITEM_TYPE, item.getSpecificObjectType());
        file.set(ITEM_ID, item.getID());
        file.set(DRAFT_ID, item.getWorkingVersion().getID());
        file.set(FILE_NAME, fileName);
        file.setAssociation(HOST, host);
        return file;
    }

    /***
     * Save information about published file using
     * PublishedFile object (publish_to_fs_files table).
     * @param itemId - cms item_id of item associated with file.
     * @param fileName - name of file (includes path from document root).

     * @param parentId - id of parent folder (will be live).
     * @return The id of record saved in publish_to_fs_files table.
     ***/
    static PublishedFile loadOrCreate(ContentItem item,
                                      String fileName, 
                                      Host host) {
        Assert.truth(item != null, "item is not null");
        Assert.truth(item.isLive(), "item is live");
        Assert.truth(fileName != null, "fileName is not null");
        Assert.truth(host != null, "host is not null");

        PublishedFile f = findByFileName(fileName, host);
        if (f == null) {
            f = create(item, fileName, host);
            f.save();
        }

        Assert.assertEquals(host, f.getHost());
        Assert.assertEquals(item.getID(), f.getItemId());
        Assert.assertEquals(item.getWorkingVersion().getID(), f.getDraftId());
        Assert.assertEquals(fileName, f.getFileName());
        return f;
    }
  

    static void deleteAll(BigDecimal id, Host host) {
        DataCollection coll = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        coll.addEqualsFilter(HOST + "." + Host.ID, host.getID());
        coll.addEqualsFilter(ITEM_ID, id);
        while ( coll.next() ) {
            PublishedFile f = new PublishedFile(coll.getDataObject());
            f.delete();
        }
    }

    /**
     * Retrieves a collection of all files on all hosts
     */
    public static DomainCollection retrieveAll() {
        return retrieveAll(null);
    }
    
    /**
     * Retrieves a collection of all files on a given host.
     * @param host the host to retrieve files for, or null to 
     * return files for all hosts
     */
    public static DomainCollection retrieveAll(Host host) {
        Session session = SessionManager.getSession();
        DataCollection files = session.retrieve(BASE_DATA_OBJECT_TYPE);
        return new DomainCollection(files);
    }

    /***
     * Checks if a file with given fileName is already published.  NOTE: That
     * a fileName can also be an asset.And since one asset can be added in
     * the content of an item more than once, method simply logs warning
     * message and returns the Found object.But if it finds more than one
     * entry then it throws PublishToFileException.
     * @return PublishedFile object that is retrieved from the database for
     * the given fileName , and if no entry is found it returns null
     ***/
    public static PublishedFile findByFileName(String fileName, 
                                               Host host) {
        DataCollection coll = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        coll.addEqualsFilter(HOST + "." + Host.ID, host.getID());
        coll.addEqualsFilter(FILE_NAME, fileName);

        PublishedFile result = null;
        if (coll.next()) {
            result = new PublishedFile(coll.getDataObject());
        }
        coll.close();
        return result;
    }

    public BigDecimal getItemId() { 
        return (BigDecimal)get(ITEM_ID); 
    }
    
    public ContentItem getItem() {
        OID oid = new OID(getItemType(),
                          getItemId());
 
        ContentItem item;

        try {
            item = (ContentItem) DomainObjectFactory.newInstance( oid );
        } catch (DataObjectNotFoundException donfe) {
            item = null;
        }

        return item;
    }
  
    public BigDecimal getDraftId() {
        return (BigDecimal) get(DRAFT_ID);
    }

    public ContentItem getDraftItem() {
        OID oid = new OID(getItemType(),
                          getDraftId());
 
        ContentItem item = (ContentItem)
            DomainObjectFactory.newInstance( oid );
        
        return item;
    }


    public String getItemType() {
        return (String)get(ITEM_TYPE);
    }

    public Host getHost() {
        DataObject obj = (DataObject)get(HOST);
        return new Host(obj);
    }

    public String getFileName() {
        return (String) get(FILE_NAME);
    }
}
