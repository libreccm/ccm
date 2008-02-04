/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.lucene;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Host;
import com.arsdigita.web.Web;


import org.apache.log4j.Logger;

/**
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2004-01-07
 * @version $DateTime: 2004/08/16 18:10:38 $ $Revision: #5 $
 **/
class IndexId extends DomainObject {
    private final static Logger s_log = Logger.getLogger(IndexId.class);

    private static final String DATA_TYPE =
        "com.arsdigita.search.lucene.IndexId";
    private static final String INDEX_ID  = "indexID";
    private static final String HOST      = "host";

    private IndexId() {
        super(SessionManager.getSession().create(oid()));
        set(HOST, getHost());

        int id = 0;

        DataCollection ids = SessionManager.getSession().retrieve( DATA_TYPE );
        ids.addOrder( INDEX_ID + " desc" );
        ids.setRange( new Integer(1), new Integer(2) );

        if( ids.next() ) {
            Integer indexID = (Integer) ids.get( INDEX_ID );
            id = indexID.intValue() + 1;
        }

        ids.close();

        Assert.truth(id >= 0, "id greater than or equal to 0");
        Assert.truth(id <= 30, "id less than or equal to 30");

        set(INDEX_ID, new Integer(id));

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "New Index ID " + id );
        }
    }

    private static OID oid() {
        return new OID(DATA_TYPE, getHost().getID());
    }

    private static Host getHost() {
        Host host = Host.retrieve(Web.getConfig().getHost());
        Assert.exists(host, Host.class);
        return host;
    }

    static Integer retrieveIndexID() {
        DataObject indexID =
            SessionManager.getSession().retrieve(oid());
        return indexID==null ? null : (Integer) indexID.get(INDEX_ID);
    }

    static class LoaderImpl implements Initializer.Loader {
        public void load() {
            if (IndexId.retrieveIndexID() == null ) {
                new IndexId();
            }
        }
    }
}
