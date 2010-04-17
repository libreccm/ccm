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
package com.arsdigita.logging;

/**
 * <p>
 * Defines APIs for logging in addition to those supplied by log4j.
 * Mainly, this class defines an API for logging securely.
 * </p>
 *
 * @author Yon Feldman 
 * @version $Id: Log.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Log {

    // this variable will hold the secure state for this class on a per thread
    // basis.
    private static ThreadLocal s_secureState = new ThreadLocal() {
            public Object initialValue() {
                return Boolean.FALSE;
            }
        };

    /**
     * Checks whether we are currently logging securely.
     *
     * @return <code>true</code> if we are logging securely, <code>false</code> otherwise.
     */
    public static boolean isSecure() {
        return ((Boolean) s_secureState.get()).booleanValue();
    }

    // begin logging securely.
    private static void startSecureLogging() {
        s_secureState.set(Boolean.TRUE);
    }

    // reset the secure logging state to the value passed in.
    private static void resetSecureLogging(Boolean inSecureState) {
        s_secureState.set(inSecureState);
    }

    /**
     * Any log messages run inside this method will be logged securely.
     *
     * @param r the class that implements the <code>Runnable</code> interface
     */
    public static void secure (Runnable r) {
        // store old value so that we can reset to the appropriate value when
        // done.
        boolean inSecureState = isSecure();

        // start logging securely
        startSecureLogging();

        // run code
        r.run();

        // reset logging state
        resetSecureLogging(new Boolean(inSecureState));
    }
}
