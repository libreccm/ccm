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
package com.arsdigita.cms;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.runtime.Startup;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: TroikaRickshawUpgrade.java 754 2005-09-02 13:26:17Z sskracic $
 */
public class TroikaRickshawUpgrade {
    private static final Logger s_log = Logger.getLogger
        (TroikaRickshawUpgrade.class);

    private static final String s_key = "ccm-cms";

    public static final void main(final String[] args) throws IOException {
        new Startup().run();

        final Session session = SessionManager.getSession();

        final TransactionContext tc = session.getTransactionContext();
        tc.beginTxn();

        final ObjectType type = session.getMetadataRoot().getObjectType
            ("com.arsdigita.cms.AuthoringStep");
        final DataCollection coll = session.retrieve(type);

        while (coll.next()) {
            final DataObject data = coll.getDataObject();
            final String component = (String) data.get("component");

            if (component != null && component.equals
                   ("com.arsdigita.cms.contenttypes.ui.FileAttachmentsStep")) {
                data.set
                    ("component",
                     "com.arsdigita.cms.contentassets.ui.FileAttachmentsStep");
            }
        }

        session.flushAll();

        tc.commitTxn();
    }
}
