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
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Host;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

/**
 * One entry in the PFS queue. The methods {@link #isPublishTask}, {@link
 * #isUnpublishTask}, {@link #isRepublishTask}, and {@link #isMoveTask} can
 * be used to further determine what exact task should be performed for
 * this entry. Exactly one of these methods will return <code>true</code>.
 *
 * @author <a href="mailto:dlutter@redhat.com">David Lutterkort</a>
 * @version 1.0
 **/
public class QueueEntry extends DomainObject {

    private static final String SEQUENCE_NAME = "publish_to_file_system_seq";

    /**
     * The name of the PDL object type for queue entries.
     */
    public static final String BASE_DATA_OBJECT_TYPE = 
      "com.arsdigita.cms.publishToFile.QueueEntry";

    public final static String ID = "id";
    public final static String ITEM_ID = "itemId";
    public final static String PARENT_ID = "parentId";
    public final static String ITEM_TYPE = "itemType";
    public final static String TASK = "task";
    public final static String HOST = "host";
    public final static String DESTINATION = "destination";
    public final static String TIME_QUEUED = "timeQueued";
    public final static String TIME_LAST_FAILED = "timeLastFailed";
    public final static String FAIL_COUNT = "failCount";
    public final static String SORT_ORDER = "sortOrder";
    public final static String IN_PROCESS = "inProcess";

    // The various tasks that can be queued
    public final static String TASK_PUBLISH   = "publish";
    public final static String TASK_UNPUBLISH = "unpublish";
    public final static String TASK_MOVE = "move";
    public final static String TASK_REPUBLISH = "republish";


    public final static String IN_PROCESS_YES = "1";
    public final static String IN_PROCESS_NO  = "0";

    /**
     * Return the PDL object type for queue entries.
     *
     * @return the PDL object type for queue entries.
     */
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    // Creates a s_logging category with name = to the full name of class
    private static Logger s_log =
        Logger.getLogger( QueueEntry.class.getName() );

    /**
     * Constructor. Creates a new DomainObject instance to encapsulate a given
     * data object.
     *
     * @param dataObject The data object to encapsulate in the new domain 
     * object.
     **/
    public QueueEntry(DataObject dataObject) {
        super(dataObject);
    }
    
    protected QueueEntry() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    protected QueueEntry(String objectType) {
        super(objectType);
    }

    public void initialize() {
        super.initialize();
        
        if (isNew()) {
            set(ID, generateID());
        }
    }

    /**
     * Creates object and initializes with data.
     *
     * @param itemId - ID of ContentItem.
     * @param parentId a <code>BigDecimal</code> value
     * @param task  task Should be QueueEntry.TASK_PUBLISH or
     * QueueEntry.TASK_UNPUBLISH.
     * @param destination location in file system (within document root). 
     *   Used only if task is publish).
     */
    public static QueueEntry create(ContentItem item, 
                                    BigDecimal parentId, 
                                    String task, 
                                    Host host, 
                                    String destination) {
        QueueEntry entry = new QueueEntry();

        entry.setItemId(item.getID());
        entry.setItemType(item.getSpecificObjectType());
        entry.setParentId(parentId);
        entry.setTask(task);
        entry.setHost(host);
        entry.setFailCount(new Long(0));
        entry.setDestination(destination);
        entry.setSortOrder(entry.getID());
        entry.setInProcess(IN_PROCESS_NO);
	entry.setTimeQueued(new Date());
        
        return entry;
    }

    /**
     * Move this entry to the end of the queue.
     */
    void requeue() {
        setSortOrder(generateID());
    }

    public BigDecimal getID() {
        return (BigDecimal)get(ID);
    }

    private void setItemId(BigDecimal itemId) {
        set(ITEM_ID, itemId);
    }

    /**
     * Return the ID of the item that is affected by this task.
     *
     * @return the ID of the item that is affected by this task.
     * @see #getItem
     */
    public BigDecimal getItemId() {
        return (BigDecimal)get(ITEM_ID);
    }

    private void setItemType(String itemType) {
        set(ITEM_TYPE, itemType);
    }

    public String getItemType() {
        return (String)get(ITEM_TYPE);
    }

