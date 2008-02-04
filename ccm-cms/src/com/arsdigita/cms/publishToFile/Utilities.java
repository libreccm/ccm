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


import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Host;
import com.arsdigita.web.Web;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.log4j.Logger;


/***
 * <b><font color="red">Experimental</font></b>
 * Static utility methods used in writing content and templates
 * to the file system.
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @version $Revision: #21 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class Utilities {

    private static Logger s_log =
            Logger.getLogger(com.arsdigita.cms.publishToFile.Utilities.class.getName());

    /**
     * Return the string that is used to indicate the output media (template
     * use context) in an item's URL. If the <code>media</code> is null or
     * <code>TemplateManager.PUBLIC_CONTEXT</code>, the empty string is
     * returned, otherwise the string <code>'$' + media</code>.
     *
     * @param media the output media (template use context)
     * @return the string that should be used in the item's URL to
     * distinguish with which output template the item was formatted.
     * @post return != null
     */
    static String getMediaIndicator(String media) {
        if ( media == null || TemplateManager.PUBLIC_CONTEXT.equals(media) ) {
            return "";
        }
        return '$' + media;
    }

    /***
     * Get url for retrieving item, not including the leading 
     * servlet prefix (eg /ccm).
     *
     * @param item  Content item
     * @return url used to get rendered html page for item.
     ***/

    static String getItemURL(ContentItem item) {
        Assert.assertNotNull(item);

        ContentSection section = item.getContentSection();
        ContentItem working = item.getWorkingVersion();
        ContentItem liveItem = working.getLiveVersion();

        String url = "";
        s_log.debug("Getting live item for " + item.getID());
        s_log.debug("Working is " + working.getID());
        if (liveItem != null ) {
            s_log.debug("getting url for item " + liveItem.getName() + " in section " + section.getName());
            url = section.getItemResolver().generateItemURL(null, liveItem, section, ContentItem.LIVE);
            s_log.debug("Url is: "  + url);
        } else {
            s_log.warn("liveItem is null");
        }

        return url;
    }


    /***
     * Get item location in the filesystem.
     * @param item  Content item
     * @return location on file system for item, including
     * the path to the item, and the item name, but not any source base prefix.
     * This assumes the location matches url generated by item resolver.
     ***/

    public static String getItemLocation(ContentItem item) {
        return PublishToFile.stripSourceBase(getItemURL(item));
    }

    /**
     * Moves files in File System
     * @param dstFolder   The destination Folder for Item to be copied, must not be null
     * @param srcFodler   The source Folder where Item must be taken, must not be null
     * @param bundle      The bundle item whcih must be moved, can be null, in this case srcFolder moved to dstFolder
     *
     * @return true if item was successfuly moved
     */
    public static boolean moveItem( Folder dstFolder, Folder srcFolder, ContentItem item ) {
        if (Assert.isEnabled()) {
            Assert.assertTrue(item != null && srcFolder != null && dstFolder != null);
        }

        final Host host = Host.retrieve(Web.getConfig().getHost());

        // FIXME: validateLocation should be removed. All FS logic should go
        // into PublishedFile [lutter]
        String dst = validateLocation(dstFolder, true);
        if ( item instanceof ContentBundle ) {
            Assert.assertTrue(!Folder.INDEX.equals(item.getName()), "don't pass ContentBundle as Index Item, better to pass Folder");
            moveBundle((ContentBundle)item, dst, host);
        } else {
            String path = item.getPath();
            if (path == null || path.equals(""))
                return false;

            ContentSection section = item.getContentSection();
            Assert.assertNotNull(section);
            path = PublishToFile.stripSourceBase(section.getURL() + path);
            Assert.assertTrue(path != null && path.length() > 0);
            
            File root = PublishToFile.getDestination(item.getSpecificObjectType()).getFile();
            String src = new File(root, path).getAbsolutePath();
            String cmd = "/bin/mv -f " + src + " " + dst;
            // s_log.debug("Moving item: source " + srcFolder + ", destination: " + dstFolder);
            try {
                executeCommand(cmd);
            } catch ( PublishToFileException ex ) {
                s_log.warn("Could not move folder on Fyle System, message: " + ex.getMessage());
            }
        }

        updateMovedItems(item, srcFolder, dstFolder, host);

        return true;
    }

    /**
     * Moves ContentBundle, i.e. language instances
     */
    private static void moveBundle(ContentBundle bundle, 
                                   String dstPath, 
                                   Host host) {
        ItemCollection ic = bundle.getInstances();
        while ( ic.next() ) {
            if ( ContentItem.LIVE.equals(ic.getVersion()) )
                moveItem(ic.getContentItem(), dstPath, host);
        }
        ic.close();
    }
    /**
     * Moves Single language instance
     */
    private static void moveItem(ContentItem item, 
                                 String dstPath,
                                 Host host) {
        Assert.assertTrue(ContentItem.LIVE.equals(item.getVersion()), "ContentItem item = " + item + " is not a live version");
        String docRoot = PublishToFile.getDestination(item.getSpecificObjectType()).getFile().getAbsolutePath();

        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.publishToFile.getRelatedFiles");
        query.setParameter("itemId", item.getID());
        query.setParameter("hostId", host.getID());
        while ( query.next() ) {
            String fileName = (String)query.get("fileName");
            Assert.assertNotNull(fileName);
            fileName = docRoot + fileName;
            String cmd = "/bin/mv -f " + fileName + " " + dstPath;
            // s_log.debug("Moving item: source " + fileName + ", destination " + dstPath);
            try {
                executeCommand(cmd);
            } catch ( PublishToFileException ex ) {
                s_log.warn("Could not move file on File System, message: " + ex.getMessage() + ", item = " + item);
            }
        }
        query.close();
    }

    /**
     * Updates PUBLISH_TO_FS_FILES after moving items, updates moved file names
     */
    private static void updateMovedItems(ContentItem item, 
                                         Folder srcFolder, 
                                         Folder dstFolder,
                                         Host host) {
        Assert.assertTrue(ContentItem.LIVE.equals(item.getVersion()) &&
                ContentItem.LIVE.equals(srcFolder.getVersion()) &&
                ContentItem.LIVE.equals(dstFolder.getVersion()) );

        String srcPath= Utilities.getItemLocation(srcFolder);
        String dstPath= Utilities.getItemLocation(dstFolder);

        if ( item instanceof Folder ) {
            String itemPath = Utilities.getItemLocation(item);
            DataOperation operation = SessionManager.getSession().
                    retrieveDataOperation("com.arsdigita.cms.publishToFile.moveFolder");
            operation.setParameter("newPrefix", dstPath);
            operation.setParameter("oldPrefix", srcPath);
            operation.setParameter("itemPath", itemPath);
            operation.setParameter("hostId", host.getID());
            operation.execute();
            operation.close();
        } else if ( item instanceof ContentBundle ) {
            ContentBundle bundle = (ContentBundle)item;
            ItemCollection ic = bundle.getInstances();
            while ( ic.next() ) {
                ContentItem moved = ic.getContentItem();
                updateItem(moved.getID(), srcPath, dstPath, dstFolder.getID(), host);
            }
            ic.close();

        } else {
            updateItem(item.getID(), srcPath, dstPath, dstFolder.getID(), host);
        }
    }

    /**
     * Updates items in database
     */
    private static void updateItem(BigDecimal itemId,
                                   String srcFolder,
                                   String dstFolder,
                                   BigDecimal newParentId,
                                   Host host) {
        DataOperation operation = SessionManager.getSession().
                retrieveDataOperation("com.arsdigita.cms.publishToFile.moveItem");

        operation.setParameter("newPrefix", dstFolder);
        operation.setParameter("oldPrefix", srcFolder);
        operation.setParameter("itemId", itemId);
        operation.setParameter("newParentId", newParentId);
        operation.setParameter("hostId", host.getID());
        operation.execute();
        operation.close();
    }


    /**
     * Validates folder location. Returns full path of folder on FS
     *
     * @param 'folder'  - the folder, which must be validated
     * @param 'makeNew' - if true and folder doesn't exist on FS, then it creates and returns full path,
     *                    Otherwise it returns empty string
     *
     * @return validated folder path if successed, othervise returns empty string
     */
    private static String validateLocation(Folder folder, boolean makeNew) {
        Assert.assertNotNull(folder);
        String fullPath = getItemFullPath(folder);
        try {
            File file = new File(fullPath);
            if ( !file.exists() && makeNew )
                return( file.mkdirs() ? fullPath : "");

            return( file.exists() ? fullPath : "" );

        } catch ( SecurityException ex ) {
            s_log.warn("Security Exception raised ....."); // do nothing
        }
        return "";
    }

    /**
     * Removes item(s) from File System
     * if 'item' is a Folder object, whole folder is removed
     * if 'item' is a ContentBundle object, all langusge instances related with ContentBundle
     * if 'item' is a language instance, particular file is removed
     *
     * @param item    The ContentItem, which must be removed from File System
     *
     * @return  true is opeation finished successfuly
     */
    public static void removeItems(Folder item) {
        if ( item == null ) return;

        final Host host = Host.retrieve(Web.getConfig().getHost());

        String path = item.getPath();
        // for security reasons do not allow to run any command with empty string!!!
        if (path == null || path.equals(""))  return;

        ContentSection section = item.getContentSection();
        Assert.assertNotNull(section);
        path = PublishToFile.stripSourceBase(section.getURL() + path);
        Assert.assertTrue(path != null && path.length() > 0);
        path = '/' + path;

        DataOperation operation = SessionManager.getSession().
                retrieveDataOperation("com.arsdigita.cms.publishToFile.deleteFiles");
        operation.setParameter("prefix", path);
        operation.setParameter("hostId", host.getID());
        operation.execute();
        operation.close();
        
        // XXX remove hardcoded content item type.
        String cmd = "/bin/rm -rf " + PublishToFile.getDestination("com.arsdigita.cms.ContentItem").getFile().getAbsolutePath() + path;
        try {
            executeCommand(cmd);
        } catch ( PublishToFileException ex ) {
            s_log.warn("Could not delete folder on File System, message: " + ex.getMessage());
        }
    }

    /**
     * Returns full path of an item on FS
     *
     * @param 'item'  - ContentItem which path must be returned
     *
     * @return Full path of an items on FS
     */
    public static String getItemFullPath(ContentItem item) {
        Assert.assertNotNull(item);
        File root = PublishToFile.getDestination(item.getSpecificObjectType()).getFile();
        String location = getItemLocation(item);

        Assert.assertNotNull(location);
        Assert.assertTrue(location.startsWith("/"));

        return new File(root, location).getAbsolutePath();
    }


    /**
     * Excecutes System Command
     */
    public static void executeCommand(String cmd) throws PublishToFileException {
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        } catch ( InterruptedException e ) {
            throw new PublishToFileException("interrupted when doing '" + cmd + "' : " + e.getMessage());
        } catch ( IOException e ) {
            throw new PublishToFileException("Error executing '" + cmd + "' : " + e.getMessage());
        }
        if ( p.exitValue() != 0 ) {
            throw new PublishToFileException("Exit value was " + p.exitValue() + " when executing '"
                    + cmd + "', should be 0");
        }
    }


    /***
     * Retrieve the content_item associated with the itemID.  Throw an error if
     * not found.
     * @param itemID ID of content item.
     * @return The corresponding content item.
     ***/
    public static ContentItem getContentItem(BigDecimal itemID) {
        Assert.assertNotNull(itemID, "You passed null as Item ID");
        ContentItem item;
        OID oid = new OID(ContentItem.BASE_DATA_OBJECT_TYPE, itemID);
        try {
            //item = (ContentItem)ACSObjectFactory.getWritableObject(itemID);
            item = (ContentItem)DomainObjectFactory.newInstance(oid);
        } catch ( DataObjectNotFoundException e ) {
            throw new IllegalArgumentException("Item " + itemID + " does not exist.");
        }
        return item;
    }

    /***
     * Retrieve the content_item associated with the itemID.  Return null if not found.
     * @param itemID ID of content item.
     * @return The corresponding content item.
     ***/
    public static ContentItem getContentItemOrNull(BigDecimal itemID) {
        ContentItem item = null;
        OID oid = new OID(ContentItem.BASE_DATA_OBJECT_TYPE, itemID);
        if ( itemID != null ) {
            try {
                //item = (ContentItem)ACSObjectFactory.getWritableObject(itemID);
                item = (ContentItem)DomainObjectFactory.newInstance(oid);
            } catch ( DataObjectNotFoundException e ) {
                return  null;
            }
        }
        return item;
    }

    /**
     * This function returns the globally unique path for the asset.
     */
    public static String getAssetPath(Asset asset) {
        return getAssetPath(asset, null);
    }

    /**
     * Returns asset's path located in folder 'folder'.
     *
     * @param 'asset' - asset object
     * @param 'folder' - folder where located asset
     */
    public static String getAssetPath(Asset asset, Folder folder) {
        if (folder != null) { 
            return Utilities.getItemLocation(folder); 
        }

        MimeType mime = asset.getMimeType();
        if ( mime == null ) {
            throw new IllegalArgumentException(
                    "MIME Type for asset " + asset + " is null.");
        }
        String path = "/" + asset.getName() + "_" + asset.getID()
            + "." + mime.getFileExtension();
        s_log.debug("AssetPath is " + path);

        return path;
    }

    /***
     * Strip any ".jsp" extension from a string.
     * @param s input string.
     * @return the string with any '.jsp' extension removed.
     ***/
    public static String stripJSP(String s) {
        final String JSP = ".jsp";
        if ( s.endsWith(JSP) )
            s = s.substring(0, s.length() - JSP.length());
        return s;
    }

    /***
     * get live version of a folder, creating one if necessary.
     * @param target folder to get live version for.
     * @return live version of target folder.
     ***/
    public static Folder getLiveTarget(Folder target) {
        ContentItem live =  ContentItem.LIVE.equals(target.getVersion()) ?
                target.getLiveVersion() : null;
        if ( live == null ) {
            //target.publish();
            LifecycleDefinition def =
                    ContentTypeLifecycleDefinition.getLifecycleDefinition(
                            target.getContentSection(),
                            target.getContentType());
            target.publish(def,null);
            live = target.getLiveVersion();
        }
        return(Folder)live;
    }

    /**
     * Applies Content Section to an ContentPage, ContentBundle and Folder, if their ContentSections are different.
     * The process will continue till new ContentSection will not be met.
     *
     * @param item - ContentItem
     * @param newSection - new ContentSection
     */
    // FIXME: This should be done in the MoveItemCommand [lutter]
    public static void updateContentSection(ContentItem item, ContentSection newSection) {
        Assert.assertNotNull(item);
        Assert.assertNotNull(newSection);

        ContentSection oldSection = item.getContentSection();

        // if item has the same ContentSection, there is nothing to do.
        if ( oldSection.getID().equals(newSection.getID()) )
            return;
        else if ( item instanceof Folder )
            Utilities.updateFolderSection((Folder) item, oldSection, newSection);
        else if ( item instanceof ContentBundle )
            Utilities.updateBundleSection( (ContentBundle) item, newSection);
        else
            Utilities.updateItemSection(item, newSection);
    }

    private static void updateFolderSection(Folder folder, ContentSection oldSection, ContentSection newSection) {
        ItemCollection ic = folder.getItems();

        while ( ic.next() ) {
            ContentItem item = ic.getContentItem();

            if ( item instanceof Folder) {
                // if we are within the same ContentSection, do updating, otherwise do nothing
                if ( oldSection.getID().equals(item.getContentSection().getID()) )
                    Utilities.updateFolderSection((Folder) item, oldSection, newSection);

            } else if ( item instanceof ContentBundle ) {
                Utilities.updateBundleSection((ContentBundle) item, newSection);

            } else {
                Utilities.updateItemSection(item, newSection);
            }
        }
        // update item itself
        Utilities.updateItemSection(folder, newSection);
    }

    private static void updateBundleSection(ContentBundle bundle, ContentSection newSection) {
        ItemCollection ic = bundle.getInstances();
        while ( ic.next() )
            Utilities.updateItemSection(ic.getContentItem(), newSection);

        Utilities.updateItemSection(bundle, newSection);
    }

    private static void updateItemSection(ContentItem item, ContentSection newSection) {
        item.setContentSection(newSection);
        item.save();
    }
}
