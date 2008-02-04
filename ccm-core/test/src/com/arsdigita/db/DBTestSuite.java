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
 *
 */
package com.arsdigita.db;

import com.arsdigita.persistence.DedicatedConnectionSource;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.extensions.TestDecorator;
import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 * @author Jon Orris
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 */
public class DBTestSuite extends PackageTestSuite {
    public final static String versionId = "$Id: DBTestSuite.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static Test suite() {
        DBTestSuite suite = new DBTestSuite();
        populateSuite(suite);
        //BaseTestSetup wrapper = new CoreTestSetup(suite);
        //return wrapper;
        TestDecorator sessionSetup = new TestDecorator(suite) {
            public void run(final TestResult result) {
                final Protectable p = new Protectable() {
                    public void protect() throws Exception {
                        final String key = "default";
                        String url = RuntimeConfig.getConfig().getJDBCURL();
                        final MetadataRoot root = MetadataRoot.getMetadataRoot();
                        SessionManager.configure(key, root, new DedicatedConnectionSource(url));

                        basicRun(result);
                    }

                };

                result.runProtected(this, p);
            }
        };
        return sessionSetup;
    }

}
