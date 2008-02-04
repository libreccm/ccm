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
package com.arsdigita.forum;

import com.arsdigita.cms.lifecycle.LifecycleListener;
import com.arsdigita.cms.lifecycle.LifecycleEvent;

import org.apache.log4j.Logger;

/**
 * This lifecycle listener deletes the noticeboard positng
 * at the end (of lifecycle);
 *
 * @version $Id: ExpirationListener.java 542 2005-06-03 15:17:05Z sskracic $
 */
public class ExpirationListener implements LifecycleListener {

    private static final Logger s_log = Logger.getLogger(ExpirationListener.class);

    /**
     * Invoked when an lifecycle or phase begins
     */
    public void begin(LifecycleEvent e) {
        //don't do anything
    }

    /**
     * Invoked when an lifecycle or phase ends
     */
    public void end(LifecycleEvent e) {
        try {
            Post posting = new Post(e.getOID());
            posting.delete();
        }  catch (Exception ex) {
            s_log.error("Error trying to delete posting " + e.getOID(), ex);
        }
    }
}
