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
package com.arsdigita.search.lucene;

import java.util.Timer;

import org.apache.log4j.Logger;

/**
 * Index
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: Index.java 738 2005-09-01 12:36:52Z sskracic $
 **/

class Index {

    private static final Logger s_log = Logger.getLogger(Index.class);

    private static String s_location;

    /**
     *
     */
    private static final Timer TIMER = new Timer(true);

    private static Integer s_id;

    synchronized static int getIndexID() {
        if ( s_id == null ) {
            s_id = IndexId.retrieveIndexID();
            if ( s_id == null ) {
                throw new IllegalStateException
                    ("lucene index id has not been initialized");
            }
        }

        return s_id.intValue();
    }

    static void setLocation(String location) {
        s_location = location;
    }

    public static final String getLocation() {
        return s_location;
    }

    public static final Timer getTimer() {
        return TIMER;
    }

}
