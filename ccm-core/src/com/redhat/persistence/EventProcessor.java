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
package com.redhat.persistence;

/**
 * Event processor.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @since 2003-02-20
 * @version $Id: EventProcessor.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public abstract class EventProcessor {

    /**
     * During this method, calls into the session to which the event processor
     * is attached are not allowed.
     *
     * @param isCommit indicates whether the clean up corresponds to a
     * commit. If true, the event processor should throw an exception if it is
     * not in a consistent state. This exception will prevent the commit
     * from actually taking place. If false, exceptions should not be thrown.
     */
    protected abstract void cleanUp(boolean isCommit);

    protected abstract void write(Event ev);

    protected abstract void flush();
}
