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
package com.arsdigita.globalization;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * <p>
 * Test for Charset DomainObject
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class CharsetTest extends BaseTestCase {
    

    /**
     * Construct a CharsetTest with the specified name.
     *
     * @param name
     */
    public CharsetTest(String name) {
        super(name);
    }

    /**
     * Test persistence of Charset DomainObject.
     */
    public void testPersistence() throws Exception {
        String charsetName = "YON-XXXX";

        // create a Charset object and save it to the database.
        Charset charset = new Charset();
        charset.setCharset(charsetName);
        charset.save();

        OID charsetOID = charset.getOID();

        // retrieve the Charset object we just created and make sure
        // all values are what we expect.
        try {
            charset = new Charset(charsetOID);
        } catch (DataObjectNotFoundException e) {
            fail("the character set was not created properly or could not be retrieved");
        }

        assertEquals(
                     "Character sets don't match",
                     charsetName,
                     charset.getCharset()
                     );
    }
}
