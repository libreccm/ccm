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
package com.arsdigita.cms.dispatcher;


import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.StringTokenizer;

/**
 * <p>This is the default implementation of
 * {@link com.arsdigita.cms.dispatcher.ItemResolver}.</p>
 *
 * <p>The <tt>getItem</tt> method of the default implementation of
 * <tt>ItemResolver</tt>,
 * {@link com.arsdigita.cms.dispatcher.SimpleItemResolver}
 * runs a simple query using the passed in information to retrieve the
 * content item with a name that matches the URL stub, in our example
 * it looks for a content item with name <tt>cheese</tt>. If no such item
 * exists, or if there is such an item, but without a live version, even
 * though one has been requested, <tt>getItem</tt> returns <tt>null</tt>.</p>
 *
 * <p>After the CMS Dispatcher received the content item from the
 * <tt>ItemResolver</tt>, it also asks it for the
 * {@link com.arsdigita.cms.dispatcher.MasterPage} for that item in the
 * current request. With the content item and the master page in hand,
 * the dispatcher calls <tt>service</tt> on the page.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #15 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: SimpleItemResolver.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class SimpleItemResolver extends AbstractItemResolver implements ItemResolver {

    private static final Logger s_log =
        Logger.getLogger(SimpleItemResolver.class.getName());

    private static final String ADMIN_PREFIX = "admin";
    private static final String WORKSPACE_PREFIX = ContentCenter.getURL();    

    private static MasterPage s_masterP = null;

    public SimpleItemResolver() {}

    /**
     * Return a content item based on page state (and content section).
     *
     * @param section The current content section
     * @param url     The section-relative URL
     * @param context The LIVE/DRAFT context (*not* the template use context)
     * @return The content item mapped to the content section and URL, or
     *   null if no such item exists
     */
    public ContentItem getItem(ContentSection section, String url,
                               String context) {

        if (s_log.isDebugEnabled()) {
            s_log.debug("trying to get " + context + " item for url " + url);
        }

        String itemUrl = stripTemplateFromURL(url);

        // getItem fails if called from a JSP template, because the request URL
        // gets replaced with the filesystem path to the JSP (when
        // DispatcherHelper.forwardRequestByPath is called).  To fix this, we check
        // if the item had already been put into the request by the CMSDispatcher
        // (which it usually has) and return it.
        ContentItem reqItem = (ContentItem) DispatcherHelper.getRequest().
            getAttribute("com.arsdigita.cms.dispatcher.item");
        if (reqItem != null) {
            s_log.info("found item in the request, returning it");
            return reqItem;
        }

        //ContentItem item = getCachedItem(section, itemUrl, context);
        //if (item != null) {
        //  return item;
        //}

        Folder rootFolder = section.getRootFolder();
        if ( rootFolder == null ) {
            s_log.info("no root folder found, returning null");
            return null;
        }

        if ( context.equals(ContentItem.LIVE) ) {
            rootFolder = (Folder) rootFolder.getLiveVersion();
            if ( rootFolder == null ) {
                s_log.info("no LIVE version of root folder found, returning null");
                return null;
            }
        } else if ( context.equals(ContentItem.DRAFT) )  {
            // Do nothing ?
        } else {
            throw new RuntimeException(
                                       "getItem: Invalid item resolver context " + context);
        }

        ContentItem item = getItem(itemUrl, rootFolder);
        //if (item != null) {
        //  cacheItem(section, itemUrl, context, item);
        //}

        return item;
    }    

    /**
     * @param state the current page state
     * @return the context of the current URL, such as "live" or "admin"
     */
    public String getCurrentContext(PageState state) {

        String url = state.getRequest().getRequestURI();

        ContentSection section =
            CMS.getContext().getContentSection();

        // If this page is associated with a content section, transform
        // the URL so that it is relative to the content section site node.
        if ( section != null ) {
            String sectionURL = section.getURL();
            if ( url.startsWith(sectionURL) ) {
                url = url.substring(sectionURL.length());
            }
        }

        // remove any template-specific URL components
        // (will only work if they're first in the URL at this point: verify
        url = stripTemplateFromURL(url);

        // Determine if we are under the admin UI.
        if ( url.startsWith(ADMIN_PREFIX) ||
             url.startsWith(WORKSPACE_PREFIX) ) {
            return ContentItem.DRAFT;
        } else {
            return ContentItem.LIVE;
        }
    }

    /**
     * Return the content item at the specified path, or null
     * if no such item exists. The path is interpreted as a series
     * of folders; for example, "/foo/bar/baz" will look for
     * an item named "baz" in a folder named "bar" in a folder named "foo"
     * under the specified root folder.
     *
     * @param url the URL to the item
     * @param rootFolder The root folder where the item search will start
     * @return the item on success, null if no such item exists
     * @pre rootFolder != null
     * @pre url != null
     */
    public ContentItem getItem(String url, Folder rootFolder) {

        StringTokenizer tokenizer = new StringTokenizer(url, "/");
        String name = null;
        Folder oldFolder = null;

        while(rootFolder != null && tokenizer.hasMoreTokens()) {
            name = tokenizer.nextToken();
            oldFolder = rootFolder;
            rootFolder = (Folder)rootFolder.getItem(name, true);
        }

        if(tokenizer.hasMoreTokens()) {
            // failure
            s_log.debug("no more tokens found, returning null");
            return null;
        } else {
            // Get the content item which is the last token
            if (rootFolder != null ) {
                ContentItem indexItem = rootFolder.getIndexItem();
                if (indexItem != null) {
                    s_log.info("returning index item for folder");
                    return indexItem;
                }
            }
            if ( name == null ) {
                s_log.debug("no name found");
                return null;
            }
            return oldFolder.getItem(name, false);
        }
    }

    // Generate the URL for an item in the DRAFT context
    private String generateDraftURL(BigDecimal itemId, ContentSection section) {
        return ContentItemPage.getItemURL(section.getFullPath() + "/", itemId,
                                          ContentItemPage.AUTHORING_TAB);
    }


    // Generate the URL for an item in the LIVE context with a given template context
    private String generateLiveURL(ContentItem item, ContentSection section,
                                   String templateContext) {
        String templateURLFrag =
            (templateContext == null || templateContext.length() == 0) ?
            "" : TEMPLATE_CONTEXT_PREFIX + templateContext + "/";
        return section.getPath() + "/" + templateURLFrag + item.getPath();
    }


    // Generate the preview URL for an item in the DRAFT context
    private String generatePreviewURL(ContentItem item, ContentSection section,
                                      String templateContext) {
        String templateURLFrag =
            (templateContext == null || templateContext.length() == 0) ?
            "" : TEMPLATE_CONTEXT_PREFIX + templateContext + "/";
        StringBuffer url = new StringBuffer();
        url
            .append(section.getPath())
            .append("/" + CMSDispatcher.PREVIEW)
            .append("/")
            .append(templateURLFrag)
            .append(item.getPath());
        return url.toString();
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "LIVE" or "DRAFT"
     * @return The URL of the item
     * @pre context != null
     */
    public String generateItemURL(PageState state, BigDecimal itemId,
                                  String name, ContentSection section,
                                  String context) {
        return generateItemURL(state, itemId, name, section, context, null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @return The URL of the item
     * @pre context != null
     */
    public String generateItemURL(PageState state, BigDecimal itemId,
                                  String name, ContentSection section,
                                  String context, String templateContext) {
        if ( ContentItem.DRAFT.equals(context) ) {
            return generateDraftURL(itemId, section);
        } else if (ContentItem.LIVE.equals(context)) {
            ContentItem item = new ContentItem(itemId);
            return generateLiveURL(item, section, templateContext);
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            ContentItem item = new ContentItem(itemId);
            return generatePreviewURL(item, section, templateContext);
        } else {
            throw new RuntimeException( (String) GlobalizationUtil.globalize("cms.dispatcher.unknown_context").localize() + context);
        }
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "LIVE" or "DRAFT"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (
                                   PageState state, ContentItem item, ContentSection section, String context
                                   ) {
        return generateItemURL(state, item, section, context, null);
    }
    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (
                                   PageState state, ContentItem item, ContentSection section,
                                   String context, String templateContext
                                   ) {
        if (ContentItem.LIVE.equals(context)) {
            return generateLiveURL(item, section, templateContext);
        } else if (ContentItem.DRAFT.equals(context)) {
            return generateDraftURL(item.getID(), section);
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            return generatePreviewURL(item, section, templateContext);
        } else {
            throw new RuntimeException( (String) GlobalizationUtil.globalize("cms.dispatcher.unknown_context").localize() + context);
        }
    }

    /**
     * Return a master page based on page state (and content section).
     *
     * @param item    The content item
     * @param request The HTTP request
     */
    public CMSPage getMasterPage(ContentItem item, HttpServletRequest request)
        throws ServletException {

        if ( s_masterP == null ) {
            s_masterP = new MasterPage();
            s_masterP.init();
        }

        return s_masterP;
    }

}
