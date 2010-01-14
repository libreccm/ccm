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
package  com.arsdigita.cms.publishToFile;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Template;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.runtime.CCMResourceManager;

import org.apache.log4j.Logger;


import java.io.*;
// import java.io.File;

import java.util.List;
import java.util.Iterator;

/**
 * Initializes the publish-to-file service. The configuration is described in
 * the {@link com.arsdigita.cms.publishToFile} page.
 *
 * (pboy) ToDo: Adjusting the initialisation to the new configuration method
 * without enterprise.init file.
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @version $Revision: #24 $ $Date: 2004/08/17 $
 */
public class LegacyInitializer implements com.arsdigita.initializer.Initializer {

    private static Logger s_log = Logger.getLogger(LegacyInitializer.class);

    private final static String PUBLISH_DESTINATIONS = "destination";
    private final static String PUBLISH_TO_FILE_LISTENER = "publishListener";
    private final static String QUEUE_POLL_STARTUP_DELAY = "startupDelay";
    private final static String QUEUE_POLL_DELAY = "pollDelay";
    private final static String RETRY_DELAY = "retryDelay";
    private final static String REQUEST_TIMEOUT = "requestTimeout";
    private final static String BLOCK_SIZE = "blockSize";
    private final static String BLOCK_SELECT_METHOD = "blockSelectMethod";
    private final static String MAXIMUM_FAIL_COUNT = "maximumFailCount";

    private Configuration m_conf = new Configuration();

    public LegacyInitializer() throws InitializationException {
        m_conf.initParameter(
            PUBLISH_DESTINATIONS,
            "List of publish destinations for content types" +
            "Each element is a four-element list in the format " +
            "'{ \"content type\", \"root directory\", \"shared storage\", " +
            "\"url stub\" }'. " +
            "Content type is the object type of the content type." +
            "Root directory must be a path to a writable directory, relative" +
            "to the webapp root. Shared storage must be true if the root " +
            "directory is shared NFS storage, false otherwise. URL stub is " +
            "must be the path component of the URL from which the live server " +
            "will serve from this directory.",
            List.class);
        m_conf.initParameter(
            PUBLISH_TO_FILE_LISTENER,
            "Class which implements PublishToFileListener used to " +
            "perform additional actions when publishing or unpublishing " +
            "to the file system.",
            String.class);
        m_conf.initParameter(
            QUEUE_POLL_STARTUP_DELAY,
            "Time (in seconds) after system startup to wait before " +
            "starting to monitor publishToFile queue.  A value < 0 " +
            "disables processing of the queue on this server.",
            Integer.class, new Integer(30));
        m_conf.initParameter(
            QUEUE_POLL_DELAY,
            "Time (in seconds) between checking if there are entries " +
            "in the publishToFile queue. A value <= 0 disables processing " +
            "the queue on this server.",
            Integer.class, new Integer(1));
        m_conf.initParameter(
            RETRY_DELAY,
            "Time to wait (seconds) before retrying to process a " +
            "failed entry.",
            Integer.class, new Integer(120));
        m_conf.initParameter(
            REQUEST_TIMEOUT,
            "Time to wait (seconds) before aborting item request.",
            Integer.class, new Integer(PublishToFile.DEFAULT_TIMEOUT));
        m_conf.initParameter(
            BLOCK_SIZE, "number of queue entries to process in one txn."
            , Integer.class, new Integer(20));
        m_conf.initParameter(
            BLOCK_SELECT_METHOD,
            "Method used to select entries for processing.  " +
            "'QueuedOrder'-in  queued order. 'GroupByParent'-group " +
            "entries according to parent when selecting items " +
            "(allows optimizations if a listener task required for " +
            "all elements in a folder can be done only once for the " +
            "folder).",
            String.class, "QueuedOrder");
        m_conf.initParameter(
            MAXIMUM_FAIL_COUNT,
            "Maximum Fail Count for actions in Queue Manager. " +
            "If Fail Count in Database is more than Specified " +
            "Limit, Queue Manager will ignore the action. The " +
            "Default Value -1 will ignore this parameter.",
            Integer.class, new Integer(-1));
    }

    public Configuration getConfiguration() {
        return m_conf;
    }


    public void startup() {
        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();
        txn.beginTxn();
        setupPublishToFileSystem();

        QueueManager.requeueMissingFiles();

        txn.commitTxn();
    }

    public void shutdown() {
        QueueManager.stopWatchingQueue();
    }


