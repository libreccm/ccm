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

import java.math.BigInteger;

/**
 * LinkAttributeTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 */

public class DynamicLinkAttributeTest extends LinkAttributeTest {

    

    public DynamicLinkAttributeTest(String name) {
        super(name);
    }

    String getModelName() {
        return "mdsql";
    }

    public void testReferenceLinkAttribute() {
        Session ssn = SessionManager.getSession();
        DataObject user = ssn.create(getModelName() + ".User");
        user.set("id", BigInteger.valueOf(0));
        user.set("email", "foo@bar.com");
        user.set("firstName", "foo");
        user.set("lastNames", "bar");
        user.save();

        DataObject[] images = new DataObject[2];
        for (int i = 0; i < images.length; i++) {
            images[i] = ssn.create(getModelName() + ".Image");
            images[i].set("id", BigInteger.valueOf(i));
            byte[] bytes = "This is the image.".getBytes();
            images[i].set("bytes", bytes);
            images[i].save();
        }

        // set image
        user.set("image", images[0]);
        user.save();

        // retrieve and then update caption
        DataAssociationCursor dac =
            ((DataAssociation) images[0].get("users")).cursor();
        dac.next();
        assertNull(dac.getLinkProperty("caption"));
        assertEquals(user, dac.getDataObject());
        DataObject link = dac.getLink();
        link.set("caption", "caption");
        link.save();

        dac = ((DataAssociation) images[0].get("users")).cursor();
        dac.next();
        assertEquals("caption", dac.getLinkProperty("caption"));
        assertEquals(1L, ((DataAssociation) images[0].get("users")).size());

        // set new image as image
        user.set("image", images[1]);
        user.save();


        // check that old image is no longer associated with user
        assertEquals(0L, ((DataAssociation) images[0].get("users")).size());

        // check that new image is associated with user and has no caption
        dac = ((DataAssociation) images[1].get("users")).cursor();
        dac.next();
        assertNull(dac.getLinkProperty("caption"));
        assertEquals(1L, ((DataAssociation) images[1].get("users")).size());
    }

    public void testDeepLink() {
        Session ssn = SessionManager.getSession();

        DataObject[] users = new DataObject[4];

        DataObject group = getSession().create(getModelName() + ".Group");
        group.set("id", BigInteger.valueOf(users.length));
        group.set("email", "sipb@mit.edu");
        group.set("name", "SIPB");
        group.save();
        DataAssociation members = (DataAssociation) group.get("members");

        for (int i = 0; i < users.length; i++) {
            users[i] = ssn.create(getModelName() + ".User");
            users[i].set("id", BigInteger.valueOf(i));
            users[i].set("email", "foo@bar.com");
            users[i].set("firstName", "foo");
            users[i].set("lastNames", "bar");
            users[i].save();
            members.add(users[i]);
        }
        group.save();

        DataObject[] images = new DataObject[users.length/2];
        for (int i = 0; i < images.length; i++) {
            images[i] = ssn.create(getModelName() + ".Image");
            images[i].set("id", BigInteger.valueOf(i));
            byte[] bytes = "This is the image.".getBytes();
            images[i].set("bytes", bytes);
            images[i].save();
        }

        // create link between user i and image i/2 with caption i
        for (int i = 0; i < users.length; i++) {
            // set image
            DataAssociation imageUsers =
                (DataAssociation) images[i/2].get("users");
            DataObject link = imageUsers.add(users[i]);
            link.set("caption", String.valueOf(i));
            link.save();
        }

        DataCollection dc = ssn.retrieve(getModelName() + ".Group");
        dc.addEqualsFilter("members.image.link.caption", "0");
        assertEquals(1, dc.size());

        dc = ssn.retrieve(getModelName() + ".User");
        dc.addPath("image.link.caption");
        assertEquals(users.length, dc.size());
        while (dc.next()) {
            assertEquals
                (dc.get("id").toString(), dc.get("image.link.caption"));
        }

        dc = ssn.retrieve(getModelName() + ".User");
        dc.addPath("image.id");
        assertEquals(users.length, dc.size());
        while(dc.next()) {
            int id = ((BigInteger) dc.get("id")).intValue();
            assertEquals(BigInteger.valueOf(id/2), dc.get("image.id"));
        }

        DataCollection dcUp = ssn.retrieve(getModelName() + ".User");
        DataCollection dcDown = ssn.retrieve(getModelName() + ".User");
        dcUp.addOrder("image.link.caption asc");
        dcDown.addOrder("image.link.caption desc");
        dcUp.next();
        dcDown.next();
        assertEquals(BigInteger.valueOf(0), dcUp.get("id"));
        assertEquals(BigInteger.valueOf(users.length - 1), dcDown.get("id"));
        dcUp.close();
        dcDown.close();

        dcUp = ssn.retrieve(getModelName() + ".Image");
        dcDown = ssn.retrieve(getModelName() + ".Image");
        dcUp.addOrder("users.link.caption asc");
        dcDown.addOrder("users.link.caption desc");
        dcUp.next();
        dcDown.next();
        assertEquals(BigInteger.valueOf(0), dcUp.get("id"));
        assertEquals(BigInteger.valueOf(images.length - 1), dcDown.get("id"));
        dcUp.close();
        dcDown.close();

        dc = ssn.retrieve(getModelName() + ".Group");
        dc.addFilter("members.image.id = 0");
        assertEquals(2, dc.size());

        dc = ssn.retrieve(getModelName() + ".Image");
        dc.addFilter("users.id = 0 and users.link.caption = '1'");
        assertEquals(0, dc.size());

        dc = ssn.retrieve(getModelName() + ".Group");
        dc.addPath("members.id");
        dc.addFilter
            ("members.image.id = 0 and members.image.link.caption = '1'");
        assertTrue(dc.next());
        assertEquals(BigInteger.valueOf(1), dc.get("members.id"));
        assertFalse(dc.next());
    }
}
