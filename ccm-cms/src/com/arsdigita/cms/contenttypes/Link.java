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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import java.math.BigDecimal;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * This content type represents a Link content type for linking
 * ContentItems and external links.
 *
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 * @author Nobuko Asakai (nasakai@redhat.com)
 * @author Sören Bernstein (Quasimodo)
 */
public class Link extends ACSObject {

    private static final Logger s_log = Logger.getLogger(Link.class);
    /** PDL properties
     *  cms_links.title */
    public static final String TITLE = "linkTitle";
    /** PDL property targetType*/
    public static final String TARGET_TYPE = "targetType";
    /** Values for TARGET_TYPE */
    public static final String EXTERNAL_LINK = "externalLink";
    public static final String INTERNAL_LINK = "internalLink";
    /** PDL property "targetURI" */
    public static final String TARGET_URI = "targetURI";
    /** PDL property "targetItem" */
    public static final String TARGET_ITEM = "targetItem";
    /** PDL property "targetWindow" */
    public static final String TARGET_WINDOW = "targetWindow";
    /** PDL property "description" */
    public static final String DESCRIPTION = "linkDescription";
    /** PDL property "order" */
    public static final String ORDER = "linkOrder";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.contenttypes.Link";

    /**
     * Default constructor. This creates a new Link.
     */
    public Link() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <code>id</code> and <code>Link.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>
     */
    public Link(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <code>oid</code>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>
     */
    public Link(OID id)
            throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public Link(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor.  Creates a new <code>Link</code> using the given data
     * object type.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public Link(String type) {
        super(type);
    }

    /**
     * Returns the title of this <code>Link</code>
     *
     * @return The Link title
     */
    public String getTitle() {
        return (String) get(TITLE);
    }

    /**
     * Sets the title of this <code>Link</code>
     *
     * @param title The Link title
     */
    public void setTitle(String title) {
        set(TITLE, title);
    }

    /**
     * Returns the target type of this <code>Link</code>
     *
     * @return The Target Type
     */
    public String getTargetType() {
        return (String) get(TARGET_TYPE);
    }

    /**
     * Sets the Target Type for this Link
     *
     * @param type must be either Link.EXTERNAL_LINK or
     * Link.INTERNAL_LINK
     */
    public void setTargetType(String type) {
        Assert.isTrue(type != null && (type.equals(EXTERNAL_LINK) || type.equals(
                INTERNAL_LINK)));
        set(TARGET_TYPE, type);
    }

    /**
     * Returns the target URI of this <code>Link</code>
     *
     * @return The Target Type
     */
    public String getTargetURI() {
        return (String) get(TARGET_URI);
    }

    /**
     * Sets the target URI of this <code>Link</code>
     *
     * @param uri The Target URI
     */
    public void setTargetURI(String uri) {
        set(TARGET_URI, uri);
    }

    /**
     * Returns the target ContentItem of this <code>Link</code>
     *
     * @return The Target Type
     */
    public ContentItem getTargetItem() {
        DataObject object = (DataObject) get(TARGET_ITEM);
        ACSObject acsObject =
                (ACSObject) DomainObjectFactory.newInstance(object);

        // Quasimodo: BEGIN
        // This is part of the patch to make RelatedLink (and Link) multilanguage compatible
        // Here we have to check if the target item is a content bundle, so we have to negotiate the language
        // If we don't do this, all related links would come back as type ContentBundle'instead of the actual
        // content type
        ContentItem ci;
        // If acsObject is instance of ContentBundle
        if (acsObject instanceof ContentBundle) {
            // get the negotiated language version of this ContentBundle 
            ci = ((ContentBundle) acsObject).negotiate(DispatcherHelper.getRequest().getLocales());
        } else {
            // else there are no language versions so just use the acsObject
            ci = (ContentItem) acsObject;
        }
        // Quasimodo: END
        return ci;
    }

    /**
     * Sets the target ContentItem of this <code>Link</code>
     *
     * @item The Target Item
     */
    public void setTargetItem(ContentItem item) {
        setAssociation(TARGET_ITEM, item);
    }

    /**
     * Returns the target Window of this <code>Link</code>
     *
     * @return The Target Window
     */
    public String getTargetWindow() {
        return (String) get(TARGET_WINDOW);
    }

    /**
     * Sets the target Window of this <code>Link</code>
     *
     * @param window The Target Window
     */
    public void setTargetWindow(String window) {
        set(TARGET_WINDOW, window);
    }

    /**
     * Returns the description for this <code>Link</code>
     *
     * @return the description
     */
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * sets the description for this <code>Link</code>
     *
     * @param description the description
     */
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Returns the link order for this <code>Link</code>
     *
     * @return the link order
     */
    public Integer getOrder() {
        return (Integer) get(ORDER);
    }

    /**
     * Sets the link order for this <code>Link</code>
     *
     * @param order the link order
     */
    public void setOrder(Integer order) {
        Assert.exists(order);
        set(ORDER, order);
    }

    /**
     * Sets the link order for this <code>Link</code>
     *
     * @param order the link order
     */
    public void setOrder(int order) {
        setOrder(new Integer(order));
    }

    /**
     * Returns the link URI as a <code>String</code> whether it is
     * internal or external. Returns the empty string if no target
     * item exists.
     *
     * @param state current <code>PageState</code>
     *
     * @return the Link URI
     */
    public String getInternalOrExternalURI(PageState state) {
        if (EXTERNAL_LINK.equals(getTargetType())) {
            return getTargetURI();
        } else {
            ContentItem item = getTargetItem();

            if (item == null) {
                s_log.error(getOID()
                        + " is internal link, but has null target item");
                return "";
            }

            ContentSection section = item.getContentSection();
            ItemResolver resolver = section.getItemResolver();
            String url = resolver.generateItemURL(
                    state, item, section, item.getVersion());

            if ((getTargetURI() != null) && getTargetURI().startsWith("&")) {
                ParameterMap parameters;
                StringTokenizer tokenizer;

                parameters = new ParameterMap();
                tokenizer = new StringTokenizer(getTargetURI().substring(
                        1), "&");
                while (tokenizer.hasMoreTokens()) {
                    String param[] = tokenizer.nextToken().split("=");
                    if (param.length >= 2) {
                        parameters.setParameter(param[0], param[1]);
                    }
                }

                s_log.debug(
                        String.format(
                        "Internal link with parameters found. Generated URL is: %s",
                        URL.there(state.getRequest(), url,
                        parameters).
                        toString()));
                return URL.there(state.getRequest(), url, parameters).
                        toString();
            } else {
                return URL.there(state.getRequest(), url).toString();
            }
        }
    }

    /**
     * Returns a DataCollection of links which refer to the given
     * item. This method returns all links regardless of context
     * (i.e. RelatedLinks and other links would all be included)
     *
     * @param item The target Item to return links for
     *
     * @return DataCollection of referring Links
     */
    public static DataCollection getReferringLinks(ContentItem item) {
        Session session = SessionManager.getSession();
        DataCollection links = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter =
                links.addInSubqueryFilter("id",
                "com.arsdigita.cms.contenttypes.getReferringLinks");
        filter.set("itemID", item.getID());

        return links;
    }

    /**
     * no arg swapWithNext is not implemented for the base Link
     * class. Provide required implementation in a subclass, or use
     * swapWithNext(String queryName) with a valid query name.
     *
     * @throws UnsupportedOperationException
     */
    public void swapWithNext() {
        throw new UnsupportedOperationException(
                "Not implemented on base Link class. Subclass Link to support this for a specific role");
    }

    /**
     * no arg swapWithPrevious is not implemented for the base Link
     * class. Provide required implementation in a subclass, or use
     * swapWithPrevious(String queryName) with a valid query name.
     *
     * @throws UnsupportedOperationException
     */
    public void swapWithPrevious() {
        throw new UnsupportedOperationException(
                "Not implemented on base Link class. Subclass Link to support this for a specific role");
    }

    /**
     * Swaps this Link with the next Link according to the specified
     * ORDER attribute. No filters are applied to the query, unless
     * getSwapQuery() is overridden, applying relevant filters (such
     * as link owner, etc. depending on the subclass implementation).
     *
     * @param queryName name of the DataQuery to use
     * @param operationName name of the DataOperation to use
     */
    public void swapWithNext(String queryName, String operationName) {
        swapKeys(true, queryName, operationName, "");
    }

    public void swapWithNext(String queryName, String operationName, String linkListName) {
        swapKeys(true, queryName, operationName, linkListName);
    }

    /**
     * Swaps this Link with the previous Link according to the specified
     * ORDER attribute. No filters are applied to the query, unless
     * getSwapQuery() is overridden, applying relevant filters (such
     * as link owner, etc. depending on the subclass implementation).
     *
     * @param queryName name of the DataQuery to use
     * @param operationName name of the DataOperation to use
     */
    public void swapWithPrevious(String queryName, String operationName) {
        swapKeys(false, queryName, operationName, "");
    }

    public void swapWithPrevious(String queryName, String operationName, String linkListName) {
        swapKeys(false, queryName, operationName, linkListName);
    }

    /**
     * Given a dataquery name, returns the (possibly filtered)
     * DataQuery for use in swapKeys. This implementation does no
     * additional filtering, so if this is required by a specific
     * implementation, this method should be overridden to provide the
     * filtering
     *
     * @param queryName name of the DataQuery to use
     * @return the DataQuery
     */
    protected DataQuery getSwapQuery(String queryName) {
        return SessionManager.getSession().retrieveQuery(queryName);
    }

    /**
     * Given a data operation name, returns the
     * DataOperation for use in swapKeys. This implementation sets the
     * "linkOrder" and "nextLinkOrder" parameters. If any other
     * parameters are needed (such as ownerID, etc.), this method will need to be overridden
     *
     * @param operationName name of the DataOperation to use
     * @return the DataOperation
     */
    protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = SessionManager.getSession().
                retrieveDataOperation(operationName);
        return operation;
    }

    /**
     *  This swaps the sort keys.
     *  @param swapNext This indicates if we are swapping with the next
     *                  or the previous
     *  @param queryName This is used to find the key with which to swap
     */
    protected void swapKeys(boolean swapNext, String queryName,
            String operationName) {
        this.swapKeys(swapNext, queryName, operationName, "");
    }

    protected void swapKeys(boolean swapNext, String queryName,
            String operationName, String linkListName) {

        String methodName = null;
        if (swapNext) {
            methodName = "swapWithNext";
        } else {
            methodName = "swapWithPrevious";
        }

        Assert.isTrue(!isNew(), methodName + " cannot be called on an "
                + "object that is new");

        Integer currentKey = (Integer) get(ORDER);
        // if the current item is not already ordered, alphabetize
        // instead the first time. This is instead of having to deal
        // with an upgrade script.
        if (currentKey == null) {
            alphabetize();
            return;
        }
        Assert.isTrue(currentKey != null, methodName + " cannot be "
                + "called on an object that is not currently in the "
                + "list");

        int key = currentKey.intValue();

        DataQuery query = getSwapQuery(queryName);
        query.setParameter("linkListName", (String) linkListName);

        int otherKey = key;

        if (swapNext) {
            otherKey = key + 1;
            query.addOrder("linkOrder ASC");
            query.addFilter(query.getFilterFactory().greaterThan("linkOrder",
                    currentKey,
                    true));
        } else {
            otherKey = key - 1;
            query.addOrder("linkOrder DESC");
            query.addFilter(query.getFilterFactory().lessThan("linkOrder",
                    currentKey, true));
        }

        if (query.next()) {
            otherKey = ((Integer) query.get("linkOrder")).intValue();
            query.close();
        }

        DataOperation operation = getSwapOperation(operationName);
        operation.setParameter("linkOrder", new Integer(key));
        operation.setParameter("nextLinkOrder", new Integer(otherKey));
        operation.setParameter("linkListName", linkListName);
        operation.execute();

    }

    /**
     * This method is only used for setting initial sort keys for
     * links which exist without them. This is called by swapKeys
     * instead of attempting to swap if the key found is
     * null. Implementations which rely on using swapKeys should
     * define something useful here based on the appropriate subset of
     * Links to operate upon. This implementation simply returns
     * without doing anything useful.
     */
    protected void alphabetize() {
        return;
    }
}
