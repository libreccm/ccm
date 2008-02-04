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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.runtime.Startup;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: WebApp.java 738 2005-09-01 12:36:52Z sskracic $
 */
public final class WebApp extends DomainObject {
    public static final String versionId =
        "$Id: WebApp.java 738 2005-09-01 12:36:52Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(WebApp.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.web.WebApp";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public WebApp(final DataObject data) {
        super(data);
    }

    public final String getName() {
        return (String) get("name");
    }

    public static WebApp create(final String name) {
        Assert.exists(name, String.class);

        final DataObject data = SessionManager.getSession().create
            (new OID(BASE_DATA_OBJECT_TYPE, name));

        return new WebApp(data);
    }

    public static DomainCollection retrieveAll() {
        final DataCollection all = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);

        return new DomainCollection(all);
    }

    // Informal testing code
    public static final void main(final String[] args) {
	new Startup().run();

        SessionManager.getSession().getTransactionContext().beginTxn();

        if (args.length == 1) {
            WebApp.create(args[0]);
        }

        final DomainCollection webapps = WebApp.retrieveAll();

        while (webapps.next()) {
            final WebApp webapp = (WebApp) webapps.getDomainObject();

            System.out.println(webapp.getName());
        }

        SessionManager.getSession().getTransactionContext().commitTxn();
    }
}
