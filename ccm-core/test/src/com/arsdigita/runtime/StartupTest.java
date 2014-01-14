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
package com.arsdigita.runtime;

import com.arsdigita.persistence.DedicatedConnectionSource;
import com.arsdigita.persistence.Initializer;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import junit.framework.TestCase;

public class StartupTest extends TestCase {

    public StartupTest(String name) {
        super(name);
    }

    public static void testHasRun() {
        final String key = "test";
        String url = RuntimeConfig.getConfig().getJDBCURL();
        final MetadataRoot root = MetadataRoot.getMetadataRoot();
        SessionManager.configure(key, root, new DedicatedConnectionSource(url));
        final Session session = SessionManager.getSession(key);

        Startup.run(session, new Initializer() {
                public void init(DataInitEvent e) { }
                public void init (DomainInitEvent e) {
                    // Must be true, because of the session that this initializer is
                    // running in.
                    assertTrue(Startup.hasRun());
                }
                public void init(LegacyInitEvent e) { }
            });

    }
}
