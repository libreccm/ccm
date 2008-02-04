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

import com.arsdigita.cms.CMSExcursion;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.dispatcher.ChainedDispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ACSObjectCache;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.web.LoginSignal;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Dispatches to the JSP
 * or Servlet for rendering a content item.
 *
 * @author Karl Goldstein (karlg@arsdigita.com)
 * @version $Revision: #20 $ $DateTime: 2004/08/17 23:15:09 $
 **/
public class ItemDispatcher implements ChainedDispatcher {

    private static Logger s_log =
        Logger.getLogger(ItemDispatcher.class);

    public static Map s_itemResolverCache =
        Collections.synchronizedMap(new HashMap());
    public static Map s_templateResolverCache =
        Collections.synchronizedMap(new HashMap());

    public static final String FILE_SUFFIX = ".jsp";
    public static final String INDEX_FILE = "/index";
    public static final String TEMPLATE_ROOT =
        "/packages/content-section/templates";
    public static final String DEFAULT_ITEM_TEMPLATE = "/default/item.jsp";
    public static final String DEFAULT_FOLDER_TEMPLATE = "/default/folder.jsp";

    public static final String XML_SUFFIX = ".xml";
    public static final String XML_MODE = "xmlMode";

    private static boolean m_cacheItems = true;

    /**
     * The context for previewing items
     */
    public static final String PREVIEW = "/preview";

    protected ItemXML m_itemXML;

    public ItemDispatcher() {
        super();
        m_itemXML = new ItemXML();
    }

    public static void setCacheItems(boolean value) {
        m_cacheItems = value;
    }

    public int chainedDispatch(final HttpServletRequest request,
                               final HttpServletResponse response,
                               final RequestContext actx)
        throws IOException, ServletException {
        String queryString = request.getQueryString();
        Transaction transaction = null;
        // XXX: add back rollback
        /*if (queryString != null) {
            String transactionID = StringUtils.getParameter
                ("transID", request.getQueryString(), '&');
            if (transactionID != null) {
                try {
                    transaction = 
                        new Transaction(new BigDecimal(transactionID));
                } catch (DataObjectNotFoundException e) {
                    s_log.warn("Unalbe to locate transaction " + transactionID);
                    // this is non-critical so we just continue
                }
            }
            }*/
        String url = actx.getRemainingURLPart();

        s_log.info("Resolving item URL " + url);

        if (url.endsWith(XML_SUFFIX)) {
            request.setAttribute(XML_MODE, Boolean.TRUE);
            s_log.debug ("StraightXML Requested");
            url = "/" + url.substring(0, url.length() - XML_SUFFIX.length());
        } else {
            request.setAttribute(XML_MODE, Boolean.FALSE);
            // it's neither a .jsp or a .xml, thus its an error
            if (url.endsWith(FILE_SUFFIX)) {
                url = "/" + url.substring(0, url.length() - FILE_SUFFIX.length());
            } else if (url.endsWith("/")) {
                url = "/" + url.substring(0, url.length() - 1);
            } else {
                s_log.warn ("Fail: URL doesn't have right suffix.");
                return ChainedDispatcher.DISPATCH_CONTINUE;
            }
        }

        final ContentSection section =
            ContentSectionDispatcher.getContentSection(request);

        final ContentItem item = getItem(section, url);
        if (item == null) {
            s_log.warn("Fail: No live item found matching " + url);
            return ChainedDispatcher.DISPATCH_CONTINUE;
        }

        if (transaction != null) {
            item.rollBackTo(transaction);
        } 

        ACSObjectCache.set(request, item);
        request.setAttribute(ContentSectionDispatcher.CONTENT_ITEM, item);

        s_log.debug("MATCHED " + item.getID());

        // Work out how long to cache for....
        // We take minimum(default timeout, lifecycle expiry)
        Lifecycle cycle = item.getLifecycle();
        int expires = DispatcherHelper.getDefaultCacheExpiry();
        if (cycle != null) {
            Date endDate = cycle.getEndDate();

            if (endDate != null) {
                int maxAge = (int) ( ( endDate.getTime() - System.currentTimeMillis() ) / 1000l );
                if (maxAge < expires) {
                    expires = maxAge;
                }
            }
        }
        // NB, this is not the same as the security check previously
        // We are checking if anyone can access - ie can we allow
        // this page to be publically cached
        if (m_cacheItems && !url.startsWith(PREVIEW)) {
            SecurityManager sm = new SecurityManager(section);
            if (sm.canAccess((User)null, SecurityManager.PUBLIC_PAGES, item))
                DispatcherHelper.cacheForWorld(response, expires);
            else
                DispatcherHelper.cacheForUser(response, expires);
        } else {
            DispatcherHelper.cacheDisable(response);
        }

        if (((Boolean)request.getAttribute(XML_MODE)).booleanValue())  {
            m_itemXML.dispatch(request, response, actx);
            return ChainedDispatcher.DISPATCH_BREAK;
        } else {

            // normal dispatching

            // This part assumes the template is JSP.
            final String templateURL = getTemplateURL(section, item, request, actx);

            s_log.debug("TEMPLATE " + templateURL);

            CMSExcursion excursion = new CMSExcursion() {
                    public void excurse() 
                        throws ServletException, IOException {
                        setContentSection(section);
                        setContentItem(item);
                        
                        DispatcherHelper.setRequestContext(request, actx);
                        DispatcherHelper.forwardRequestByPath(templateURL, request, response);
                    }
                };
            excursion.run();

            return ChainedDispatcher.DISPATCH_BREAK;
        }
    }

