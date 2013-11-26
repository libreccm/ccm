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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.versioning.VersionedACSObject;
import java.math.BigDecimal;


import org.apache.log4j.Logger;

/**
 * <p>An {@link com.arsdigita.cms.Asset asset} representing a reusable
 * image.</p>
 *
 * @see com.arsdigita.cms.ImageAsset
 *
 * @author Scott Seago (sseago@redhat.com)
 * @author Stanislav Freidin
 *
 * @version $Id: ReusableImageAsset.java 2218 2011-06-22 23:55:36Z pboy $
 */
public class ReusableImageAsset extends ImageAsset {

    private static final Logger s_log =
                                Logger.getLogger(ReusableImageAsset.class);
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.ReusableImageAsset";

    /**
     * Default constructor. This creates a new image asset.
     */
    public ReusableImageAsset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     */
    public ReusableImageAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>ReusableImageAsset.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ReusableImageAsset(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ReusableImageAsset(DataObject obj) {
        super(obj);
    }

    public ReusableImageAsset(String type) {
        super(type);
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
     * Retrieve all images in the database. Expensive operation.
     *
     * @return a collection of ReusableImageAssets
     */
    public static ImageAssetCollection getAllReusableImages() {
        DataCollection da = SessionManager.getSession().retrieve(
                BASE_DATA_OBJECT_TYPE);
        //da.addEqualsFilter(VersionedACSObject.IS_DELETED, new Integer(0));
        //da.addEqualsFilter(ACSObject.OBJECT_TYPE, BASE_DATA_OBJECT_TYPE);
        da.addFilter(String.format("%s = '%s'",
                                   VersionedACSObject.IS_DELETED,
                                   "0"));
        da.addFilter(String.format("%s = '%s'",
                                   ACSObject.OBJECT_TYPE,
                                   BASE_DATA_OBJECT_TYPE));
        return new ImageAssetCollection(da);
    }

    /**
     * Find all images whose name matches the specified keyword
     *
     * @param keyword a String keyword
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @return a collection of images whose name matches the keyword
     */
    public static ImageAssetCollection getReusableImagesByKeyword(
            String keyword, String context) {
        ImageAssetCollection c = getAllReusableImages();
        c.addOrder(Asset.NAME);
        Filter f;
        if (!(keyword == null || keyword.length() < 1)) {
            f = c.addFilter("lower(name) like lower(\'%\' || :keyword || \'%\')");
            f.set("keyword", keyword);            
        }
        f = c.addFilter("version = :version");
        f.set("version", context);
        return c;
    }

    /**
     * Find all images whose name matches the specified keyword
     *
     * @param keyword a String keyword
     * @return a collection of images whose name matches the keyword
     */
    public static ImageAssetCollection getReusableImagesByKeyword(String keyword) {
        return getReusableImagesByKeyword(keyword, ContentItem.DRAFT);
    }
}
