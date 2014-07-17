/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

/**
 * <p><code>DomainObject</code> class to represent Event <code>ContentType</code>
 * objects.
 * <br />
 * It represents an event object and provides methods for creating new event
 * objects, retrieving existing objects from the persistent storage and 
 * retrieving and setting is properties.</p>
 * <p>This class extends {@link com.arsdigita.cms.contenttypes.GenericArticle content page} and
 * inherits title, name (filename), body (TextAsset), and metadata. It adds 
 * extended attributes specific for an event:</p>
 * <dl>
 *  <dt>lead</dt>            <dd>optional, standard text field, short  
 *                               description (summary), used as lead text</dd> 
 *  <dt>startDate</dt>       <dd>mandatory, date when it will begin</dd>
 *  <dt>startTime</dt>       <dd>mandatory, time when it will begin</dd>
 *  <dt>endDate</dt>         <dd>optional, date when it will end</dd>
 *  <dt>endTime</dt>         <dd>optional, time when it will end</dd>
 *  <dt>eventDate</dt>       <dd>optional, rich text field. From pdl File:
 *                               The date and time of the event, stored as varchar 
 *                               for now so you can enter other information
 *                               configurable as hidden in the authoring kit</dd>
 *  <dt>location</dt>        <dd>optional, rich text field, description 
 *                               of location</dd>
 *  <dt>mainContributor</dt> <dd>optional, rich text field,
 *                               configurable as hidden in the authoring kit</dd>
 *  <dt>eventType</dt>       <dd>optional, standard text field, type of event, 
 *                               configurable as hidden in the authoring kit</dd>
 *  <dt>mapLink</dt>         <dd>optional, standard text field, ling to a map,
 *                               configurable as hidden in the authoring kit</dd>
 *  <dt>cost</dt>            <dd>optional, standard text field, costs,
 *                               configurable as hidden in the authoring kit</dd>
 * </dl>
 * <p>Some of its behaviour can be configured by <code>EventConfig</code>.</p>
 *
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class Event extends GenericArticle {

    private final static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(Event.class);
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.cms.contenttypes.Event";

    /** PDL property name for lead (summary) */
    public static final String LEAD = "lead";
    /** PDL property name for event date */
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String EVENT_DATE = "eventDate";
    /** PDL property name for location */
    public static final String LOCATION = "location";
    /** PDL property name for main contributor */
    public static final String MAIN_CONTRIBUTOR = "mainContributor";
    /** PDL property name for event type */
    public static final String EVENT_TYPE = "eventType";
    /** PDL property name for map link */
    public static final String MAP_LINK = "mapLink";
    /** PDL property name for cost */
    public static final String COST = "cost";
    public static final String RECENT_EVENT = "com.arsdigita.cms.contenttypes.RecentEvent";
    private static final EventConfig s_config = new EventConfig();

    static {
        s_log.debug("Static initalizer starting...");
        s_config.load();
        s_log.debug("Static initalizer finished.");
    }

    public static final EventConfig getConfig() {
        return s_config;
    }

    /**
     * Default constructor. This creates a new (empty) Event.
     **/
    public Event() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>Event.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Event(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Event(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public Event(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor.  Creates a new content item using the given data
     * object type.  Such items are created as draft versions.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public Event(String type) {
        super(type);
    }

    /**
     * For new content items, sets the associated content type if it
     * has not been already set.
     */
    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

    public String formatDate(Date date) {
        return (date != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(date) : null;
    }

    public String formatTime(Date time) {
        return (time != null) ? DateFormat.getTimeInstance(DateFormat.SHORT).format(time) : null;
    }

    /* accessors *****************************************************/
    public Date getStartTime() {
        return (Date) get(START_TIME);
    }

    public void setStartTime(Date startTime) {
        set(START_TIME, startTime);
    }

    public String getDisplayStartTime() {
        return formatTime(getStartTime());
    }

    public Date getEndTime() {
        return (Date) get(END_TIME);
    }

    public void setEndTime(Date endTime) {
        set(END_TIME, endTime);
    }

    public String getDisplayEndTime() {
        return formatTime(getEndTime());
    }

    public Date getStartDate() {
        Date startDate = (Date) get(START_DATE);
        return startDate;
    }

    public void setStartDate(Date startDate) {
        set(START_DATE, startDate);
    }

    public String getDisplayStartDate() {
        return formatDate(getStartDate());
    }

    public Date getEndDate() {
        Date endDate = (Date) get(END_DATE);
        return endDate;
    }

    public String getDisplayEndDate() {
        return formatDate(getEndDate());
    }

    public void setEndDate(Date endDate) {
        set(END_DATE, endDate);
    }

    public String getEventDate() {
        return (String) get(EVENT_DATE);
    }

    public void setEventDate(String eventDate) {
        set(EVENT_DATE, eventDate);
    }

    public String getLocation() {
        return (String) get(LOCATION);
    }

    public void setLocation(String location) {
        set(LOCATION, location);
    }

    public String getLead() {
        return (String) get(LEAD);
    }

    public void setLead(String lead) {
        set(LEAD, lead);
    }

    public String getMainContributor() {
        return (String) get(MAIN_CONTRIBUTOR);
    }

    public void setMainContributor(String mainContributor) {
        set(MAIN_CONTRIBUTOR, mainContributor);
    }

    public String getEventType() {
        return (String) get(EVENT_TYPE);
    }

    public void setEventType(String eventType) {
        set(EVENT_TYPE, eventType);
    }

    public String getMapLink() {
        return (String) get(MAP_LINK);
    }

    public void setMapLink(String mapLink) {
        set(MAP_LINK, mapLink);
    }

    public String getCost() {
        return (String) get(COST);
    }

    public void setCost(String cost) {
        set(COST, cost);
    }
    // Search stuff to allow the content type to be searchable
    public static final int SUMMARY_LENGTH = 200;

    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getLead(),
                SUMMARY_LENGTH,
                true);
    }


    /*
     * Returns a random event which is not over
     */
    public static Event getRandomEvent() {
        DataQuery events = SessionManager.getSession().retrieveQuery(RECENT_EVENT);

        Event event = null;
        long size = events.size();

        if (size > 0) {
            events.rewind();

            long n = (System.currentTimeMillis() % size) + 1;
            int count = 0;

            while (n > count) {
                count++;
                events.next();
                if (n == count) {
                    try {
                        event = new Event((BigDecimal) events.get("eventID"));
                    } catch (DataObjectNotFoundException ex) {
                        //
                    }
                }
            }
        }
        events.close();

        return event;
    }
}
