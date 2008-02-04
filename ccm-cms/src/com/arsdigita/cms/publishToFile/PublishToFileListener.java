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

/**
 * The listener that does the actual work of writing and removing
 * files. The {@link QueueManager queue manager} calls the listener during
 * queue processing and notifies it of all the tasks that have been
 * queued. The package documentation describes how to set the concrete
 * listener to use.
 *
 * <p> The queue manager processes queue entries in blocks, following these
 * steps:
 * <pre>
 *   Iterator block = get block of queue entries from DB;
 *   start DB transaction
 *   listener.transactionStart();
 *   while (block.hasNext()) {
 *     listener.doTask(block.next());
 *   }
 *   listener.transactionEnd();
 *   commit DB transaction
 * </pre>
 * If the call to {@link #doTask doTask} throws an exception, the entry is
 * marked as failing in the queue and the next entry is processed. If the
 * call to {@link #transactionStart} or {@link #transactionEnd} causes an
 * exception, the whole transaction is rolled back.
 *
 * @see com.arsdigita.cms.publishToFile Configuration information
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 */


public interface PublishToFileListener {
  
  /**
   * Process one queued task.
   * @param qe  QueueEntry describing the task that should be performed
   */
  public boolean doTask(QueueEntry qe);

  /**
   * Queue manager just started the processing of a new block.
   */
  public void transactionStart();

  /**
   * Queue manager finished processing one block and is about to commit the
   * transaction.
   */
  public void transactionEnd();

}
