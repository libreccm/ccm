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
package com.arsdigita.persistence.pdl;

import com.redhat.persistence.metadata.Link;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.PDL;
import java.io.FileReader;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * AssociationMetadataTest
 *
 * @author <a href="mailto:ashah@redhat.com">ashah@redhat.com</a>
 * @version $Id: AssociationMetadataTest.java 745 2005-09-02 10:50:34Z sskracic $
 **/

public class AssociationMetadataTest extends TestCase {

    private static Logger s_log =
        Logger.getLogger(AssociationMetadataTest.class);

    public AssociationMetadataTest(String name) {
        super(name);
    }

    private Root m_root;
    private ObjectType m_ot1;
    private ObjectType m_ot2;
    private ObjectType m_ot3;

    private static final String FILE =
        "test/pdl/com/arsdigita/persistence/Association.pdl";

    protected void setUp() throws Exception {
        super.setUp();
        m_root = new Root();
        PDL pdl = new PDL();
        pdl.load(new FileReader(FILE), FILE);
        pdl.emit(m_root);
        m_ot1 = m_root.getObjectType("Association.Obj1");
        m_ot2 = m_root.getObjectType("Association.Obj2");
        m_ot3 = m_root.getObjectType("Association.Obj3");
    }

    public void testLinkAttribute1() {
        Link l = (Link) m_ot1.getProperty("obj2");
        assertEquals(m_ot2, l.getTo().getType());

        verifyLink(l);

        assertFalse(l.isCollection());
        assertFalse(l.isNullable());
        assertFalse(l.isComponent());
    }

    public void testLinkAttribute2() {
        Link l = (Link) m_ot1.getProperty("obj3");
        assertEquals(m_ot3, l.getTo().getType());

        verifyLink(l);

        assertTrue(l.isCollection());
        assertTrue(l.isNullable());
        assertTrue(l.isComponent());
    }

    private void verifyLink(Link l) {
        // collection and nullability should match
        assertEquals
            (l.isCollection(), l.getFrom().getReverse().isCollection());
        assertEquals(l.isNullable(), l.getFrom().getReverse().isNullable());

        // both reverses should be components
        assertTrue(l.getTo().getReverse().isComponent());
        assertTrue(l.getFrom().getReverse().isComponent());

        // both to and from should be nonnullable, noncollections
        assertFalse(l.getTo().isNullable());
        assertFalse(l.getTo().isCollection());
        assertFalse(l.getFrom().isCollection());
        assertFalse(l.getFrom().isNullable());

        // component should propagate
        assertEquals(l.isComponent(), l.getTo().isComponent());
    }

    public void testComposite() {
        Role r = (Role) m_ot1.getProperty("test");
        assertTrue(r.isComponent());
    }
}
