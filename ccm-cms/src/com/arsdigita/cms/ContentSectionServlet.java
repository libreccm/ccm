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

import com.arsdigita.caching.CacheTable;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.ContentItemDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.TemplateResolver;
import com.arsdigita.cms.publishToFile.LocalRequestPassword;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.ACSObjectCache;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.AbstractTransactionListener;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.versioning.Versions;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Content Section's application servlet
 * 
 * Repaired ItemURLCache to save multilingual items with automatic
 * language negotiation. The cahce now uses the remaining url part
 * and the language concatinated as a hash table key. The delimiter
 * is CACHE_KEY_DELIMITER.
 * 
 * @author unknown
 * @author Sören Bernstein <sbernstein@quasiweb.de>
 */

public class ContentSectionServlet extends BaseApplicationServlet {

    private static final Logger s_log =
        Logger.getLogger(ContentSectionServlet.class);

    /** Stringarray of file name patterns for index files. Should be made
        configurable.                                                        */
    private static final String[] WELCOME_FILES = new String[] {
        "index.jsp", "index.html"
    };

    private ContentItemDispatcher m_disp = new ContentItemDispatcher();
    public static Map s_itemResolverCache = Collections
                                            .synchronizedMap(new HashMap());

    /** cache the content items                                              */
    private static Map s_itemURLCacheMap = null;

    /** The context for previewing items                                      */
    public static final String PREVIEW = "/preview";
    public static final String FILE_SUFFIX = ".jsp";
    public static final String INDEX_FILE = "/index";
    public static final String CONTENT_ITEM =
        "com.arsdigita.cms.dispatcher.item";
    public static final String CONTENT_SECTION =
        "com.arsdigita.cms.dispatcher.section";
    public static final String XML_SUFFIX = ".xml";
    public static final String XML_MODE = "xmlMode";
    public static final String MEDIA_TYPE = "templateContext";

    private static boolean s_cacheItems = true;
    
    private static final String CACHE_KEY_DELIMITER = "%";

    /**
     * Implements the service method of BaseApplicationServlet
     * @see com.arsdigita.web.BaseApplicationServlet#doService
     *      (HttpServletRequest, HttpServletResponse, Application)
     */
    protected void doService( HttpServletRequest sreq, 
                              HttpServletResponse sresp, 
                              Application app)
                   throws ServletException, IOException {

        ContentSection section = (ContentSection) app;

        RequestContext ctx = DispatcherHelper.getRequestContext();
        
        String url = ctx.getRemainingURLPart();  // here SiteNodeRequestContext

        if (s_log.isInfoEnabled()) {
            s_log.info("Resolving item URL " + url);
        }

        //TODO: need to do a check for public page

        final ItemResolver itemResolver = getItemResolver(section);
        final ContentItem item = getItem(section, url, itemResolver);

        if (item != null) {
            
            /* We have to serve an item here                                 */
            String param = sreq.getParameter("transID");

            if (param != null) {
                Session ssn = SessionManager.getSession();
                TransactionContext txn = ssn.getTransactionContext();
                txn.addTransactionListener(new AbstractTransactionListener() {
                    @Override
                    public void beforeCommit(TransactionContext txn) {
                        Assert.fail("uncommittable transaction");
                    }
                });

                Kernel.getContext().getTransaction().setCommitRequested(false);

                BigInteger transID = new BigInteger(param);
                Versions.rollback(item.getOID(), transID);
            }

            serveItem(sreq, sresp, section, item);
            
        } else {
            
            /* We have to deal with a content-section, folder or an other bit*/
            if (s_log.isInfoEnabled()) {
                s_log.info("NOT serving content item");
            }
            sreq.setAttribute(CONTENT_SECTION, section);

            String packageURL = ctx.getPageBase() + 
                ctx.getRemainingURLPart();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Forwarding onto file " + packageURL);
            }
            if (packageURL.endsWith("/")) {
                for (int i = 0 ; i < WELCOME_FILES.length ; i++) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Trying welcome resource " + 
                                    packageURL + WELCOME_FILES[i]);
                    }

