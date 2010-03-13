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

import com.arsdigita.util.Assert;

/**
 * Utility methods for lifecycle durations.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #11 $ $Date: 2004/08/17 $
 * @version $Id: Duration.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Duration {

    /**
     * A convenience wrapper around {@link #formatDuration(int)}.
     *
     * @see #formatDuration(int)
     * @pre minutes != null
     */
    public static String formatDuration(Integer minutes) {
        Assert.exists(minutes, "minutes");
        return formatDuration(minutes.intValue());
    }

    /**
     * Formats a duration into a user friendly format of the form "x days, h
     * hours, m minutes".
     *
     * @param minutes the duration in minutes
     */
    public static String formatDuration(int minutes) {
        int[] dhm = formatDHM(minutes);
        StringBuffer buffer = new StringBuffer();

        if ( dhm[0] > 0 ) {
            buffer.append(dhm[0]).append(" days");
        }

        if ( dhm[1] > 0 ) {
            if ( dhm[0] > 0 ) {
                buffer.append(", ");
            }
            buffer.append(dhm[1]).append(" hours");
        }

        if ( dhm[0] > 0 || dhm[1] > 0 ) {
            buffer.append(", ");
        }
        buffer.append(dhm[2]).append(" minutes");

        return buffer.toString();
    }

    /**
     * Formats time in minutes into a days/hours/minutes format.
     **/
    public static int[] formatDHM(int minutes) {
        int[] dhm = new int[3];

        int days = minutes / (60*24);
        int hours = minutes / 60;  // no pun intended
        int mins = minutes;

        if ( days > 0 ) {
            hours = hours - (days * 24);
            mins = mins - (days * 24 * 60);
        }
        if ( hours > 0 ) {
            mins = mins - (hours * 60);
        }

        dhm[0] = days;
        dhm[1] = hours;
        dhm[2] = mins;
        return dhm;
    }

    /**
     * Formats time in minutes into a days/hours/minutes format.
     *
     * @see #formatDHM(int)
     * @param minutes timespan in minutes
     */
    public static Integer[] formatDHM(Integer minutes) {
        int dhm[] = formatDHM(minutes.intValue());
        return copyArray(dhm);
    }

    private static Integer[] copyArray(int[] from) {
        Assert.exists(from, "from");
        Integer[] to = new Integer[from.length];
        for (int ii=0; ii<from.length; ii++) {
            to[ii] = new Integer(from[ii]);
        }
        return to;
    }
}
