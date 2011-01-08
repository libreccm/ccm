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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 * A class that represents an Article
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #28 $ $Date: 2004/08/17 $
 * @version $Id: Article.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class GenericArticle extends ContentPage {

    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.contenttypes.GenericArticle";
    public static final String TEXT_ASSET = "textAsset";
    protected static final int SUMMARY_SIZE = 1024;
    private static org.apache.log4j.Logger s_log =
            org.apache.log4j.Logger.getLogger(GenericArticle.class);

    /**
     * Default constructor. This creates a new article.
     **/
    public GenericArticle() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public GenericArticle(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public GenericArticle(String type) {
        super(type);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>Article.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public GenericArticle(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericArticle(DataObject obj) {
        super(obj);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Return the text asset for this <code>GenericArticle</code>. Could return
     * null if there is no text body actually associated with the page
     */
    public TextAsset getTextAsset() {
        DataObject text = (DataObject) get(TEXT_ASSET);
        if (text == null) {
            return null;
        } else {
            return new TextAsset(text);
        }
    }

    /**
     * Pass in a null value to remove the text of this item.
     * Explicitly call text.delete() to remove the text from the database
     */
    public void setTextAsset(TextAsset text) {
        setAssociation(TEXT_ASSET, text);
    }

    /**
     * Return a short summary of the text body for search.
     * This method is WRONG, because the text body could actually
     * be extremely large, and doing substring on it is NOT safe
     */
    public String getSearchSummary() {
        TextAsset a = getTextAsset();

        if (a == null) {
            return "";
        }
        return com.arsdigita.util.StringUtils.truncateString(a.getText(),
                SUMMARY_SIZE,
                true);
    }
}
