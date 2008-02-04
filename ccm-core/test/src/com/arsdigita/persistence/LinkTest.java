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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * LinkTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 **/

public abstract class LinkTest extends PersistenceTestCase {

    public final static String versionId = "$Id: LinkTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log =
        Logger.getLogger(LinkTest.class.getName());

    public LinkTest(String name) {
        super(name);
    }

    abstract String getModel();

    public void testArticle() {
        Session ssn = SessionManager.getSession();
        DataObject article = ssn.create(getModel() + ".Article");
        article.set("id", BigInteger.ZERO);
        String text = "This is the article text.";
        article.set("text", text);
        article.save();

        OID oid = new OID(getModel() + ".Article", BigInteger.ZERO);

        article = ssn.retrieve(oid);
        assertEquals("incorrect id", BigInteger.ZERO, article.get("id"));
        assertEquals("incorrect text", text, article.get("text"));

        article.delete();

        assertEquals("article not deleted properly", null, ssn.retrieve(oid));
    }

    public void testImage() {
        Session ssn = SessionManager.getSession();
        DataObject image = ssn.create(getModel() + ".Image");
        image.set("id", BigInteger.ZERO);
        byte[] bytes = "This is the image.".getBytes();
        image.set("bytes", bytes);
        image.save();

        OID oid = new OID(getModel() + ".Image", BigInteger.ZERO);

        image = ssn.retrieve(oid);
        assertEquals("incorrect id", BigInteger.ZERO, image.get("id"));
        assertTrue("incorrect image",
                   Arrays.equals(bytes, (byte[])image.get("bytes")));

        image.delete();

        assertEquals("image not deleted properly", null, ssn.retrieve(oid));
    }

    public void testArticleImageLink() {
        Session ssn = SessionManager.getSession();
        DataObject article = ssn.create(getModel() + ".Article");
        article.set("id", BigInteger.ZERO);
        String text = "This is the article text.";
        article.set("text", text);

        for (int i = 0; i < 10; i++) {
            DataObject image = ssn.create(getModel() + ".Image");
            image.set("id", new BigInteger(Integer.toString(i)));
            byte[] bytes = "This is the image.".getBytes();
            image.set("bytes", bytes);
            image.save();
        }

        DataAssociation links = (DataAssociation) article.get("images");
        DataCollection images = ssn.retrieve(getModel() + ".Image");
        while (images.next()) {
            DataObject image = images.getDataObject();
            DataObject link = ssn.create(getModel() + ".ArticleImageLink");
            link.set("article", article);
            link.set("image", image);
            link.set("caption", "The caption for: " + image.getOID());
            links.add(link);
        }

        article.save();

        DataAssociationCursor cursor = links.cursor();
        assertEquals(10, cursor.size());

        DataCollection aiLinks = ssn.retrieve(getModel()+".ArticleImageLink");
        aiLinks.addEqualsFilter("image.id", new BigDecimal(5));
        if (aiLinks.next()) {
            DataObject linkArticle = (DataObject) aiLinks.get("article");
            DataObject linkImage = (DataObject) aiLinks.get("image");
            String caption = (String) aiLinks.get("caption");
            assertEquals(BigInteger.valueOf(0), linkArticle.get("id"));
            assertEquals(BigInteger.valueOf(5), linkImage.get("id"));

            if (aiLinks.next()) { fail("too many rows"); }
        } else {
            fail("no rows returned");
        }

        article.delete();
    }
}
