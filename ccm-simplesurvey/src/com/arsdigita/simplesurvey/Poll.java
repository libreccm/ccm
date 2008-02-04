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
package com.arsdigita.simplesurvey;


import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import java.math.BigDecimal;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;






/**
 * A Poll domain object that represents a simple Poll. This is
 * the main domain object of the Simple Survey application.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: Poll.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Poll extends Survey {

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.simplesurvey.Poll";

    // Object type attribute names
    public static final String FORM_SECTION = "formSection";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";    

    public static final String RECENT_POLL = "com.arsdigita.simplesurvey.RecentPoll";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Poll(DataObject dataObject) {
        super(dataObject);
    }

    public Poll(String typeName) {
        super(typeName);
    }

    public Poll() {

        this(BASE_DATA_OBJECT_TYPE);
    }

    public Poll(ObjectType type) throws DataObjectNotFoundException {
        super(type);
    }

    public Poll(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Poll(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static Survey retrieve(BigDecimal id) 
	throws DataObjectNotFoundException {
	return new Poll(id);
    }
    
    //*** Attribute Methods

    /*
     * Retrieves most recent poll that isn't completed
     */
    public static Poll getMostRecentPoll() {
	DataQuery polls = SessionManager.getSession().retrieveQuery(RECENT_POLL);

	
	Poll poll = null;
	if (polls.next()) {
	    try {
		poll = new Poll((BigDecimal) polls.get("pollID"));
	    } catch (DataObjectNotFoundException ex) {
		// nothing
	    }
	}
	polls.close();
	
	return poll;
    }
}
