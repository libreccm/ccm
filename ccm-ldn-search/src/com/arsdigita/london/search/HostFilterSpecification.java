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
 */

package com.arsdigita.london.search;

import com.arsdigita.search.FilterSpecification;
import com.arsdigita.persistence.OID;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.util.Assert;

/**
 * A filter spec for supplying a list of server hosts
 * to the remote server service
 */
public class HostFilterSpecification extends FilterSpecification {

    public final static String SERVERS = "servers";
    
    /**
     * Creates a new filter restricting results to a single
     * search host
     * @param hostID the content type name
     */
    public HostFilterSpecification(OID hostID) {
        this(lookupHosts(new OID[] {hostID}));
    }

    /**
     * Creates a new filter restricting results to a set
     * of search hosts
     * @param hostID the content type names
     */
    public HostFilterSpecification(OID[] hostIDs) {
        this(lookupHosts(hostIDs));
    }

    /**
     * Creates a new filter restricting results to a single
     * search host
     * @param host the search host
     */
    public HostFilterSpecification(Server host) {
        this(new Server[] { host });
    }

    /**
     * Creates a new filter restricting results to a set
     * of search hosts.
     * @param host the search hosts
     */
    public HostFilterSpecification(Server[] hosts) {
        super(new Object[] { SERVERS, disconnect(hosts) },
              new HostFilterType());
    }
    
    /**
     * Returns the set of object hosts to filter on
     * @return the object host
     */
    public Server[] getServers() {
        return (Server[])get(SERVERS);
    }

    private static Server[] disconnect(Server[] servers) {
        for (int i = 0 ; i < servers.length ; i++) {
            servers[i].disconnect();
        }
        return servers;
    }

    private static Server[] lookupHosts(OID[] hostIDs) {
        Assert.exists(hostIDs, OID[].class);
        Server[] hosts = new Server[hostIDs.length];
        for (int i = 0 ; i < hostIDs.length ; i++) {
            hosts[i] = (Server)DomainObjectFactory.newInstance(hostIDs[i]);
        }
        return hosts;
    }
}
