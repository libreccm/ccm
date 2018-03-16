/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.conversion.core.workflow;

import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.security.User;
import com.arsdigita.portation.modules.core.workflow.TaskComment;

import java.util.List;

/**
 * Class for converting all
 * trunk-{@link com.arsdigita.workflow.simple.TaskComment}s into
 * ng-{@link TaskComment}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 9/27/17
 */
public class TaskCommentConversion extends AbstractConversion {
    private static TaskCommentConversion instance;

    static {
        instance = new TaskCommentConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.workflow.simple.TaskComment}s
     * from the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link TaskComment}s focusing on keeping
     * all the associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("task comments");
        List<com.arsdigita.workflow.simple.TaskComment> trunkTaskComments = com
                .arsdigita.workflow.simple.TaskComment.getAllTaskComments();

        ExportLogger.converting("task comments");
        createTaskCommentsAndSetAssociations(trunkTaskComments);
    }

    /**
     * Creates the equivalent ng-class of the {@code TaskComment} and restores
     * the associations to other classes.
     *
     * @param trunkTaskComments List of all
     *                       {@link com.arsdigita.workflow.simple.TaskComment}s
     *                       from this old trunk-system.
     */
    private void createTaskCommentsAndSetAssociations(
            List<com.arsdigita.workflow.simple.TaskComment> trunkTaskComments) {
        int processed = 0;

        for (com.arsdigita.workflow.simple.TaskComment trunkTaskComment :
                trunkTaskComments) {
            // create TaskComments
            TaskComment taskComment = new TaskComment(trunkTaskComment);

            // set author associations
            com.arsdigita.kernel.User trunkAuthor = trunkTaskComment.getUser();
            if (trunkAuthor != null) {
                User author = NgCoreCollection
                        .users
                        .get(trunkAuthor.getID().longValue());
                taskComment.setAuthor(author);
            }

            processed++;
        }

        ExportLogger.created("task comments", processed);
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static TaskCommentConversion getInstance() {
        return instance;
    }
}
