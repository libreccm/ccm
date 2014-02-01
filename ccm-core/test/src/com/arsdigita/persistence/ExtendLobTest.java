/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * ExtendLobTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class ExtendLobTest extends PersistenceTestCase {

    

    private static final Logger LOG = Logger.getLogger(ExtendLobTest.class);

    private static final String EXTEND_LOB =
        "com.arsdigita.persistence.ExtendLob";

    public ExtendLobTest(String name) {
        super(name);
    }

    public void test() {
        Session ssn = SessionManager.getSession();

        DataObject data = ssn.create(new OID(EXTEND_LOB, new BigDecimal("0")));
        data.set("lob", "value");
        data.save();
    }

}
