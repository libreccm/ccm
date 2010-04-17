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

import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This event class stores the information necessary for the firing of events
 * in the LifecycleListener class.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: LifecycleEvent.java 2070 2010-01-28 08:47:41Z pboy $
 */

public class LifecycleEvent {

    private int m_eventType;
    private Date m_startDateTime, m_endDateTime;
    private OID m_oid;

    public final static int PHASE = 0;
    public final static int LIFECYCLE = 1;

    /**
     * Construct a lifecycle event
     *
     * @param eventType specify either PHASE or LIFECYCLE
     * @param start the start date time of this event
     * @param end the end date time of this event
     * @param objectType the object-type of the object receiving of the action
     * @param id the ID of the object receiving of the action
     */
    protected LifecycleEvent(int eventType, Date start, Date end,
                             String objectType, BigDecimal id) {
        this(eventType, start, end, new OID(objectType, id)) ;
    }

    /**
     * Construct a lifecycle event
     *
     * @param eventType specify either PHASE or LIFECYCLE
     * @param start the start date time of this event
     * @param end the end date time of this event
     * @param oid the OID of the object of receiving of the action
     */
    protected LifecycleEvent(int eventType, Date start,
                             Date end, OID oid) {
        m_eventType = eventType;
        m_startDateTime = start;
        m_endDateTime = end;
        m_oid = oid;
    }

    public int getEventType() {
        return m_eventType;
    }

    public Date getStartDate() {
        return m_startDateTime;
    }
    public Date getEndDate() {
        return m_endDateTime;
    }

    public OID getOID() {
        return m_oid;
    }

    public String toString() {
        String type;
        String endDate;

        if (m_eventType == PHASE) {
            type = "Phase";
        } else {
            type = "Lifecycle";
        }

        if ( m_endDateTime == null ) {
            endDate = "never";
        } else {
            endDate = m_endDateTime.toString();
        }

        return "LifecycleEvent (" + type + ")- startDateTime:" +
            m_startDateTime.toString() + " endDateTime:" + endDate;
    }

}
