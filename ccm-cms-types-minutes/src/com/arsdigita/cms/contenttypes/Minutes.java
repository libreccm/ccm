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
import com.arsdigita.cms.TextPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * This content type represents a minutes.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/
public class Minutes extends TextPage {

    private static Logger s_log =
        Logger.getLogger(Minutes.class);

    /** PDL property name for attendees */
    public static final String ATTENDEES = "attendees";
    /** PDL property name for description */
    public static final String DESCRIPTION = "description";
    /** PDL property name for action item */
    public static final String ACTION_ITEM = "actionItem";
    /** PDL property name for minute number */
    public static final String MINUTE_NUMBER = "minuteNumber";
    /** PDL property name for description */
    public static final String DESCRIPTION_OF_MINUTES = "descriptionOfMinutes";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.Minutes";

    public Minutes() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Minutes(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Minutes(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public Minutes(DataObject obj) {
        super(obj);
    }

    public Minutes(String type) {
        super(type);
    }


    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public String getAttendees() {
        return (String) get(ATTENDEES);
    }

    public void setAttendees(String attendees) {
        set(ATTENDEES, attendees);
    }

    public String getActionItem() {
        return (String) get(ACTION_ITEM);
    }

    public void setActionItem(String actionItem) {
        set(ACTION_ITEM, actionItem);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String Description) {
        set(DESCRIPTION, Description);
    }

    public String getDescriptionOfMinutes() {
        return (String) get(DESCRIPTION_OF_MINUTES);
    }

    public void setDescriptionOfMinutes(String descriptionOfMinutes) {
        set(DESCRIPTION_OF_MINUTES, descriptionOfMinutes);
    }

    public String getMinuteNumber() {
        return (String) get(MINUTE_NUMBER);
    }

    public void setMinuteNumber(String minuteNumber) {
        set(MINUTE_NUMBER, minuteNumber);
    }

    public static final int SUMMARY_LENGTH = 200;
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getDescription(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }

}
