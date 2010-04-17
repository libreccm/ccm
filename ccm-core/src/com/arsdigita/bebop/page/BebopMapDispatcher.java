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
package com.arsdigita.bebop.page;

import com.arsdigita.bebop.ConfirmPage;
import com.arsdigita.bebop.Page;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.MapDispatcher;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Common base class for a generic URL-to-Bebop-Page dispatching
 * pattern.  This class may be used directly by applications, or it
 * may be subclassed to be coded with a specific map or to override
 * the map lookup for certain sets of URLs.
 *
 * @version $Id: BebopMapDispatcher.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class BebopMapDispatcher extends MapDispatcher {

    private PresentationManager m_presManager;

    private static final Logger s_log =
        Logger.getLogger(BebopMapDispatcher.class);

    /**
     * Creates BebopMapDispatcher with empty URL mapped to "/" and the
     * default presentation manager.  Also maps a Bebop confirmation
     * page to the location at ConfirmPage.CONFIRM_URL.
     */
    public BebopMapDispatcher() {
        super();

        //mount the confirmation page
        Map m = new HashMap();
        m.put(ConfirmPage.CONFIRM_URL, new ConfirmPage());
        setMap(m);
    }

    /**
     * Sets the page map for this dispatcher.
     * @param m the page map (which maps URLs to Pages)
     */
    public synchronized final void setMap(Map m) {
        // hijack the page map and replace Page targets
        // with Dispatcher targets.
        // NOTE: this is *bad* OOP because we have logic code
        // that branches on instanceof.  We only need this method
        // to maintain the current BebopMapDispatcher.setMap() contract.
        Iterator iter = m.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            Object handler = entry.getValue();
            Dispatcher disp;
            if (handler instanceof Page) {
                disp = new PageDispatcher((Page)handler, 
                                          getPresentationManager());
            } else {
                disp = (Dispatcher)handler;
            }
            this.addPage((String)entry.getKey(), disp);
        }
    }

    /**
     * Adds a new URL-page mapping to this dispatcher.
     * @param url the URL to map
     * @param p the page target
     */
    public synchronized final void addPage(String url, Page p) {
        super.addPage(url, new PageDispatcher(p, 
                                              getPresentationManager()));
    }

    /**
     * Sets the default page to display if no page can be found on dispatch
     * for the URL in the page map.
     * @param p the default page
     */
    public final void setNotFoundPage(Page p) {
        setNotFoundDispatcher(new PageDispatcher(p, 
                                                 getPresentationManager()));
    }

    /**
     * Sets the presentation manager used by this dispatcher.
     * @param pm the presentation manager
     */
    public final synchronized void setPresentationManager
        (PresentationManager pm) {

        m_presManager = pm;
        // kludge for #185449:
        // go through map and replace page dispatchers with new p.d.
        // using new p.m.
        Iterator iter = getMap().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            Object obj = entry.getValue();
            if (obj instanceof PageDispatcher) {
                PageDispatcher replacement =
                    new PageDispatcher(((PageDispatcher)obj).getPage(), pm);
                entry.setValue(replacement);
            }
        }
    }

    public final PresentationManager getPresentationManager() {
        if (m_presManager == null) {
            return Templating.getPresentationManager();
        }
        return m_presManager;
    }

    /**
     * <b><font color="red">Expirimental</font></b>  Returns
     * a new SAX event handler specific to configuring a BebopMapDispatcher.
     * The BMD SAX event handler traps "page-class" elements
     * and delegates the rest to the default MapDispatcher event
     * handler.
     * @param md the MapDispatcher to configure
     * @return a SAX DefaultHandler object for handling SAX events.
     */
    protected DefaultHandler newParseConfigHandler(MapDispatcher md) {
        return new BebopConfigHandler(md);
    }

    private static class BebopConfigHandler
        extends MapDispatcher.ParseConfigHandler {

        public BebopConfigHandler(MapDispatcher md) {
            super(md);
        }

        public void endElement(String uri, String localName, String qn) {
            if (qn.equals("page-class")) {
                try {
                    Class pclass = Class.forName(m_buffer.toString().trim());
                    Page p = (Page)pclass.newInstance();
                    m_dispatcher = new PageDispatcher(p);
                } catch (Exception e) {
                    s_log.error("error in parsing config file", e);
                }
            } else {
                super.endElement(uri, localName, qn);
            }
        }
    }
}