                    RequestDispatcher rd = 
                        DispatcherHelper.getRequestContext(sreq).
                        getServletContext().getRequestDispatcher(packageURL + WELCOME_FILES[i]);
                    if (rd != null && 
                        DispatcherHelper.getRequestContext(sreq).
                        getServletContext().getResource(packageURL + WELCOME_FILES[i]) != null) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Got dispatcher " + rd);
                        }
                        sreq = DispatcherHelper.restoreOriginalRequest(sreq);
                        rd.forward(sreq,sresp);
                        return;
                    }
                }
            } 
            RequestDispatcher rd = 
                DispatcherHelper.getRequestContext(sreq).
                getServletContext().getRequestDispatcher(packageURL);
            if (rd != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got dispatcher " + rd);
                }
                sreq = DispatcherHelper.restoreOriginalRequest(sreq);
                rd.forward(sreq,sresp);
            } else {
                sresp.sendError(404, packageURL + " not found on this server.");
            }
        }
    }

    /** 
     * 
     * @param sreq
     * @param sresp
     * @param section
     * @param item
     * @throws ServletException
     * @throws IOException 
     */
    private void serveItem(HttpServletRequest sreq,
                           HttpServletResponse sresp,
                           ContentSection section,
                           ContentItem item)
        throws ServletException, IOException {
        //this is a content item, so use ContentItemDispatcher
        if (s_log.isInfoEnabled()) {
            s_log.info("serving content item");
        }

        RequestContext ctx = DispatcherHelper.getRequestContext();
        String url = ctx.getRemainingURLPart();

        final ItemResolver itemResolver = getItemResolver(section);

        //set the content item in the request
        ACSObjectCache.set(sreq, item);
        sreq.setAttribute(CONTENT_ITEM, item);

        //set the template context
        TemplateResolver templateResolver =
            m_disp.getTemplateResolver(section);
        String templateURL = url;
        if (!templateURL.startsWith("/")) {
            templateURL = "/" + templateURL; 
        }
        if (templateURL.startsWith(PREVIEW)) {
            templateURL = templateURL.substring(PREVIEW.length());
        } 

        String sTemplateContext = itemResolver.getTemplateFromURL(templateURL);
        if (s_log.isDebugEnabled()) {
            s_log.debug("setting template context to " + sTemplateContext);
        }
        templateResolver.setTemplateContext(sTemplateContext, sreq);

        // Work out how long to cache for....
        // We take minimum(default timeout, lifecycle expiry)
        Lifecycle cycle = item.getLifecycle();
        int expires = DispatcherHelper.getDefaultCacheExpiry();
        if (cycle != null) {
            Date endDate = cycle.getEndDate();

            if (endDate != null) {
                int maxAge = (int) ((endDate.getTime() - 
                                     System.currentTimeMillis()) / 1000l);
                if (maxAge < expires) {
                    expires = maxAge;
                }
            }
        }

        // NB, this is not the same as the security check previously
        // We are checking if anyone can access - ie can we allow
        // this page to be publically cached
        if (s_cacheItems && item.isLiveVersion()) {
            SecurityManager sm = new SecurityManager(section);
            if (sm.canAccess
                ((User) null, SecurityManager.PUBLIC_PAGES, item)) {
                DispatcherHelper.cacheForWorld(sresp, expires);
            } else {
                DispatcherHelper.cacheForUser(sresp, expires);
            }
        } else {
            DispatcherHelper.cacheDisable(sresp);
        }

        //use ContentItemDispatcher
        m_disp.dispatch(sreq,sresp,ctx);
    }

    /**
     * Fetches the ItemResolver for a content section. Checks cache first.
     *
     * @param section The content section
     * @return The ItemResolver associated with the content section
     */
    public ItemResolver getItemResolver(ContentSection section) {

        String path = section.getPath();
        ItemResolver ir = (ItemResolver) s_itemResolverCache.get(path);

        if ( ir == null ) {
            ir = section.getItemResolver();
            s_itemResolverCache.put(path, ir);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("using ItemResolver " + ir.getClass().getName());
        }

        return ir;
    }

    public ContentItem getItem(ContentSection section, String url,
                               ItemResolver itemResolver) {

        if (s_log.isDebugEnabled()) {
            s_log.debug("getting item at url " + url);
        }
        HttpServletRequest request = Web.getRequest();

        //first sanitize the url
        if (url.endsWith(XML_SUFFIX)) {
            request.setAttribute(XML_MODE, Boolean.TRUE);
            s_log.debug ("StraightXML Requested");
            url = "/" + url.substring(0, url.length() - XML_SUFFIX.length());
        } else {
            request.setAttribute(XML_MODE, Boolean.FALSE);
            if (url.endsWith(FILE_SUFFIX)) {
                url = "/" + url.substring(0, url.length() - FILE_SUFFIX.length());
            } else if (url.endsWith("/")) {
                url = "/" + url.substring(0, url.length() - 1);
            }
        }

        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        ContentItem item;
        // Check if the user has access to view public or preview pages
        SecurityManager sm = new SecurityManager(section);
        boolean hasPermission = true;

        // If the remaining URL starts with "preview/", then try and
        // preview this item.  Otherwise look for the live item.
        boolean preview = false;
        if (url.startsWith(PREVIEW)) {
            url = url.substring(PREVIEW.length());
            preview = true;
        }

        if (preview) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Trying to get item for PREVIEW");
            }

            item = itemResolver.getItem(section, url, CMSDispatcher.PREVIEW);
            if (item != null) {
                hasPermission = sm.canAccess
                    (request, SecurityManager.PREVIEW_PAGES, item);
            }
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("Trying to get LIVE item");
            }

            //check if this item is in the cache
            //we only cache live items
            if (s_log.isDebugEnabled()) {
                s_log.debug("Trying to get content item for URL " + url +
                            " from cache");
            }
            
            // Get the negotiated locale
            String lang = GlobalizationHelper.getNegotiatedLocale().getLanguage();
            
            item = itemURLCacheGet(section, url, lang);
            item = null;

            if (item == null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug
                        ("Did not find content item in cache, so trying " +
                         "to retrieve and cache...");
                }
                //item not cached, so retreive it and cache it
                item = itemResolver.getItem(section, url, ContentItem.LIVE);
                itemURLCachePut(section, url, lang, item);
            } else if (s_log.isDebugEnabled()) {
                s_log.debug("Found content item in cache");
            }

            if (s_log.isDebugEnabled() && item != null) {
                s_log.debug
                    ("Sanity check: item.getPath() is " + item.getPath());
            }

            if (item != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Content Item is not null");
                }

                hasPermission = sm.canAccess(request,
                                             SecurityManager.PUBLIC_PAGES,
                                             item);

                if (hasPermission) {
                    
                }
            }
        }

        if (item == null && url.endsWith(INDEX_FILE)) {

            if (item == null) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("no item found");
                }
            }

            // look up folder if it's an index
            url = url.substring(0, url.length() - INDEX_FILE.length());
            if (s_log.isInfoEnabled()) {
                s_log.info("Attempting to match folder " + url);
            }
            item = itemResolver.getItem(section, url, ContentItem.LIVE);
            if (item != null) {
                hasPermission = sm.canAccess(request,
                                             SecurityManager.PUBLIC_PAGES,
                                             item);
            }
        }

        if (!hasPermission &&
            !LocalRequestPassword.validLocalRequest(request)) {

            // first, check if the user is logged-in
            // if he isn't, give him a chance to do so...
            Party user = Kernel.getContext().getParty();
            if (user == null) {
                throw new LoginSignal(request);
            }

            throw new AccessDeniedException();
        }

        return item;
    }

    public ContentItem getItem(ContentSection section, String url) {
        ItemResolver itemResolver = getItemResolver(section);

        return getItem(section, url, itemResolver);
    }

    //  synchronize access to the item-url cache

    private static synchronized void itemURLCachePut(ContentSection section, 
                                                     String sURL,
                                                     String lang,
                                                     BigDecimal itemID) {
        
        getItemURLCache(section).put(sURL + CACHE_KEY_DELIMITER + lang, itemID);
    }

    /**
     * Maps the content item to the URL in a cache
     * @param section the content section in which the content item is published
     * @param sURL the URL at which the content item s published
     * @param item the content item at the URL
     */
    public static synchronized void itemURLCachePut(ContentSection section, 
                                                    String sURL,
                                                    String lang,
                                                    ContentItem item) {
        if (sURL == null || item == null) {
            return;
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("adding cached entry for url " + sURL + " and language " + lang);
        }

        itemURLCachePut(section, sURL, lang, item.getID());
    }

    /**
     * Removes the cache entry for the URL, sURL
     * @param section the content section in which to remove the key
     * @param sURL the cache entry key to remove
     */
    public static synchronized void itemURLCacheRemove(ContentSection section, 
                                                       String sURL,
                                                       String lang) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("removing cached entry for url " + sURL + "and language " + lang);
        }
        getItemURLCache(section).remove(sURL + CACHE_KEY_DELIMITER + lang);
    }

    /**
     * Fetches the ContentItem published at that URL from the cache
     * @param section the content section in which the content item is published
     * @param sURL the URL for the item to fetch
     * @return the ContentItem in the cache, or null
     */
    public static ContentItem itemURLCacheGet(ContentSection section, 
                                              final String sURL,
                                              final String lang) {
        final BigDecimal itemID = (BigDecimal) getItemURLCache(section)
                .get(sURL + CACHE_KEY_DELIMITER + lang);

        if (itemID == null) {
            return null;
        } else {
            try {
                return (ContentItem) DomainObjectFactory.newInstance
                    (new OID(ContentItem.BASE_DATA_OBJECT_TYPE, itemID));
            } catch (DataObjectNotFoundException donfe) {
                return null;
            }
        }
    }

    private static synchronized CacheTable getItemURLCache(ContentSection section) {
        Assert.exists(section, ContentSection.class);
        if (s_itemURLCacheMap == null) {
            initializeItemURLCache();
        }
        return (CacheTable) s_itemURLCacheMap.get(section.getPath());
    }

    private static synchronized void initializeItemURLCache() {
        ContentSectionCollection sections = ContentSection.getAllSections();
        s_itemURLCacheMap = new HashMap();
        while (sections.next()) {
            ContentSection section = sections.getContentSection();
            String idStr = section.getID().toString();
            String path = section.getPath();
            CacheTable itemURLCache =
                new CacheTable("ContentSectionServletItemURLCache" + idStr);
            s_itemURLCacheMap.put(path, itemURLCache);
            
        }
    }
}
