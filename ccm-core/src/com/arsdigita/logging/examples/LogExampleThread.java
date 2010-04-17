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
package com.arsdigita.logging.examples;

import com.arsdigita.logging.Log;
import org.apache.log4j.Logger;

/**
 * <p>
 * This is a helper class to the example class LogExampleThread
 * </p>
 *
 * @version $Id: LogExampleThread.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class LogExampleThread extends Thread {

    // get the category named the same as this class
    private static final Logger cat = Logger.getLogger(LogExampleThread.class);

    public void run() {
        Log.secure(new Runnable() {
                public void run() {
                    try {
                        sleep((new Double(Math.random() * 100.0)).intValue());
                    } catch (InterruptedException e) {
                        // do nothing
                        return;
                    }

                    // do everything that needs to be logged securely here
                    if(Log.isSecure())
                        cat.info(
                                 "We are running securely in thread " +
                                 Thread.currentThread().getName() +
                                 ". There are " +
                                 Thread.currentThread().activeCount() +
                                 " threads currently running."
                                 );
                    else
                        cat.info(
                                 "We are not running securely in thread " +
                                 Thread.currentThread().getName() +
                                 ". There are " +
                                 Thread.currentThread().activeCount() +
                                 " threads currently running."
                                 );
                }
            });
    }
}
