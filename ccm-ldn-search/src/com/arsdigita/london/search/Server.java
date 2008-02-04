/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.search;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.DataCollection;


/**
 * @author Daniel Berrange <berrange@redhat.com>
 */
public class Server extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE
	= "com.arsdigita.london.search.Server";

    // Attributes
    public static final String TITLE = "title";
    public static final String HOSTNAME = "hostname";

    public Server() {
	super(BASE_DATA_OBJECT_TYPE);
    }

    public Server(BigDecimal id) 
	throws DataObjectNotFoundException {
	this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Server(DataObject obj) {
	super(obj);
    }

    public Server(OID oid)
	throws DataObjectNotFoundException {
	super(oid);
    }

    public static Server create(String hostname,
				String title) {
	Server server = new Server();

	server.setHostname(hostname);
	server.setTitle(title);

	return server;
    }

    public static Server retrieve(BigDecimal id) 
	throws DataObjectNotFoundException {
        
        Session session = SessionManager.getSession();
        DataCollection server = session.retrieve(BASE_DATA_OBJECT_TYPE);
	
        server.addEqualsFilter(ACSObject.ID, id);
        
        if (server.next()) {
            DataObject obj = server.getDataObject();
            server.close();
            return new Server(obj);
        }
        
        throw new DataObjectNotFoundException("cannot find server " + id);
    }

    public static Server retrieve(String hostname) 
	throws DataObjectNotFoundException {
        
        Session session = SessionManager.getSession();
        DataCollection server = session.retrieve(BASE_DATA_OBJECT_TYPE);
	
        server.addEqualsFilter(HOSTNAME, hostname);
        
        if (server.next()) {
            DataObject obj = server.getDataObject();
            server.close();
            return new Server(obj);
        }
        
        throw new DataObjectNotFoundException("cannot find server" + hostname);
    }

    public static ServerCollection retrieveAll() {
        Session session = SessionManager.getSession();
        DataCollection servers = session.retrieve(BASE_DATA_OBJECT_TYPE);
	servers.addOrder(TITLE);
	return new ServerCollection(servers);
    }

    /*
     * Accessor to the base object type.
     * @return The fully qualified name of the supporting data object.
     */
    public String getBaseDataObjectType() {
	return BASE_DATA_OBJECT_TYPE;
    }

    /*
     * @param title The title of the channel (SEARCH server).
     */
    public void setTitle(String title) {
	set(TITLE, title);
    }

    public String getTitle() {
	return (String)get(TITLE);
    }

    /*
     * @param hostname The hostname to the main site containing the channel.
     */
    public void setHostname(String hostname) {
	set(HOSTNAME, hostname);
    }

    public String getHostname() {
	return (String)get(HOSTNAME);
    }
}
