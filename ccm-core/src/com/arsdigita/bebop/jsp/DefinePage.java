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
package com.arsdigita.bebop.jsp;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SlaveComponent;
import com.arsdigita.caching.CacheTable;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;

/**
 * Defines a Bebop page with JSP tags.  Component tags within the page
 * will add components to the Page.  At the end of the tag, we generate
 * XML output from the Bebop page and render it with the designated
 * PresentationManager.
 * <p>If no presentation manager is supplied, then the output XML
 * document from the page definition is just stored in the
 * "com.arsdigita.xml.Document" request attribute.
 * <p>You can also specify a base class for the Page object defined, using
 * the "pageClass" attribute.
 *
 * <p>Example usage:
 * <pre>&lt;define:page name="p" [title="title"] [pmClass="..."] [pageClass=...]>
 *   ... define components here ...
 * &lt;/bebop:page>
 * </pre>
 *
 * <p><b>Note on Bebop static/dynamic split</b>: You should not assume
 * that any code inside define:page will be executed more than once.
 * The created Page object may be cached.
 */
public class DefinePage extends DefineContainer implements JSPConstants {

    public static final String versionId = "$Id: DefinePage.java 1498 2007-03-19 16:22:15Z apevec $ by $Author: apevec $, $DateTime: 2004/08/16 18:10:38 $";

    private Page m_page;
    private String m_title;
    private String m_application;
    private String m_master;
    private Class m_pageClass;
    private boolean m_cache = true;
    private static final Logger s_log =
        Logger.getLogger(DefinePage.class.getName());

    private static CacheTable s_pageCache = new CacheTable("PageCache");
    // maps URL(/packages/foo/www/asdf.jsp) -> {Page, creation date}
    // should this be a CacheTable or not?
    private static Map s_pageLocks = new HashMap();


  

    /**
     * Creates a Bebop Page instance.  A page tag is a special case
     * because we don't expect it to have a parent tag.
     */
    public int doStartTag() throws JspException {
        if (m_cache) {
            String cacheKey = DispatcherHelper.getCurrentResourcePath
                ((HttpServletRequest)pageContext.getRequest());
            Object pageLock;
            // First off all we have the global synchronization
            // block to get the page's unique sync object.
            synchronized (s_pageLocks) {
                pageLock = s_pageLocks.get(cacheKey);
                if (pageLock == null) {
                    pageLock = cacheKey;
                    s_pageLocks.put(cacheKey, cacheKey);
                }
            }
            // Now we just synchronize against our specific page.
            synchronized (pageLock) {
            	
            	CachedPage cached = (CachedPage)s_pageCache.get(cacheKey);
                if (cached != null) {
					Object[] pair = cached.getPageTimeStampPair();
                
                    long pageDate = ((Long)pair[1]).longValue();
                    File jspFile = new File(pageContext.getServletContext()
                                            .getRealPath(cacheKey));
                    if (jspFile.lastModified() <= pageDate) {
                        // jsp file is not newer than cached page,
                        // and page hasn't been marked as dirty by anyone so we can use the cached page.

                        Page page = (Page)pair[0];

                        // We may have to for the page to be locked
                        // by another thread
                        while (!page.isLocked()) {
                            if (s_log.isDebugEnabled()) {
                                s_log.debug(Thread.currentThread().getName() + " waiting on " + cacheKey);
                            }
                            try {
                                pageLock.wait(500);
                            } catch (InterruptedException ex) {
                                if (s_log.isDebugEnabled()) {
                                    s_log.debug(Thread.currentThread().getName() + " waiting interrupted on " + cacheKey, ex);
                                }
                                continue;
                            }
                        }
                    }
                    m_page = (Page)pair[0];
                    pageContext.setAttribute(getName(), m_page);
                    return SKIP_BODY;
                }



                m_page = buildPage();
              //  s_pageCache.put(cacheKey,
                 //               new CachedPage(new Object[] {m_page,
                //                              new Long(System.currentTimeMillis())}));


            }
        } else {
            m_page = buildPage();
        }

        return EVAL_BODY_BUFFERED;
    }

    /**
     * pass in resource URL (eg by using DispatcherHelper.getCurrentResourcePath())
     * and page will be rebuilt next time it is requested.
     * @param resourceURL URL for page eg /packages/foo/www/asdf.jsp
     * 
     * 
     */
    public static void invalidatePage (String resourceURL) {
    	s_log.debug("page - " + resourceURL + " removed from cache");
        s_pageCache.remove(resourceURL);
    }


