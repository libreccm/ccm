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
package com.arsdigita.persistence;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * MetaTest
 *
 * @author <a href="mailto:jorris@arsdigita.com"Jon Orris</a>
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 */

public class MetaTest extends PersistenceTestCase {

    
    private static final Logger s_log =
        Logger.getLogger(MetaTest.class.getName());
    static  {
        s_log.setLevel(Level.DEBUG);
    }

    String m_objectTypeName;
    public MetaTest(String objectTypeName) {
        super("testGenericCRUD");
        m_objectTypeName = objectTypeName;
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Party.pdl");
        load("com/arsdigita/persistence/testpdl/mdsql/Order.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperationExtra.pdl");
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        load("com/arsdigita/persistence/testpdl/static/Link.pdl");
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        load("com/arsdigita/persistence/testpdl/static/Party.pdl");
        super.persistenceSetUp();
    }

    public void testGenericCRUD()  throws Exception {
        try {
            ObjectTypeValidator validator = new ObjectTypeValidator();
            validator.performCRUDTest(m_objectTypeName);
        } catch (AbortMetaTestException e) {
            // This happens on postgres when the validator encounters a valid
            // error that on postgres destroys the transaction and keeps the
            // validator from continuing its testing. Eventually we should
            // have a better way of dealing with this.
        }
    }
    public String getName() {
        return super.getName() + ":" + m_objectTypeName;
    }
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new MetaTest("mdsql.Order"));
        suite.addTest(new MetaTest("mdsql.LineItem"));
        suite.addTest(new MetaTest("mdsql.Party"));
        suite.addTest(new MetaTest("mdsql.User"));
        suite.addTest(new MetaTest("mdsql.Group"));
        suite.addTest(new MetaTest("mdsql.Node"));
        suite.addTest(new MetaTest("linkTest.Article"));
        //        suite.addTest(new MetaTest("linkTest.ArticleImageLink"));
        suite.addTest(new MetaTest("linkTest.Image"));
        suite.addTest(new MetaTest("examples.Node"));
        suite.addTest(new MetaTest("examples.Order"));
        //        suite.addTest(new MetaTest("examples.LineItem"));
        suite.addTest(new MetaTest("examples.Party"));


        suite.addTest(new MetaTest("examples.User"));
        suite.addTest(new MetaTest("examples.Group"));
        suite.addTest(new MetaTest("examples.Datatype"));
        return suite;
    }

}
