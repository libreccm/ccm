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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;

/**
 * A collection of queue entries.
 *
 * @author <a href="mailto:dlutter@redhat.com">David Lutterkort</a>
 * @version $Id: QueueEntryCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
class QueueEntryCollection extends DomainCollection {
  
    /**
     * Produce a colection of all queue entries with the given
     * <code>itemID</code>.
     *
     * @param itemID item for which all queue entries should be listed.
     */
    public QueueEntryCollection(BigDecimal itemID) {
        super(getEntries(itemID));
    }

    /**
     * Delete the current entry in the collection. If the entry is marked as
     * <tt>in process</tt>, the entry is nothing is done.
     */
    public void delete() {
        if ( ! isInProcess() ) {
            m_dataCollection.getDataObject().delete();
        }
    }

    public BigDecimal getID() {
        return (BigDecimal)get(QueueEntry.ID);
    }
    public String getTask() {
        return (String) get(QueueEntry.TASK);
    }

    public boolean isPublishTask() {
        return QueueEntry.TASK_PUBLISH.equals(getTask());
    }
  
    public boolean isUnpublishTask() {
        return QueueEntry.TASK_UNPUBLISH.equals(getTask());
    }
  
    public boolean isRepublishTask() {
        return QueueEntry.TASK_REPUBLISH.equals(getTask());
    }
  
    public boolean isMoveTask() {
        return QueueEntry.TASK_MOVE.equals(getTask());
    }
  
    public boolean isInProcess() {
        return QueueEntry.IN_PROCESS_YES.equals(get(QueueEntry.IN_PROCESS));
    }
  
    private static DataCollection getEntries(BigDecimal itemID) {
        DataCollection result = SessionManager.getSession()
            .retrieve(QueueEntry.BASE_DATA_OBJECT_TYPE);
        result.addEqualsFilter(QueueEntry.ITEM_ID, itemID);
        return result;
    }

}
