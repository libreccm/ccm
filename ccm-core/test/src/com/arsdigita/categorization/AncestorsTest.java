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
package com.arsdigita.categorization;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

public class AncestorsTest extends CategoryTestCase {
    private final static Logger s_log = Logger.getLogger(AncestorsTest.class);

    private Category m_parent;
    private Category m_child;
    private Category m_toddler;
    private BigDecimal m_parentID;
    private BigDecimal m_childID;
    private BigDecimal m_toddlerID;

    public AncestorsTest(String name) {
        super(name);
    }

    public void setUp() {
        m_parent = new Category();
        m_parent.setName("Parent");
        m_parentID = m_parent.getID();

        m_child = new Category();
        m_child.setName("Child");
        m_childID = m_child.getID();

        m_toddler = new Category();
        m_toddler.setName("M_Toddler");
        m_toddlerID = m_toddler.getID();
    }

    public void testAncestors() {
        m_parent.save();
        m_child.save();
        m_toddler.save();

        validatePath(m_parentID, new BigDecimal[] { m_parentID });
        validatePath(m_childID, new BigDecimal[] { m_childID });
        validatePath(m_toddlerID, new BigDecimal[] { m_toddlerID });

        m_child.addChild(m_toddler);
        m_toddler.setDefaultParentCategory(m_child);
        m_child.save();
        m_toddler.save();

        validatePath(m_parentID, new BigDecimal[] { m_parentID });
        validatePath(m_childID, new BigDecimal[] { m_childID });
        validatePath(m_toddlerID, new BigDecimal[] { m_childID, m_toddlerID });

        m_parent.addChild(m_child);
        m_parent.save();
        m_child.save();

        validatePath(m_parentID, new BigDecimal[] { m_parentID });
        validatePath(m_childID, new BigDecimal[] { m_childID });
        validatePath(m_toddlerID, new BigDecimal[] { m_childID, m_toddlerID });

        m_child.setDefaultParentCategory(m_parent);
        m_parent.save();
        m_child.save();

        validatePath(m_parentID, new BigDecimal[] { m_parentID });
        validatePath(m_childID, new BigDecimal[] { m_parentID, m_childID });
        validatePath(m_toddlerID, new BigDecimal[] { m_parentID, m_childID, m_toddlerID });

        m_parent.removeChild(m_child);
        m_parent.save();
        m_child.save();

        validatePath(m_parentID, new BigDecimal[] { m_parentID });
        validatePath(m_childID, new BigDecimal[] { m_childID });
        validatePath(m_toddlerID, new BigDecimal[] { m_childID, m_toddlerID });

        m_child.removeChild(m_toddler);
        m_child.save();
        m_toddler.save();
        
        validatePath(m_parentID, new BigDecimal[] { m_parentID });
        validatePath(m_childID, new BigDecimal[] { m_childID });
        validatePath(m_toddlerID, new BigDecimal[] { m_toddlerID });
    }


    private void validatePath(BigDecimal id,
                              BigDecimal[] path) {
        // Messed up part Java / part SQL denormalizatio
        // means we can't rely on the Category dataobject
        // having correct value, so fetch direct from DB.
        DataQuery anc = SessionManager.getSession().retrieveQuery("com.arsdigita.categorization.fetchAncestors");
        anc.setParameter("id", id);
        
        assertTrue(anc.next());
        
        String actual = (String)anc.get("ancestors");
        anc.close();
        
        StringBuffer expected = new StringBuffer();
        for (int i = 0 ; i < path.length ; i++) {
            expected.append(path[i] + "/");
        }
        
        s_log.debug("Compare " + expected + " to " + actual);

        assertEquals(expected.toString(),
                     actual);
    }
}
