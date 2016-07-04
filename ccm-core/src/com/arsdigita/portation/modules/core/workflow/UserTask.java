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
package com.arsdigita.portation.modules.core.workflow;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.modules.core.security.User;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class UserTask extends Task {

    private boolean locked;
    private User lockingUser;
    private Date startDate;
    private Date dueDate;
    private long durationMinutes;
    private User notificationSender;
    private List<TaskAssignment> assignments;

    public UserTask(final com.arsdigita.workflow.simple.UserTask
                            trunkUserTask) {
        super(trunkUserTask);

    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new UserTaskMarshaller();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public User getLockingUser() {
        return lockingUser;
    }

    public void setLockingUser(final User lockingUser) {
        this.lockingUser = lockingUser;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(final Date dueDate) {
        this.dueDate = dueDate;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(final long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public User getNotificationSender() {
        return notificationSender;
    }

    public void setNotificationSender(final User notificationSender) {
        this.notificationSender = notificationSender;
    }

    public List<TaskAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(final List<TaskAssignment> assignments) {
        this.assignments = assignments;
    }

    public void addAssignment(final TaskAssignment assignment) {
        assignments.add(assignment);
    }

    public void removeAssignment(final TaskAssignment assignment) {
        assignments.remove(assignment);
    }
}
