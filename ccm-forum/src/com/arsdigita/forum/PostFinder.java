/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Application;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;

/**
 * @author cgyg9330
 * (copied from Matt Booth)
 * A URLFinder for Posts 
 */
public class PostFinder implements URLFinder {

    public String find(OID oid, String context)	throws NoValidURLException {
	return find(oid);
    }
    public String find(OID oid) throws NoValidURLException {
	DataObject dobj = SessionManager.getSession().retrieve(oid);
	if (dobj == null) {
	    throw new NoValidURLException("No such data object " + oid);
	}

	Application app = Application.retrieveApplication(dobj);

	if (app == null) {
	    throw new NoValidURLException("Could not find application instance for " + dobj);
	}

	try {
	    ThreadedMessage message = new ThreadedMessage(oid);
 	    ParameterMap params = new ParameterMap();
	    params.setParameter("thread", message.getThread().getID().toString());
	    return URL.there("/" + app.getPath() + "/thread.jsp", params).toString();    
	} catch (DataObjectNotFoundException e) {
	    throw new NoValidURLException("Could not find application instance for " + dobj);
	}
    }
}
