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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * Represents a mapping from (content item + use context type) to a
 * template.  This class is package scope since it is part of the
 * internal templating implementation.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemTemplateMapping.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ItemTemplateMapping extends TemplateMapping {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ItemTemplateMapping";

    public static final String ITEM         = "item";
    public static final String TEMPLATE     = TemplateCollection.TEMPLATE;
    public static final String USE_CONTEXT  = TemplateCollection.USE_CONTEXT;

    private static final String CTX_QUERY_NAME =
        "com.arsdigita.cms.templatesInUseContexts";
    private static final String CTX_TYPE_QUERY_NAME =
        "com.arsdigita.cms.templatesInUseContextsWithType";
    private static final String CTX_QUERY_ITEM_ID = "itemID";
    private static final String CTX_QUERY_MAPPING = "mapping";
    private static final String CTX_MIME_TYPES_ID = "availableMimeTypes";

    // Default constructor
    public ItemTemplateMapping() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    // OID constructor
    public ItemTemplateMapping(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    // ID constructor
    public ItemTemplateMapping(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    // DataObject constructor
    public ItemTemplateMapping(DataObject obj) {
        super(obj);
    }

    // Subtype constructor
    public ItemTemplateMapping(String type) {
        super(type);
    }

    public ContentSection getContentSection() {
        return getContentItem().getContentSection();
    }

    public final ContentItem getContentItem() {
        return (ContentItem)DomainObjectFactory.newInstance
            ((DataObject) get(ITEM));
    }

    public final void setContentItem(ContentItem item) {
        Assert.exists(item);
        setAssociation(ITEM, item);
    }

    public ACSObject getParent() {
        return getContentItem();
    }

    public Boolean isDefault() {
        return Boolean.TRUE;
    }

    public void setDefault(Boolean b) {
        // Do nothing
    }

    /**
     * Load the specified mapping; return null if no such mapping
     * exists
     * @deprecated use getMapping(ContentItem item, String useContext, MimeType mimeType)
     */
    public static ItemTemplateMapping getMapping(ContentItem item, 
                                                 String useContext) {
        return getMapping(item, useContext, 
                          MimeType.loadMimeType(Template.JSP_MIME_TYPE));
    }

    /**
     * Load the specified mapping; return null if no such mapping
     * exists
     */
    public static ItemTemplateMapping getMapping(ContentItem item, 
                                                 String useContext, 
                                                 MimeType mimeType) {
        ItemTemplateCollection c = getTemplates(item);
        c.addEqualsFilter(USE_CONTEXT, useContext);
        if (mimeType != null) {
            c.addEqualsFilter(TemplateCollection.TEMPLATE + "." + 
                              Template.MIME_TYPE + "." + MimeType.MIME_TYPE, 
                              mimeType.getMimeType());
        } 
        if(!c.next()) return null;
        ItemTemplateMapping m = (ItemTemplateMapping)c.getDomainObject();
        // FIXME: There HAS to be a better way to enforce uniqueness here...
        Assert.isTrue(!c.next());
        c.close();
        return m;
    }

    public static ItemTemplateMapping getMapping(ContentItem item, 
                                                 String useContext, 
                                                 Template template) {
        ItemTemplateCollection c = getTemplates(item);
        c.addEqualsFilter(USE_CONTEXT, useContext);
        c.addEqualsFilter(TEMPLATE + "." + Template.ID, template.getID());
        if(!c.next()) return null;
        ItemTemplateMapping m = (ItemTemplateMapping)c.getDomainObject();
        c.close();
        return m;        
    }

    /**
     * Get the template for the item in the specified use context. Return
     * null if no such template exists
     * @deprecated use getTemplate(ContentItem item, String useContext, MimeType mimeType)
     */
    protected static Template getTemplate(ContentItem item, String useContext) {
        return getTemplate(item, useContext, 
                           MimeType.loadMimeType(Template.JSP_MIME_TYPE));
    }

    /**
     * Get the template for the item in the specified use context. Return
     * null if no such template exists
     */
    protected static Template getTemplate(ContentItem item, String useContext, 
                                          MimeType mimeType) {
        ItemTemplateMapping m = getMapping(item, useContext, mimeType);
        if(m == null) return null;
        return m.getTemplate();
    }

    /**
     * Retrieve all templates for the given content item,
     * along with their use context
     */
    protected static ItemTemplateCollection getTemplates(ContentItem item) {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        ItemTemplateCollection c = new ItemTemplateCollection(da);
        c.addEqualsFilter(ITEM + "." + ACSObject.ID, item.getID());
        c.addOrder(USE_CONTEXT);
        return c;
    }

    /**
     * Retrieve all use contexts along with possibly null template
     * for the item in that use context.  
     */
    protected static ItemTemplateCollection getUseContexts(ContentItem item) {
        DataQuery q = SessionManager.getSession().retrieveQuery(CTX_QUERY_NAME);
        q.setParameter(CTX_QUERY_ITEM_ID, item.getID());
        // this query is an outer join and when the ItemTemplateMapping is null
        // we want get("useContext") to return a nonnull value, so the query
        // explicitly fetches a useContext property
        q.alias("useContext", "useContext");

        DataQueryDataCollectionAdapter da =
            new DataQueryDataCollectionAdapter(q, CTX_QUERY_MAPPING);

        return new ItemTemplateCollection(da);
    }

    /**
     * Retrieve all use context/mime type combinations along with
     * possibly null template for the item in that pair.  This
     * returns one row for each use context/mime type pair.  So, you
     * could get 4 rows if there is public/jsp, public/xsl,
     * alternate/jsp, and alternate/xsl.  This is because there can be
     * one template per context/mime-type pair
     */
    protected static ItemTemplateCollection getContextsWithTypes(ContentItem item) {
        DataQuery q = 
            SessionManager.getSession().retrieveQuery(CTX_TYPE_QUERY_NAME);
        q.setParameter(CTX_QUERY_ITEM_ID, item.getID());
        q.setParameter(CTX_MIME_TYPES_ID, 
                       Template.SUPPORTED_MIME_TYPES.keySet());
        // this query is an outer join and when the ItemTemplateMapping is null
        // we want get("useContext") to return a nonnull value, so the query
        // explicitly fetches a useContext property
        q.alias("useContext", "useContext");
        q.alias("mimeType", "mimeType");

        q.addOrder("useContext");
        DataQueryDataCollectionAdapter da =
            new DataQueryDataCollectionAdapter(q, CTX_QUERY_MAPPING);

        return new ItemTemplateCollection(da);
    }

    /**
     * Retrieve all content items for the given template,
     * along with their use context
     */
    protected static ItemTemplateCollection getItems(Template template) {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        ItemTemplateCollection c = new ItemTemplateCollection(da);
        c.addEqualsFilter(TEMPLATE + "." + ACSObject.ID, template.getID());
        c.addOrder(USE_CONTEXT);
        return c;
    }

}