    /**
     * initialize source and destinations for publishing to the file system
     */
    private void setupPublishToFileSystem() throws InitializationException {
        DomainObjectInstantiator inst = new DomainObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dobj) {
                    return new PublishedFile(dobj);
                }
            };
        DomainObjectFactory.registerInstantiator(
            PublishedFile.BASE_DATA_OBJECT_TYPE,
            inst);


        processDestination((List) m_conf.getParameter(PUBLISH_DESTINATIONS));

        PublishToFile.setRequestTimeout(getInteger(REQUEST_TIMEOUT).intValue());

        QueueManager.setRetryDelay(getInteger(RETRY_DELAY));
        QueueManager.setBlockSize(getInteger(BLOCK_SIZE));
        QueueManager.setBlockSelectMethod(getString(BLOCK_SELECT_METHOD));
        QueueManager.setMaximumFailCount(getInteger(MAXIMUM_FAIL_COUNT));


        // setup listener if specified
        String listenerName = getString( PUBLISH_TO_FILE_LISTENER );
        if (listenerName != null) {
            PublishToFileListener listener = null;
            Class listenerClass = null;
            try {
                listenerClass = Class.forName(listenerName);
            } catch (ClassNotFoundException ex) {
                invalidParam(PUBLISH_TO_FILE_LISTENER,
                             "could not find listener class " + listenerClass);
            }

            try {
                listener = (PublishToFileListener)listenerClass.newInstance();
            } catch (InstantiationException ex) {
                invalidParam(PUBLISH_TO_FILE_LISTENER,
                             "could not find instantiate listener class " +
                             listenerClass + " (" + ex.getMessage() + ")");
            } catch (IllegalAccessException ex) {
                invalidParam(PUBLISH_TO_FILE_LISTENER,
                             "could not find instantiate listener class " +
                             listenerClass + " (" + ex.getMessage() + ")");
            }
            QueueManager.setListener(listener);
        }

        // start thread for monitoring queue
        int startupDelay = getInteger(QUEUE_POLL_STARTUP_DELAY).intValue();
        int pollDelay = getInteger(QUEUE_POLL_DELAY).intValue();
        QueueManager.startWatchingQueue(startupDelay, pollDelay);
    }

    //
    // Process publishing destinations
    //

    private static void processDestination(List dest)
        throws InitializationException {
        if (dest == null) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "publish destinations must not be null");
        }
        if (dest.size() < 1) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "publish destinations must contain at " +
                         "least one entry");
        }

        Iterator entries = dest.iterator();
        while (entries.hasNext()) {
            processDestinationEntry((List)entries.next());
        }
    }

    private static void processDestinationEntry(List entry)
        throws InitializationException {

        if ( entry.size() != 4 ) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "publish destinations entry must contain " +
                         "four elements: '{ \n" +
                         "  \"content type\",\n" +
                         "  \"root directory\", \n" +
                         "  \"is shared\", \n" +
                         "  \"url stub\" \n" +
                         "};\n");
        }

        String contentType = (String)entry.get(0);
        // destRoot is here relative to webapp root!
        String destRoot = (String) entry.get(1);
        Boolean sharedRoot = (Boolean) entry.get(2);
        String destURL = (String) entry.get(3);

        if ( contentType == null || contentType.trim().length() == 0) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination content type must not be null");
        }

        ObjectType type = MetadataRoot.getMetadataRoot().getObjectType(contentType);
        if (type == null) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination content type cannot be found");
        }

        if (destRoot == null || destRoot.trim().length() == 0) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination root must not be null");
        }
        if (destRoot.endsWith("/")) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination root '" + destRoot +
                         "' must not end with a '/'");
        }

        // Does destRoot really now turns into an absolute fully pathname
        destRoot = new File(CCMResourceManager.getBaseDirectory().getPath(),
                            destRoot).getPath();
        s_log.info("Destination Root is set to : " + destRoot);



        if (sharedRoot == null) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination shared flag must not be null");
        }


        if (destURL == null || "".equals(destURL.trim())) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination URL must not be null");
        }
        if (!destURL.startsWith("/")) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination URL '" + destURL +
                         "' must start with a '/'");
        }
        if (destURL.endsWith("/")) {
            invalidParam(PUBLISH_DESTINATIONS,
                         "the destination URL '" + destURL +
                         "' must not end with a '/'");
        }

        DestinationStub dest = new DestinationStub(destRoot,
                                                   sharedRoot.booleanValue(),
                                                   destURL);

        File file = dest.getFile();
        if (!file.exists()) {
            file.mkdirs();
            s_log.info(file.getPath() + " created");
        }
        boolean writable = false;
        FileWriter fl;
        File fname = new File(file.getPath(),"placeholder.txt");
        s_log.info("Try to create : " + destRoot);
        try {
            writable = file.canWrite() && file.isDirectory();
            try {
                fl = new FileWriter(fname.getPath());
                fl.write("Location for the p2fs module to store static content. \n");
                fl.close();
            } catch ( IOException e ) {
                // Will be reported as an initalization error
                s_log.warn("Error creating file " + fname.getPath());
            }

 
        } catch ( SecurityException ex ) {
            // Will be reported as an initalization error
        }
        if ( ! writable ) {
            // HACK: Let's see if we can write to the config directory.  If we can,
            // then we're running as ccmadmin inside of ccm load, and there is no
            // need to thrown an exception.
            File conf = CCMResourceManager.getConfigDirectory();
            if (conf.isDirectory() && conf.canWrite()) {
                // we're ok
            } else {
                invalidParam(PUBLISH_DESTINATIONS,  " the document root '" + file
                             +"' must be a writable directory");
            }
        }

        if (Template.BASE_DATA_OBJECT_TYPE.equals(contentType) ||
                !ContentSection.getConfig().getDisableItemPfs()) {
            PublishToFile.addDestination(contentType,
                                         dest);
        }
    }

    private Integer getInteger(String paramName) {
        return (Integer) m_conf.getParameter(paramName);
    }

    private String getString(String paramName) {
        return (String) m_conf.getParameter(paramName);
    }

    private static void invalidParam(String param, String msg)
        throws InitializationException {

        throw new InitializationException(
            "publishToFile: parameter " + param +": " + msg);
    }
}