    private Page buildPage() throws JspException {
        Page page;

        if (m_pageClass == null && m_application == null) {
            m_pageClass = Page.class;
        }

        if (m_pageClass == null) {
            page = PageFactory.buildPage(m_application,
                                           m_title,
                                           getName());
        } else {
            try {
                page = (Page)m_pageClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new JspWrapperException("cannot create page class instance", e);
            } catch (InstantiationException e) {
                throw new JspWrapperException("cannot create page class instance", e);
            }
            if (m_title != null) {
                page.setTitle(m_title);
            }
        }
        pageContext.setAttribute(getName(), page);

        // we *might* be nested, but probably aren't
        Tag t = getParent();
        if (t != null && t instanceof DefineContainer) {
            ((BodyTag)t).doAfterBody();
        }

        return page;
    }

    /**
     * Locks the Page object, generates XML from the Page,
     * gets a presentation manager instance, and renders the XML using
     * the XSLT transformation in the PresentationManager.
     *
     * <p> Nested pages are special cases; if this define:page is nested
     * in another define:page tag, then we won't generate output from
     * this Page directly but we'll make a reference from the parent
     * Page into this page.  Master/slave works similarly; we pass
     * the current page object to the master JSP file, which will include
     * the current page as the slave page to include at the point where
     * &lt;define:slave/> appears.
     *
     */
    public int doEndTag() throws JspException {
        try {
            if (m_cache) {
                String cacheKey = DispatcherHelper.getCurrentResourcePath
                    ((HttpServletRequest)pageContext.getRequest());
                Object pageLock;
                // Global lock to get hold of page sync object
                synchronized (s_pageLocks) {
                    pageLock = s_pageLocks.get(cacheKey);
                    if (pageLock == null) {
                        pageLock = cacheKey;
                        s_pageLocks.put(cacheKey, cacheKey);
                    }
                }
                // Now sync just on this page
                synchronized(pageLock) {
                    if (!m_page.isLocked()) {
                        m_page.lock();
						CachedPage cached = (CachedPage)s_pageCache.get(cacheKey);
                		// cache page in end tag because hashcode of cached page relies on 
                        // components that are added between start and end tag
                        if (cached == null) {
							s_pageCache.put(cacheKey,
											                new CachedPage(new Object[] {m_page,
											                              new Long(System.currentTimeMillis())}));
                		}
                        // Now notify anyone waiting for us to lock the page
                        // that we're all done. see doStartTag()
                        if (s_log.isDebugEnabled()) {
                            s_log.debug(Thread.currentThread().getName() + " waking everyone up on " + cacheKey);
                        }
                        pageLock.notifyAll();
                    }
                }
            } else {
                if (!m_page.isLocked()) {
                    m_page.lock();
                }
            }
            Tag t = getParent();
            if (t != null && t instanceof DefineContainer) {
                // nested...
                // treat like a slave component, a page nested
                // within another page
                ((DefineContainer)t).addComponent(new SlaveComponent(m_page));
                // if we're nested
            } else if (m_master != null) {
                // if we have a master page, then we pass control to it,
                // with the current (m_page) as a request attribute.
                pageContext.getRequest()
                    .setAttribute("com.arsdigita.bebop.SlavePage", m_page);
                DispatcherHelper.forwardRequestByPath(m_master, pageContext);
            } else {
                // pass on the document to the ShowAll code. Note ugly
                // long-range code dependency voodoo here.
                HttpServletRequest req =
                    (HttpServletRequest)pageContext.getRequest();
                HttpServletResponse resp =
                    (HttpServletResponse)pageContext.getResponse();

                Document doc;
                PageState state;

                try {
                    doc = new Document ();
                    state = m_page.process (req, resp);
                }
                catch (ParserConfigurationException ex) {
                    throw new UncheckedWrapperException (ex);
                }
                catch (ServletException ex) {
                    throw new UncheckedWrapperException (ex);
                }
                m_page.generateXML (state, doc);

                req.setAttribute(INPUT_DOC_ATTRIBUTE, doc);
                pageContext.setAttribute(INPUT_PAGE_STATE_ATTRIBUTE, state);
            }
            return EVAL_PAGE;
        } catch (Exception e) {
            try {
                // try to serve <%@ page errorPage=... %>
                pageContext.handlePageException(e);
            } catch (Throwable nested) {
                // serving error page failed, so
                // percolate error up to top-level
                throw new UncheckedWrapperException(e);
            }
            return SKIP_PAGE;
        }
    }

    protected final Component getComponent() {
        return m_page;
    }

    public final void setTitle(String s) {
        m_title = s;
    }

    public final void setApplication(String s) {
        m_application = s;
    }

    public void setPageClass(String s) throws JspException {
        try {
            m_pageClass = Class.forName(s);
        } catch (ClassNotFoundException e) {
            throw new JspException(e.toString());
        }
    }

    public final void setMaster(String s) {
        m_master = s;
    }

    public void setCache(String s) {
        m_cache = new Boolean(s).booleanValue();
    }
}
