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
package com.arsdigita.core;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.security.KeyStorage;
import com.arsdigita.persistence.DedicatedConnectionSource;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.pdl.PDLCompiler;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.Startup;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: TroikaRickshawUpgrade2.java 738 2005-09-01 12:36:52Z sskracic $
 */
public class TroikaRickshawUpgrade2 {
    private static final Logger s_log = Logger.getLogger
        (TroikaRickshawUpgrade2.class);

    public static final void main(final String[] args) throws IOException {
        final Session session = makeSession
            (RuntimeConfig.getConfig().getJDBCURL());

        final TransactionContext tc = session.getTransactionContext();
        tc.beginTxn();

        final ObjectType type = session.getMetadataRoot().getObjectType
            ("com.arsdigita.runtime.Initializer");
        session.create(new OID(type, "com.arsdigita.core.Initializer"));
        session.flushAll();

        tc.commitTxn();

        final Startup startup = new Startup();

        // This awkward hack exists to handle the case where the
        // ccm-core being upgraded contains UDCTs that reference CMS
        // content types.
//         try {
//             final Class clacc = Class.forName("com.arsdigita.cms.Initializer");
//             startup.add((com.arsdigita.runtime.Initializer)
//                             Classes.newInstance(clacc));
//         } catch (ClassNotFoundException cnfe) {
//             // The normal scenario; carry on
//         }

        startup.run();

        final TransactionContext tcontext = SessionManager.getSession
            ().getTransactionContext();

        tcontext.beginTxn();

        KeyStorage.KERNEL_KEY_STORE.init();
        loadWebDev();

        tcontext.commitTxn();
    }

    private static Session makeSession(final String jdbc) {
        final String pdl = "/com/arsdigita/runtime/Initializer.pdl";
        final MetadataRoot root = new MetadataRoot();
        final PDLCompiler compiler = new PDLCompiler();

        compiler.parse
            (new InputStreamReader
             (TroikaRickshawUpgrade2.class.getResourceAsStream(pdl)), pdl);
        compiler.emit(root);

        final DedicatedConnectionSource source =
            new DedicatedConnectionSource(jdbc);

        return SessionManager.open("upgrade", root, source);
    }

    private static void loadWebDev() {
        PackageType packType;
        try {
            packType = PackageType.findByKey("webdev-support");
        } catch (DataObjectNotFoundException e) {
            // Add the package type to the database
            packType = PackageType.create
                ("webdev-support", "WebDeveloper Support", "WebDeveloper Supports",
                 "http://arsdigita.com/webdev-support");
        }
        SiteNode node;
        try {
            node = SiteNode.getSiteNode("ds");
        } catch (DataObjectNotFoundException e) {
            // Add the node and the package instance on that node.
            node = SiteNode.createSiteNode("ds");
            // Specify the URL stub for this package instance.
            node.mountPackage(packType.createInstance("webdev-support"));
            // Map the package type to a dispatcher class
            packType.setDispatcherClass("com.arsdigita.webdevsupport.Dispatcher");
        }
    }
}
