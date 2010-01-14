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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Host;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashSet;
import java.io.File;


/**
 * Class for queuing tasks for publishing and unpublishing to
 * the file system and for processing the queue.  When processing
 * queued tasks, this class insures that the transaction that
 * created the queue entry is committed before the task is processed.
 * This is mainly for publishing tasks because a http request
 * is used to get the content to publish and the content may not
 * be available until the transaction which makes the content live
 * is committed.  The class saves the information for the page to
 * be published using the QueueEntry object (publish_to_fs_queue
 * table).
 *
 * This class locks the Lifecycle thread when it starts processing
 * queue entries.This is done in order to stop the lifecycle thread
 * from modifying the item's date when items are being processed for
 * publication.
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @author <a href="mailto:sshinde@redhat.com">Shashin Shinde</a>
 * @author <a href="mailto:dlutter@redhat.com">David Lutterkort</a>
 *
 * @version $Revision: #24 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class QueueManager implements Runnable {

    // Creates a s_logging category with name = to the full name of class
    private static Logger s_log = Logger.getLogger(QueueManager.class);

    // Should probably use these constants.  Are hardcoded for now because
    // matches DataQuery suffex.
    // final static String BLOCK_SELECT_METHOD_QUEUED_ORDER    = "QueuedOrder";
    // final static String BLOCK_SELECT_METHOD_GROUP_BY_PARENT = "GroupByParent";

    // Parameters involved in processing the queue and their default values.
    // Set to values other than default by calling methods from an initializer.
    private int m_startupDelay;
    private int m_pollDelay;

    static Integer s_retryDelay = new Integer(120);
    static Integer s_blockSize = new Integer(20);
    static String s_blockSelectMethod = "QueuedOrder";
    static Integer s_maximumFailCount = new Integer(-1);

    // Following true if should keep watching queue

    static private boolean s_keepWatchingQueue =  true;
    static private Thread s_queueThread = null;

    // Class implementing methods run when publishing or unpublishing to file.
    private static PublishToFileListener s_publishListener = null;

    ////////////////////////////////////////////////////////////////////
    // Constructor related code.
    //

    /**
     * 
     * @param startupDelay
     * @param pollDelay
     */
    private QueueManager(int startupDelay, int pollDelay) {
        m_startupDelay = startupDelay;
        m_pollDelay = pollDelay;
    }

    ////////////////////////////////////////////////////////////////////
    // Initialization related code.
    //

    /**
     * Set how many seconds the queue manager should wait before trying to
     * process an entry which has previously failed.
     *
     * @param delay number of seconds between reprocessing of failed
     * entries.
     */
    public static void setRetryDelay(Integer delay) {
        s_retryDelay = delay;
    }

    /**
     * The number of queue entries that should be processed in one block
     * (database transaction).
     *
     * @param size number of queue enries to be procesed in one block
     */
    public static void setBlockSize(Integer size) {
        s_blockSize = size;
    }

    // Package protected since there is only one sensible value
    // ("GroupByParent") right now. [lutter]
    static void setBlockSelectMethod(String method) {
        s_blockSelectMethod = method;
    }

    /**
     * Set how many times processing a queue entry should be attempted at the
     * most. Queue entries that have not been processed succesfully after
     * <code>maxFailCount</code> attempts are ignored.
     * @param maxFailCount maximum fail count in publishing thread
     */
    public static void setMaximumFailCount(Integer maxFailCount) {
        Assert.exists(maxFailCount, Integer.class);
        s_maximumFailCount = maxFailCount;
    }


    /***
     * Set the listener that processes the publish and unpublish requests.
     */
    public static void setListener(PublishToFileListener l) {
        s_publishListener = l;
    }

    ////////////////////////////////////////////////////////////////////
    // Methods for queuing tasks
    //

    public static void requeueMissingFiles() {
        DomainCollection files = PublishedFile.retrieveAll();

        // Micro-optimization, since the same file name may be
        // present multiple times (once per host), we cache
        // list of files processed, keyed on filename
        HashSet done = new HashSet();

        while (files.next()) {
            PublishedFile fileRecord = (PublishedFile)files.getDomainObject();

            if (done.contains(fileRecord.getFileName())) {
                continue;
            }
            done.add(fileRecord.getFileName());

            File file = fileRecord.getFile();

            if (file == null) {
                // fix when turning off p2fs for items
                s_log.debug("Ignoring null file.");

            } else if (file.exists()) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug( "File " + file.getAbsolutePath() +
                                 " already exists");
                }

            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Published file " + file.getAbsolutePath() +
                               " for item " + fileRecord.getItemId() +
                               " isn't on the filesystem. Scheduling for " +
                               "republishing.");
                }

                ContentItem item = fileRecord.getItem();

                if (item == null) {
                    s_log.warn
                        ("No corresponding content item found for " +
                         "published file " + fileRecord.getFileName() + " " +
                         "(draft id " + fileRecord.getDraftId() + ", " +
                         "item id " + fileRecord.getItemId() + ")");
                } else {
                    ACSObject parent = item.getParent();
                    BigDecimal parentID = null;
                    if (null != parent) {
                        parentID = parent.getID();
                    }
                    queue(item, parentID, QueueEntry.TASK_REPUBLISH,
                          null, Web.getConfig().getCurrentHost());
                }
            }
        }
    }


    /***
     * Schedule an item for publishing. This should be called just after a new
     * live version of an item becomes available.
     *
     * @param item  item to be published to file system.
     * @pre item.isLiveVersion()
     * @pre ! item instanceof ContainerItem
     ***/
    public static void queuePublish(ContentItem item) {
        Assert.isTrue( !(item instanceof ContentBundle),
                "Cannot queue content bundle " + item );
        Assert.isTrue(item.isLiveVersion(), "Item is not live");
        if (s_log.isInfoEnabled()) {
            s_log.info("Queue publish task for " + item.getID());
        }

        String task = QueueEntry.TASK_PUBLISH;
        QueueEntryCollection q = new QueueEntryCollection(
            item.getID());
        while (q.next()) {
            if ( q.isPublishTask() ) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Deleting existing publish task " + q.getID());
                }
                q.delete();
            } else if ( q.isUnpublishTask() || q.isRepublishTask() ) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Deleting existing (un|re)publish task " +
                                q.getID());
                }
                task = QueueEntry.TASK_REPUBLISH;
                q.delete();
            }
        }
        queue(item, task);
    }


    /***
     * Schedule an item for unpublishing. This should be called just before
     * the live version <code>item</code> is deleted.
     *
     * @param item  item to be unpublished from file system.
     * @pre item.isLiveVersion()
     * @pre ! item instanceof ContainerItem
     ***/
    public static void queueUnpublish(ContentItem item) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Queue unpublish task for " + item.getID());
        }

        QueueEntryCollection q = new QueueEntryCollection(
            item.getID());
        while (q.next()) {
            if ( q.isPublishTask()
                    || q.isRepublishTask()
                    || q.isUnpublishTask() ) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Deleting existing (un|re|new)publish task "
                                + q.getID());
                }
                q.delete();
            }
        }
        queue(item, QueueEntry.TASK_UNPUBLISH);
    }


    /***
     * Schedule an item for republishing. This should be called whenever an
     * existing live item <code>item</code> should be refreshed in the file
     * system.
     *
     * @param item Item to be republished (unpublished then published) on the
     * file system.
     * @pre item.isLiveVersion()
     * @pre ! item instanceof ContainerItem
     ***/
    public static void queueRepublish(ContentItem item) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Queue republish task for " + item.getID());
        }

        QueueEntryCollection q = new QueueEntryCollection(
            item.getID());
        while (q.next()) {
            if ( q.isUnpublishTask()
                    || q.isPublishTask() ) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Deleting existing (un|new)publish task "
                                + q.getID());
                }
                q.delete();
            } else if ( q.isRepublishTask() ) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Aborting because there is already a republish "
                                + q.getID());
                }
                // There is already a republish task, don't do anything
                q.close();
                return;
            }
        }
        queue(item, QueueEntry.TASK_REPUBLISH);
    }

    /**
     * Schedule the moving of an item in the file system.
     *
     * @param liveItem the item to be moved
     * @param source the folder from which the item is moved
     * @param destination the folder to which the item is moved
     */
    // FIXME: THis whole move business within PFS is highly suspicious and
    // should probably be taken out of PFS [lutter]
    public static void queueMoveTask(ContentItem liveItem,
                                     Folder source,
                                     Folder destination) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Queue move task for " + liveItem.getID());
        }

        Assert.isTrue(liveItem != null && liveItem.isLiveVersion(),
                     "Item is not live");
        Assert.isTrue(source != null && source.isLiveVersion(),
                     "Source is not live");
        Assert.isTrue(destination != null && destination.isLiveVersion(),
                     "Destination is not live");

        // for move put itemId as destination folder ID, for parent_id source
        // folder ID we do not need any other information for move
        queue(liveItem,
              source.getID(),
              QueueEntry.TASK_MOVE,
              destination.getID().toString());
    }

    private static void queue(ContentItem item, String task) {
        Assert.isTrue( !(item instanceof ContentBundle),
                "Cannot queue content bundle " + item );
        Assert.isTrue(item.isLiveVersion(), "Item is not live");

        ACSObject parent = item.getParent();
        BigDecimal parentID = null;
        if (null != parent) {
            parentID = parent.getID();
        }
        queue(item, parentID, task, null);
    }

    /***
     * Create a new queue entry and set its fields.
     ***/
    private static void queue(ContentItem item,
                              BigDecimal parentId,
                              String task,
                              String destination) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Queue " + task + " for " + item.getOID() + " on all hosts");
        }

        DestinationStub dest = PublishToFile.getDestination(
            item.getSpecificObjectType()
        );
        // No destination configured for this object type, lets
        // get outta here, since they obviously don't want to
        // p2fs it....
        if (dest == null) {
            return;
        }

        DomainCollection hosts = Host.retrieveAll();
        while ( hosts.next() ) {
            Host host = (Host) hosts.getDomainObject();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Queue on " + host);
            }

            QueueEntry q = QueueEntry.create(item,
                                             parentId,
                                             task,
                                             host,
                                             destination);
            q.save();
        }
    }

    private static void queue(ContentItem item,
                              BigDecimal parentId,
                              String task,
                              String destination,
                              Host host) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Queue item " + item + " for " +
                        task + " on " + host);
        }

        QueueEntry q = QueueEntry.create(item,
                                         parentId,
                                         task,
                                         host,
                                         destination);
        q.save();
    }

    ////////////////////////////////////////////////////////////////////
    // Routines from here down involved with processing the queue.
    //


    /**
     * Start watching and processing the queue. This method spawns a
     * background thread that processes the queue. Queue processing starts
     * after <code>startupDelay</code> seconds. The queue is checked for new
     * entries every <code>pollDelay</code> seconds.
     *
     * @param startupDelay number of seconds to wait before starting to
     * process the queue
     * @param pollDelay number of seconds to wait between checks if the queue
     * has any entries.
     */
    public static void startWatchingQueue(int startupDelay, int pollDelay) {
        if ( startupDelay > 0 ) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Going to start queue processing.");
            }
            s_queueThread = new Thread( new QueueManager(startupDelay, pollDelay) );
            s_queueThread.setDaemon(true);
            s_queueThread.setName("cms-p2fs-queue");
            s_queueThread.start();
            s_keepWatchingQueue = true;
        }
    }

    /**
     * Stop watching and processing the queue. The background thread that
     * processes the queue will terminate after this method has been
     * called. Termination is not immediate, since the queue may be in the
     * middle of processing a block of entries.
     */
    public static void stopWatchingQueue() {
        if (s_log.isInfoEnabled()) {
            s_log.info("Sending signal to stop queue processing.");
        }
        s_keepWatchingQueue = false;

        s_log.debug("Going to sleep.");
        sleep(45);
        s_log.debug("Resume processing.");
    }



    /***
     * Watch queue for entries to process.  The main routine that starts
     * queue processing.
     ***/
    public void run() {
        s_log.info("Start polling queue in " + m_startupDelay + "s.");
        sleep(m_startupDelay);
        s_log.info("Polling queue every " + m_pollDelay + "s.");

        HashSet failedItems = new HashSet();

        while ( sleep(m_pollDelay) && s_keepWatchingQueue ) {
//          synchronized( Scheduler.class ) {
            //while there are more entries in queue process them.HashSet
            //is used to store the failed items and for checking that
            //they do not get processed again.
            while ( processQueueItems(failedItems) )
                ;

            // clear failed items
            failedItems.clear();
//          }
        }
        s_log.info("Start polling queue in " + m_startupDelay + "s.");
    }

    /***
     * Sleep for n seconds
     ***/
    private static boolean sleep(long n) {
        try {
            Thread.sleep(n * 1000);
            return true;
        } catch ( InterruptedException e ) {
            s_log.error( "Waiting was interrupted.");
            return false;
        }
    }

    /***
     * Process queued items.
     * First Flags the block of items to be processed.
     * @return true if items processed and there are no valid uprocessed items
     * in the queue, and false otherwise.
     **/
    private static boolean processQueueItems(HashSet failedItems) {
        Session ssn = SessionManager.getSession();
        TransactionContext txn = ssn.getTransactionContext();
        boolean hasMore = false;
        DataQuery query = null;

        try {
            txn.beginTxn();
            hasMore = processQueueItemsInternal(query, failedItems);
            txn.commitTxn();
        } catch (Exception e) {
            s_log.warn("Ignoring uncaught exception", e);
        } finally {
            try {
                if ( query != null ) {
                    query.close();
                }

                if ( txn.inTxn() ) {
                    txn.abortTxn();
                    s_log.info("Aborting transaction");
                }
            } catch (Exception e) {
                s_log.warn("Txn cleanup failed", e);
            } finally {
                query = null;
            }
        }

        // Tell the caller if there are more items to process.
        return hasMore;
    }

    /**
     * This method exists so that we can test p2fs without the
     * transaction management code.
     */
    private static boolean processQueueItemsInternal(DataQuery query, HashSet failedItems) {
        Host host = Web.getConfig().getCurrentHost();
        boolean hasMore = false;

        query = getBlockQuery();
        query.setParameter("hostId", host.getID());
        query.setParameter("queueEntryRetryDelay", s_retryDelay);
        query.setParameter("maximumFailCount", s_maximumFailCount);

        int entryCount = 0;
        while ( query.next() && entryCount < s_blockSize.intValue() ) {
            DataObject dobj = (DataObject) query.get("queueEntry");
            QueueEntry qe = new QueueEntry(dobj);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Processing queue entry " + qe);
            }

            BigDecimal itemId = qe.getItemId();
            if ( !failedItems.contains(itemId) ) {
                try {
                    if (entryCount == 0) {
                        // Tell the publish listener that we are about
                        // to execute the first task in this
                        // transaction.
                        s_publishListener.transactionStart();
                    }

                    // Call the primary publishToFileClass.
                    // This will normally be calling cms.publishToFile.PublishToFile
                    s_publishListener.doTask(qe);

                    qe.delete();   // successfully processed item, delete from queue
                } catch ( PublishToFileException e ) {
                    flagError(itemId, "PublishToFileException.", e, qe, failedItems);
                } catch ( Exception e ) {
                    flagError(itemId, "Task " + qe + " failed:", e, qe, failedItems);
                }
                entryCount++;
            }
        }

        if ( entryCount > 0 )
            s_publishListener.transactionEnd();

        hasMore = !query.isAfterLast();

        return hasMore;

    }


    /**
     * Get items to process
     * @return Query for fetching block to process.
     */
    static DataQuery getBlockQuery() {
        DataQuery query = SessionManager.getSession()
            .retrieveQuery("com.arsdigita.cms.publishToFile.getBlock");

        if ("GroupByParent".equals(s_blockSelectMethod))
            query.addOrder("queueEntry.parentId, queueEntry.sortOrder");
        else
            query.addOrder("queueEntry.sortOrder");

        return query;
    }

    private static void flagError(BigDecimal itemId, String exName, Exception e,
                                  QueueEntry qe, HashSet failedItems) {
        // Flag that queue entry failed
        failedItems.add(itemId);
        Long failCount = qe.getFailCount();
        s_log.error( exName + "  itemId=" + itemId +
                     " task=" + qe +
                     " destination=" + qe.getDestination() + " failCount=" + failCount +
                     " error=" + e.getMessage(), e);
        DataOperation operation = SessionManager.getSession().
            retrieveDataOperation(
                "com.arsdigita.cms.publishToFile.flagPublishFailed"
            );
        operation.setParameter("id", qe.getID());
        operation.execute();
    }

}
