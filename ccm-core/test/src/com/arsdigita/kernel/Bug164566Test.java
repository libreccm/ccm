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
package com.arsdigita.kernel;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Regression test for <a href='http://developer.arsdigita.com/acs5/sdm/one-ticket?ticket_id=164566'>bug 164566</a>.
 *
 *
 * @author Rob Mayoff
 * @version 1.0
 * @see com.arsdigita.kernel
 */

public class Bug164566Test extends BaseTestCase {


    public Bug164566Test(String name) {
        super(name);
    }

    public void testBug() throws Exception {
        User u = new User();
        u.setScreenName("testuser");
        u.getPersonName().setGivenName("testuser-givenName");
        u.getPersonName().setFamilyName("testuser-familyName");
        u.setPrimaryEmail(new EmailAddress("testuser@example.com"));
        u.save();

        try {
            SiteNode sn = new SiteNode();
            sn = new SiteNode(new OID(
                                      sn.getBaseDataObjectType(), u.getID()));
            fail("did not get expected DataObjectNotFoundException");
        }

        catch (DataObjectNotFoundException e) {
        }
    }

}
