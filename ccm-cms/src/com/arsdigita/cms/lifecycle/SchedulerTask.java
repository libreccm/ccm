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

import org.apache.log4j.Logger;

import java.util.TimerTask;



/**
 * The SchedulerTask class provides the method that is scheduled
 * by the timer created by Scheduler.  It simply calls the
 * run method of Scheduler.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 **/
class SchedulerTask extends TimerTask {

    public static final String versionId = "$Id: SchedulerTask.java 754 2005-09-02 13:26:17Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";
    private static final Logger s_log = Logger.getLogger(SchedulerTask.class);
    public void run() {
        try {
            Scheduler.run();
        } catch (Throwable t) {
            s_log.error("Unknown error occured.", t);
            // We don't want to rethrow, since the disruption might be temporarily
            // (eg. connection to database lost).
        }
    }

}
