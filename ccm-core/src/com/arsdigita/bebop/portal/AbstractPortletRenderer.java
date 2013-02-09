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
package com.arsdigita.bebop.portal;

import com.arsdigita.bebop.PageState;
import com.arsdigita.caching.CacheTable;
import com.arsdigita.xml.Element;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;

/**
 * An abstract implementation of {@link PortletRenderer} meant to capture 
 * default behavior for portlets defined by users of the {@link Portal}
 * component. 
 *
 * <p>The {@link #generateXML} method in this class provides a default
 * XML dressing around a portlet. This dressing is used by Portal's
 * stylesheet rules to generate a title and frame around each portlet.
 * Programmers looking to implement a portlet should extend this class
 * and override {@link #generateBodyXML}.</p>
 *
 * @see Portal
 * @see PortalModel
 * @see PortalModelBuilder
 * @see PortletRenderer
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: AbstractPortletRenderer.java 902 2005-09-22 04:57:12Z apevec $
 */
public abstract class AbstractPortletRenderer extends Portlet {
    // PortletRenderer renderer => Element root
    // use a cachetable to enable cached versions to be invalidated across nodes
    private static CacheTable s_cachedXMLMap = new CacheTable("portletXML");

    // Portlet portlet => Date dateCached.  Note that we choose not
    // to use a synchronized map, since a race here wouldn't matter
    // much.
    //
    // cg - no need to use a CacheTable here - if xml for a given key has been invalidated in s_cachedXMLMap
    // then makeXML will be invoked on all nodes individually when they 
    // are next asked to render the portlet and the local date entries overwritten at that time
    // 
    private static Map s_dateCachedMap =
        Collections.synchronizedMap(new HashMap());

    void generateXMLBody(PageState ps, Element parentElement) {
        generateBodyXML(ps, parentElement);
    }

    /**
     * Generate XML for the body, not the frame, of this Portlet.  It's
     * the primary intention of this class that programmers override
     * this particular method.
     *
     * @param pageState the PageState representing the current request.
     * @param parentElement the Element to which to attach the XML
     * this method produces.
     * @pre pageState != null
     * @pre parentElement != null
     */
    protected abstract void generateBodyXML(PageState pageState,
                                            Element parentElement);

    private Element makeXML(PageState state) {
        Element holder = new Element("holder");
        setDateCached(new Date(), state);
        
        generateXMLBody(state, holder);
        generateChildrenXML(state, holder);
        return holder;
    }

    public void generateXML(PageState state, Element parent) {
        String key = getCacheKey(state);
        Element xml;

        if (isDirty(state)) {
            xml = makeXML(state);
            s_cachedXMLMap.put(key, xml);
        }

        xml = (Element) s_cachedXMLMap.get(key);

        // For the case where isDirty always returns false.
        if (xml == null) {
            xml = makeXML(state);
            s_cachedXMLMap.put(key, xml);
        }

        Iterator iter = xml.getChildren().iterator();

        Element wrapper = generateXMLHelper( parent );

        while (iter.hasNext()) {
            wrapper.newChildElement((Element) iter.next());
        }
    }

    /**
     * Invalidate cached version of XML output for this portlet 
     * across all nodes. 
     * 
     * This can be invoked when a portlet is edited on one node eg.
     * 
     * <pre>
     * protected void processWidgets(PageState state, Portlet portlet)
     *        throws FormProcessException {
     * 
     * 
     * ExamplePortlet myportlet = (ExamplePortlet) portlet;
     *        
     * update portlet data 
     *     
     * myportlet.getPortletRenderer().invalidateCachedVersion(state);
     * 
     * }    
     * 
     * </pre>
     *
     * @param state
     */
    public void invalidateCachedVersion(PageState state) {
        s_cachedXMLMap.remove(getCacheKey(state));
    }

    // Can return null.
    public Date getDateCached(PageState state) {
        return (Date) s_dateCachedMap.get(getCacheKey(state));
    }

    // Can take null.
    private void setDateCached(Date dateCached, PageState state) {
        s_dateCachedMap.put(getCacheKey(state), dateCached);
    }

}
