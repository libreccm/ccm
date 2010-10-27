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
package com.arsdigita.versioning;

import com.redhat.persistence.EventProcessor;
import com.redhat.persistence.EventProcessorManager;

// new versioning

/**
 * This class interfaces the versioning package with persistence.
 *
 * <p>The versioning initializer registers this event processor manager with the
 * {@link com.arsdigita.persistence.SessionManager}. When the session manager
 * spawns a new session, it queries this processor manager for an instance of an
 * event processor to be registered with the newly spawned session.  The session
 * then becomes responsible for dispatching persistence events to the {@link
 * VersioningEventProcessor versioning event processor}. </p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-28
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/
final class VersioningProcessorManager implements EventProcessorManager {
    
    private static final ThreadLocal s_processor = new ThreadLocal() {
            public Object initialValue() {
                return new VersioningEventProcessor();
            }
        };

    VersioningProcessorManager() {}

    public EventProcessor getEventProcessor() {
        return getVersioningEventProcessor();
    }

    static VersioningEventProcessor getVersioningEventProcessor() {
        return (VersioningEventProcessor) s_processor.get();
    }
}
