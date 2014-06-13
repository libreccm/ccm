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
package com.arsdigita.cms;

import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * This class extends {@link com.arsdigita.cms.ContentItem content item} with
 * the additional attributes name, title and description. The name attribute is
 * used in generating the URL for this content page.
 * It is the base class for any document-type of content, i.e. content bearing a
 * title, name and description/abstract (in con trast to assets or special
 * content as Contact or internal type as folder).
 * 
 * ContentPage is a bit of missleading, more adaquat would have been
 * ContentDocument.
 *
 * @author Uday Mathur
 * @author Jack Chung
 * @author Michael Pih
 * @version $Id: ContentPage.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class ContentPage extends ContentItem {

    private static final Logger s_log = Logger.getLogger(ContentPage.class);

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.ContentPage";

    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String LAUNCH_DATE = "launchDate";
    public static final String DESCRIPTION = "pageDescription";

    protected static final String PAGES_IN_FOLDER = "com.arsdigita.cms.pagesInFolder";
    protected static final String PAGES_IN_CATEGORY = "com.arsdigita.cms.pagesInFolderByCategory";
    public static final String QUERY_PAGE = "page";
    public static final String QUERY_TYPE = "type";
    public static final String QUERY_ROOT_ID = "rootFolderID";
    public static final String QUERY_CATEGORY_ID = "categoryID";

    private String m_newName = null;

    /**
     * Default constructor. This creates a new content page.
     *
     */
    public ContentPage() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved from the persistent storage
     * mechanism with an <code>OID</code> specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved <code>DataObject</code>.
     *
     */
    public ContentPage(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved from the persistent storage
     * mechanism with an <code>OID</code> specified by <i>id</i> and
     * <code>ContentPage.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved <code>DataObject</code>.
     *
     */
    public ContentPage(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ContentPage(DataObject obj) {
        super(obj);
    }

    public ContentPage(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes should override this method to
     *         return the correct value.
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getDisplayName() {
        final String result = getTitle();

        if (result == null) {
            return super.getDisplayName();
        } else {
            return result;
        }
    }

    /**
     * Sets the name of the content page. If the parent of the page is a <code>ContentBundle</code>
     * this method sets the bundle's name as well.
     * 
     * @param name The new name of the item
     */
    @Override
    public void setName(final String name) {
        super.setName(name);

        final ContentBundle bundle = getContentBundle();

        if (bundle != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug(this + " is inside a bundle; setting the " + "bundle's name to " + name
                            + " as well");
            }

            bundle.setName(name);
            bundle.save();
        }
    }

    /**
     * Decides whether plugged in assets should be indexed with the page or separately. Default is
     * false (assets are ONLY indexed as separate items) Subtypes may override this method to
     * provide the logic that decides whether or not assets should be indexed separately. If true,
     * content of assets is ONLY included in the page, not as a separate item. This method only
     * provides guidance to assets. Writers of plug in assets should refer to this method in
     * relation to the object that owns the asset and make a decision based on the answer.
     *
     * Best course of action is for assets to provide their own MetadataProvider which refers to
     * this method in the indexObject method eg.
     *
     *
     *
     * <pre>
     * public boolean indexObject (DomainObject dobj) {
     * FileAttachment file = (FileAttachment) dobj;
     * ContentPage owner = (ContentPage) file.getFileOwner();
     * s_log.debug("index this file attachment? " + !owner.indexAssetsWithPage());
     * return !owner.indexAssetsWithPage();
     * }
     * </pre>
     *
     * @return
     */
    public boolean indexAssetsWithPage() {
        return false;
    }

    public final ContentBundle getContentBundle() {
        final ACSObject parent = getParent();

        if (parent instanceof ContentBundle) {
            return (ContentBundle) parent;
        } else {
            return null;
        }
    }

    public String getTitle() {
        return (String) get(TITLE);
    }

    public void setTitle(String value) {
        set(TITLE, value);
    }

    public Date getLaunchDate() {
        return (Date) get(LAUNCH_DATE);
    }

    public void setLaunchDate(Date ldate) {
        set(LAUNCH_DATE, ldate);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    // Set parameters on the pagesInFolder data query
    /**
     * @deprecated This doesn't filter its results based on the permissions of the current user. Use
     * <code>setPagesQueryParameters( String name, ContentSection s, String context, OID userOID )</code>
     * instead.
     */
    protected static DataQuery setPagesQueryParameters(String name,
                                                       ContentSection s,
                                                       String context) {
        DataQuery q = SessionManager.getSession().retrieveQuery(name);
        Folder root;
        Folder f = s.getRootFolder();
        if (ContentItem.LIVE.equals(context)) {
            root = (Folder) f.getLiveVersion();
            if (root == null) {
                // A hack to make sure we still return a valid DataQuery; the query
                // will have no rows
                root = f;
            }
        } else {
            root = f;
        }
        q.setParameter(QUERY_ROOT_ID, root.getID());
        q.setParameter(ContentItem.VERSION, context);
        return q;
    }

    protected static DataQuery setPagesQueryParameters(String name,
                                                       ContentSection s,
                                                       String context,
                                                       OID userOID) {
        DataQuery q = setPagesQueryParameters(name, s, context);

        FilterFactory ff = q.getFilterFactory();
        PrivilegeDescriptor pd = PrivilegeDescriptor.get(SecurityManager.CMS_READ_ITEM);

        Filter f = PermissionService.getFilterQuery(ff, "page.id", pd,
                                                    userOID);
        q.addFilter(f);

        return q;
    }

    /**
     * Retrieve all pages within the given content section that belong to the given category
     *
     * @param s       the section
     * @param context if {@link ContentItem#LIVE}, retrieve only live items. If
     *                {@link ContentItem#DRAFT}, return only draft items
     * @param cat     the category
     *
     * @return a DataQuery of all then pages within a section that belong to the given category
     *
     * @deprecated This doesn't filter its results based on the permissions of the current user. Use
     * <code>getPagesInSectionQuery( ContentSection s, String context, Category cat, OID userOID )</code>
     * instead.
     */
    public static DataQuery getPagesInSectionQuery(ContentSection s,
                                                   String context,
                                                   Category cat) {
        DataQuery q = setPagesQueryParameters(PAGES_IN_CATEGORY, s, context);
        q.setParameter(QUERY_CATEGORY_ID, cat.getID());
        return q;
    }

    /**
     * Retrieve all pages within the given content section that belong to the given category
     *
     * @param s       the section
     * @param context if {@link ContentItem#LIVE}, retrieve only live items. If
     *                {@link ContentItem#DRAFT}, return only draft items
     * @param cat     the category
     * @param userOID the OID of the current user
     *
     * @return a DataQuery of all then pages within a section that belong to the given category
     *         which the current user has permission to view
     */
    public static DataQuery getPagesInSectionQuery(ContentSection s,
                                                   String context,
                                                   Category cat,
                                                   OID userOID) {
        DataQuery q = setPagesQueryParameters(PAGES_IN_CATEGORY, s,
                                              context, userOID);
        q.setParameter(QUERY_CATEGORY_ID, cat.getID());
        return q;
    }

    /**
     * Retrieve all pages within the given content section.
     *
     * @param s       the section
     * @param context if {@link ContentItem#LIVE}, retrieve only live items. If
     *                {@link ContentItem#DRAFT}, return only draft items
     *
     * @return a DataQuery of all the pages within the section.
     *
     * @deprecated This doesn't filter its results based on the permissions of the current user. Use      <code>getPagesInSectionQuery( ContentSection s, String context,
     * OID userOID )</code> instead.
     */
    public static DataQuery getPagesInSectionQuery(ContentSection s,
                                                   String context) {
        return setPagesQueryParameters(PAGES_IN_FOLDER, s, context);
    }

    /**
     * Retrieve all pages within the given content section.
     *
     * @param s       the section
     * @param context if {@link ContentItem#LIVE}, retrieve only live items. If
     *                {@link ContentItem#DRAFT}, return only draft items
     * @param userOID the OID of the current user
     *
     * @return a DataQuery of all the pages within the section which the current user has permission
     *         to view
     */
    public static DataQuery getPagesInSectionQuery(ContentSection s,
                                                   String context,
                                                   OID userOID) {
        return setPagesQueryParameters(PAGES_IN_FOLDER, s, context, userOID);
    }

    // These could be protected, but public may make it easier to
    // debug
    /**
     * default behaviour is to return the description. Subtypes should override to change use some
     * other attribute
     */
    public String getSearchSummary() {
        return getDescription();
    }

}