    public ContentItem getItem(ContentSection section, String url) {

        ItemResolver itemResolver = getItemResolver(section);
        ContentItem item;
        // Check if the user has access to view public or preview pages
        SecurityManager sm = new SecurityManager(section);
        boolean hasPermission = true;
        HttpServletRequest request = DispatcherHelper.getRequest();

        // If the remaining URL starts with "preview/", then try and
        // preview this item.  Otherwise look for the live item.
        boolean preview = false;
        if ( url.startsWith(PREVIEW) ) {
            url = url.substring(PREVIEW.length());
            preview = true;
        }

        if ( preview ) {
            item = itemResolver.getItem(section, url, ContentItem.DRAFT);
            if (item != null) {
                hasPermission = sm.canAccess(request,
                                             SecurityManager.PREVIEW_PAGES,
                                             item);
            }
        } else {
            item = itemResolver.getItem(section, url, ContentItem.LIVE);
            if (item != null) {
                hasPermission = sm.canAccess(request,
                                             SecurityManager.PUBLIC_PAGES,
                                             item);
            }
        }

        if (item == null && url.endsWith(INDEX_FILE)) {

            // look up folder if it's an index
            url = url.substring(0, url.length() - INDEX_FILE.length());
            s_log.info("Attempting to match folder " + url);
            item = itemResolver.getItem(section, url, ContentItem.LIVE);
            if (item != null) {
                hasPermission = sm.canAccess(request,
                                             SecurityManager.PUBLIC_PAGES,
                                             item);
            }
        }
	// chris.gilbert@westsussex.gov.uk -  if user is not logged in, give them a chance to do that, else show them the door
	Party user = Kernel.getContext().getParty();
	if (!hasPermission && user == null) {
		throw new LoginSignal(request);
	}
        if( !hasPermission )
            throw new com.arsdigita.dispatcher.AccessDeniedException();

        return item;
    }

    /**
     * Fetches the ItemResolver for a content section. Checks cache first.
     *
     * @param section The content section
     * @return The ItemResolver associated with the content section
     */
    public ItemResolver getItemResolver(ContentSection section) {

        String name = section.getName();
        ItemResolver ir = (ItemResolver) s_itemResolverCache.get(name);

        if ( ir == null ) {
            ir = section.getItemResolver();
            s_itemResolverCache.put(name, ir);
        }

        return ir;
    }

    /**
     * Fetches the ItemResolver for a content section. Checks cache first.
     *
     * @param section The content section
     * @return The ItemResolver associated with the content section
     */
    public TemplateResolver getTemplateResolver(ContentSection section) {

        String name = section.getName();
        TemplateResolver ir = (TemplateResolver) s_templateResolverCache.get(name);

        if ( ir == null ) {
            ir = section.getTemplateResolver();
            s_templateResolverCache.put(name, ir);
        }

        return ir;
    }

    /**
     * Fetches the URL of a template for an item. The returned URL is
     * relative to the webapp context.
     */
    public String getTemplateURL(ContentSection section,
                                 ContentItem item,
                                 HttpServletRequest request,
                                 RequestContext actx) {

        String templateURL = getTemplateResolver(section).getTemplate(section, item, request);

        return templateURL;
    }
}
