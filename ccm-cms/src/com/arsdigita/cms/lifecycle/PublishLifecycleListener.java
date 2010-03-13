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
package com.arsdigita.cms.lifecycle;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * This class makes an item live while it is in a lifecycle.
 *
 * @author Michael Pih
 * @author Stanislav Freidin
 * @version $Id: PublishLifecycleListener.java 754 2005-09-02 13:26:17Z sskracic $
 */
public class PublishLifecycleListener implements LifecycleListener {

    private static final Logger s_log = Logger.getLogger
        (PublishLifecycleListener.class);

    /**
     * Invoked when a lifecycle or phase ends. The content item will
     * no longer be live.
     *
     * @param event The lifecycle event
     */
    public void begin(LifecycleEvent event) {
        s_log.info("PublishLifecycleListener begin is called. \n" +
                   event.toString() + "\n" +
                   event.getOID().toString());

        final ContentItem item = getContentItem(event.getOID());

        if (Assert.isEnabled()) {
            Assert.isFalse(item instanceof ContentBundle,
                           "Content bundles cannot be published");
        }

        // Make the pending version live. Hack: this code should work
        // regardless of which item the lifecycle is attached to.

        ContentItem master = null;
        ContentItem pending = null;

        if (item.isDraftVersion()) {
            master = item;
            pending = master.getPublicVersion();

            if (Assert.isEnabled()) {
                Assert.isEqual(ContentItem.PENDING, pending.getVersion());
            }
        } else {
            // Item is already live or is pending.
            // ContentItem.promotePendingVersion does the right thing for both
            pending = item;
            master = pending.getDraftVersion();

            Assert.isNotEqual(master, pending);
            Assert.isEqual(ContentItem.DRAFT, master.getVersion());
        }

        master.promotePendingVersion(pending);
    }

    /**
     * Invoked when a lifecycle or phase ends.
     *
     * @param event The lifecycle event
     */
    public void end(LifecycleEvent event) {
        s_log.info("PublishLifecycleListener end is called. \n" +
                    event.toString() + "\n" +
                    event.getOID().toString());

        ContentItem item = getContentItem(event.getOID());
        ContentItem master = null;

        if (item.isLiveVersion()) {
            // Get the master item for this version
            master = item.getWorkingVersion();
        } else {
            master = item;
        }

        if (master.isLive()) {
            master.setLive(null);
        }

    }

    private ContentItem getContentItem(OID oid) {
        ContentItem item = (ContentItem)DomainObjectFactory.newInstance(oid);
        return item;
    }

}
