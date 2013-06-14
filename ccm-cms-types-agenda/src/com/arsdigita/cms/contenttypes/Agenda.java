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
import com.arsdigita.cms.TextAsset;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

/**
 * This content type represents an agenda data abject.
 *
 * This class extends {@link com.arsdigita.cms.ContentPage content
 * page} with additional attributes specific to an agenda:
 * <dl>
 *  <dt>summary</dt>      <dd>optional, standard text field, short  
 *                            description, used as lead text</dd> 
 *  <dt>agendaDate</dt>   <dd>mandatory, date/time when it will happen</dd>
 *  <dt>location</dt>     <dd>optional, standard text field, (short) description 
 *                            of location, usable in a list</dd>
 *  <dt>attendees</dt>    <dd>optional, standard text field</dd>
 *  <dt>subjectItems</dt> <dd>optional, standard text field</dd> 
 *  <dt>contactInfo</dt>  <dd>optional, standard text field</dd>
 *  <dt>creationDate</dt> <dd>automatic</dd>
 * </dl>
 *
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class Agenda extends GenericArticle {

    /** Data object type for this domain object (for CMS compatibility) */
    private static final Logger s_log = Logger.getLogger(Logger.class);

    //  PDL stuff  *************************************************************
    /** PDL property name for summary */
    public static final String SUMMARY = "summary";
    /** PDL property name for agenda date */
    public static final String AGENDA_DATE = "agendaDate";
    /** PDL property name for location */
    public static final String LOCATION = "location";
    /** PDL property name for attendees */
    public static final String ATTENDEES = "attendees";
    /** PDL property name for subject items */
    public static final String SUBJECT_ITEMS = "subjectItems";
    /** PDL property name for contact info */
    public static final String CONTACT_INFO = "contactInfo";
    /** PDL property name for creation date */
    public static final String CREATION_DATE = "creationDate";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.cms.contenttypes.Agenda";

    /**
     * Default constructor. This creates a new (empty) Agenda.
     **/
    public Agenda() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>Agenda.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Agenda(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i>.
     *
     * @param id The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Agenda(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public Agenda(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor.  Creates a new content item using the given data
     * object type.  Such items are created as draft versions.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public Agenda(String type) {
        super(type);
    }

    /**
     * For new Agenda objects (content items), sets the associated
     * content type if it has not been already set.
     */
    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public Date getAgendaDate() {
        return (Date) get(AGENDA_DATE);
    }

    public String getDisplayAgendaDate() {
        Date d = getAgendaDate();
        return (d != null) ? DateFormat.getDateInstance(DateFormat.LONG).format(d) : null;
    }

    public void setAgendaDate(Date agendaDate) {
        set(AGENDA_DATE, agendaDate);
    }

    public String getLocation() {
        return (String) get(LOCATION);
    }

    public void setLocation(String location) {
        set(LOCATION, location);
    }

    public String getAttendees() {
        return (String) get(ATTENDEES);
    }

    public void setAttendees(String attendees) {
        set(ATTENDEES, attendees);
    }

    public String getSubjectItems() {
        return (String) get(SUBJECT_ITEMS);
    }

    public void setSubjectItems(String subjectItems) {
        set(SUBJECT_ITEMS, subjectItems);
    }

    public String getContactInfo() {
        return (String) get(CONTACT_INFO);
    }

    public void setContactInfo(String contactInfo) {
        set(CONTACT_INFO, contactInfo);
    }

    public String getSummary() {
        return (String) get(SUMMARY);
    }

    public void setSummary(String summary) {
        set(SUMMARY, summary);
    }

    public Date getCreationDate() {
        return (Date) get(CREATION_DATE);
    }

    public void setCreationDate(Date creationDate) {
        set(CREATION_DATE, creationDate);
    }
    // Search stuff to allow the content type to be searchable
    public static final int SUMMARY_LENGTH = 200;

    public String getSearchSummary() {
        TextAsset ta = getTextAsset();

        if (ta != null) {
            return com.arsdigita.util.StringUtils.truncateString(ta.getText(),
                    SUMMARY_LENGTH,
                    true);
        } else {
            return "";
        }
    }
}
