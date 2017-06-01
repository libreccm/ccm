/*
 * Copyright (c) 2017 Jens Pelzetter,
 * ScientificCMS Team, http://www.scientificcms.org
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

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Talk extends Publication {

    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.contenttypes.Talk";

    public static final String EVENT = "event";
    public static final String DATE_OF_TALK = "dateOfTalk";
    public static final String PLACE = "place";

    public Talk() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Talk(final BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Talk(final OID oid) {
        super(oid);
    }

    public Talk(final DataObject dataObject) {
        super(dataObject);
    }

    public Talk(final String type) {
        super(type);
    }

    public TalkBundle getTalkBundle() {
        return (TalkBundle) getContentBundle();
    }

    public String getEvent() {
        return (String) get(EVENT);
    }

    public void setEvent(final String event) {
        set(EVENT, event);
    }

    public Date getDateOfTalk() {
        return (Date) get(DATE_OF_TALK);
    }
    
    public void setDateOfTalk(final Date date) {
        set(DATE_OF_TALK, date);
    }
    public String getPlace() {
        return (String) get(PLACE);
    }

    public void setPlace(final String place) {
        set(PLACE, place);
    }
    
}
