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
package com.arsdigita.web;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.servlet.HttpHost;
import java.math.BigDecimal;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * This class represents info about a single host running
 * a server in a webapp cluster.
 */
public class Host extends DomainObject {

    private static final Logger s_log = Logger.getLogger(Host.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.web.Host";

    public static final String ID = "id";
    public static final String SERVER_NAME = "serverName";
    public static final String SERVER_PORT = "serverPort";

    protected Host() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected Host(String type) {
        super(type);
    }

    public Host(DataObject dobj) {
        super(dobj);
    }

    public void initialize() {
        super.initialize();

        if (isNew()) {
            try {
                set(ID, Sequences.getNextValue());
            } catch (SQLException ex) {
                throw new UncheckedWrapperException("cannot allocate id", ex);
            }
        }
    }

    /**
     * Creates a new host
     *
     * @param serverName the fully qualified server hostname
     * @param serverPort the HTTP port number
     */
    public static Host create(String serverName,
                              int serverPort) {
        Host host = new Host();
        host.setServerName(serverName);
        host.setServerPort(serverPort);
        return host;
    }

    /**
     * Retrieves the host with a given id
     *
     * @param id the host id
     */
    public static Host retrieve(BigDecimal id) {
        Session session = SessionManager.getSession();
        DataCollection hosts = session.retrieve(BASE_DATA_OBJECT_TYPE);
        hosts.addEqualsFilter(ID, id);
        if (hosts.next()) {
            Host host = new Host(hosts.getDataObject());
            hosts.close();
            return host;
        }

        throw new DataObjectNotFoundException("cannot find host with id " + id);
    }

    /**
     * Retrieves the collection of hosts associated with this server.
     *
     * @return A <code>DomainCollection</code> of <code>Hosts</code>;
     * it cannot be null
     */
    public static final DomainCollection retrieveAll() {
        final Session session = SessionManager.getSession();

        final DataCollection hosts = session.retrieve(BASE_DATA_OBJECT_TYPE);

	return new DomainCollection(hosts);
    }

    /**
     * Finds a host using fields from the given <code>HttpHost</code>.
     *
     * @return The <code>Host</code> that corresponds to
     * <code>hhost</code> or null if the host is not found
     */
    public static Host retrieve(final HttpHost hhost) {
        final String name = hhost.getName();
        final Integer port = new Integer(hhost.getPort());

        final Session session = SessionManager.getSession();
        final DataCollection hosts = session.retrieve(BASE_DATA_OBJECT_TYPE);

        hosts.addEqualsFilter(SERVER_NAME, name);
        hosts.addEqualsFilter(SERVER_PORT, port);

        if (hosts.next()) {
            final Host host = new Host(hosts.getDataObject());

            hosts.close();

            return host;
        } else {
            return null;
        }
    }

    /**
     * Find a Host object for a given server.
     * @param serverName the fully qualified host name, excluding port
     * @throws DataObjectNotFoundException if no host exists with that name
     */
    public static Host findByServerName(String serverName) {
        Session session = SessionManager.getSession();
        DataCollection hosts = session.retrieve(BASE_DATA_OBJECT_TYPE);
        hosts.addEqualsFilter(SERVER_NAME, serverName);
        if (hosts.next()) {
            Host host = new Host(hosts.getDataObject());
            hosts.close();
            return host;
        }

        throw new DataObjectNotFoundException(
            "cannot find host with name " + serverName);
    }

    public BigDecimal getID() {
        return (BigDecimal)get(ID);
    }

    public String getServerName() {
        return (String)get(SERVER_NAME);
    }

    public void setServerName(String name) {
        set(SERVER_NAME, name);
    }

    public int getServerPort() {
        return ((Integer)get(SERVER_PORT)).intValue();
    }

    public void setServerPort(int port) {
        set(SERVER_PORT, new Integer(port));
    }

    public String toString() {
        if (getServerPort() == 80) {
            return getServerName();
        } else {
            return getServerName() + ":" + getServerPort();
        }
    }

    public URL getURL() {
        return getURL(null, null);
    }

    public URL getURL(String path,
                      ParameterMap params) {
        return getURL(Web.getConfig().getDefaultScheme(), path, params);
    }

    public URL getURL(String scheme,
                      String path,
                      ParameterMap params) {
        WebConfig config = Web.getConfig();
        return new URL(scheme,
                       getServerName(),
                       getServerPort(),
                       config.getDispatcherContextPath(),
                       config.getDispatcherServletPath(),
                       path,
                       params);
    }
}