    /**
     * Return a readonly copy the item affected by this queue entry or
     * <code>null</code> if the item does no longer exist.
     *
     * @return the item affected by this entry or <code>null</code>
     * @post return == null || ! return.isWritable()
     * @post ! ( return instanceof ContainerItem )
     */
    public ContentItem getItem() {
        ContentItem result = Utilities.getContentItemOrNull(getItemId());
        // FIXME: We need to exempt move tasks from the ContainerItem
        // assertion, since moves can have anything as the item [lutter]
        Assert.isTrue(TASK_MOVE.equals(getTask())
                          || ! (result instanceof ContentBundle),
                          "The item for a task must not be a ContentBundle");
        return result;
    }

    private void setParentId(BigDecimal parentId) {
        set(PARENT_ID, parentId);
    }

    /**
     * Return the ID of the parent of this item.
     *
     * @return the ID of the parent of this item.
     * @see #getParent
     */
    public BigDecimal getParentId() {
        return (BigDecimal)get(PARENT_ID);
    }

    /**
     * Return a readonly copy of the parent of the item affected by this
     * queue entry or <code>null</code> if the item does no longer exist.
     *
     * @return the parent of the item affected by this entry or
     * <code>null</code>
     * @post return == null || ! return.isWritable()
     * @post return == null || return instanceof ContainerItem
     */
    public ContentItem getParent() {
        ContentItem result = Utilities.getContentItemOrNull(getParentId());
        Assert.isTrue(result == null || (result instanceof ContentBundle) );
        return result;
    }        

    private void setDestination(String destination) {
        set(DESTINATION, destination);
    }

    /**
     * Get the destination of the task.
     *
     * @return a <code>String</code> value
     */
    public String getDestination() {
        return (String)get(DESTINATION);
    }

    private void setTask(String task) {
        set(TASK, task);
    }

    public String getTask() {
        return (String) get(TASK);
    }

    private void setHost(Host host) {
        setAssociation(HOST, host);
    }
    
    public Host getHost() {
        DataObject obj = (DataObject)get(HOST);
        return new Host(obj);
    }

    /**
     * Return <code>true</code> if this entry represents a publish task.
     *
     * @return <code>true</code> if this entry represents a publish task.
     */
    public boolean isPublishTask() {
        return QueueEntry.TASK_PUBLISH.equals(getTask());
    }
  
    /**
     * Return <code>true</code> if this entry represents an unpublish task.
     *
     * @return <code>true</code> if this entry represents an unpublish task.
     */
    public boolean isUnpublishTask() {
        return QueueEntry.TASK_UNPUBLISH.equals(getTask());
    }
  
    /**
     * Return <code>true</code> if this entry represents a republish task.
     *
     * @return <code>true</code> if this entry represents a republish task.
     */
    public boolean isRepublishTask() {
        return QueueEntry.TASK_REPUBLISH.equals(getTask());
    }
  
    /**
     * Return <code>true</code> if this entry represents a move task.
     *
     * @return <code>true</code> if this entry represents a move task.
     */
    public boolean isMoveTask() {
        return QueueEntry.TASK_MOVE.equals(getTask());
    }

    private void setTimeQueued(Date timeQueued) {
        set(TIME_QUEUED, timeQueued);
    }

    /**
     * Return when this entry was added to the queue.
     */
    public Date getTimeQueued() {
        return (Date) get(TIME_QUEUED);
    }

    private void setTimeLastFailed(Date timeLastFailed) {
        set(TIME_LAST_FAILED, timeLastFailed);
    }

    /**
     * Return the time this entry was last processed unsuccessfully.
     */
    public Date getTimeLastFailed() {
        return (Date) get(TIME_LAST_FAILED);
    }

    private void setFailCount(Long count) {
        set(FAIL_COUNT, count);
    }

    /**
     * Return the number oftimes this entry has been processed
     * unsuccessfully. 
     *
     * @return the number oftimes this entry has been processed
     * unsuccessfully.
     */
    public Long getFailCount() {
        return (Long) get(FAIL_COUNT);
    }

    private void setSortOrder(BigDecimal sOrder) {
        set(SORT_ORDER, sOrder);
    }

    private void setInProcess(String s) {
        set(IN_PROCESS, s);
    }

    private String getInProcess() {
        return (String)get(IN_PROCESS);
    }

    public String toString() {
        return getTask() + " for item " + getItemId()
            + "(parent " + getParentId() +") on host "
            + getHost();
    }
    
    /***
     * get next sequence value from publish_to_file_system_seq
     ***/
    static BigDecimal generateID() 
        throws PublishToFileException {
        try {
            return Sequences.getNextValue(SEQUENCE_NAME);
        } catch (SQLException e) {
            throw new PublishToFileException("Failed to generate pfs ID", e);
        }
    }
}
