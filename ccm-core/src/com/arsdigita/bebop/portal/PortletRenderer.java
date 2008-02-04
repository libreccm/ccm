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
import com.arsdigita.xml.Element;

/**
 *  <p>An interface specifying {@link
 * com.arsdigita.bebop.Component}-like behavior for a PortletRenderer,
 * insofar as XML generation is concerned.  Since a PortletRenderer
 * gets its state only from {@link PortalModel}, it is stateless from
 * the Bebop point of view and does not need Component's state
 * management.  We do still, however, want PortletRenderer to produce
 * XML just as other Components do.</p>
 *
 * <p>The PortletRenderer interface is used in {@link Portal} when it
 * builds a new {@link PortalModel} and fetches a set of portlets.
 * Portal calls {@link #generateXML} on each PortletRenderer
 * returned.</p>
 *
 * <p>Note that implementers of PortletRenderers will ordinarily want
 * to extend {@link AbstractPortletRenderer} since it provides a
 * default XML frame for portlets, one that the Portal stylesheet
 * knows to transform.</p>
 *
 * @see Portal
 * @see PortalModel
 * @see PortalModelBuilder
 * @see AbstractPortletRenderer
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: PortletRenderer.java 902 2005-09-22 04:57:12Z apevec $ */
public interface PortletRenderer {
    public static final String versionId = "$Id: PortletRenderer.java 902 2005-09-22 04:57:12Z apevec $ by $Author: apevec $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Build an XML fragment and attach it to this component's parent.
     * Someone implementing a TimeOfDayPortlet could, for instance,
     * override this method to fetch the time and, say, wrap it in a
     * Bebop {@link com.arsdigita.bebop.Label}.  Note, however, that
     * it is preferable to extend {@link
     * com.arsdigita.bebop.AbstractPortlet} since it provides a
     * default "XML wrapper" for portlets.
     *
     * @param pageState the PageState of the current request.
     * @param parentElement the element to which to attach the XML this
     * method creates.
     * @pre pageState != null
     * @pre parentElement != null
     */
    void generateXML(PageState pageState, Element parentElement);

    /**
     * Developers of portlets may specify the rules that decide if the cached version 
     * of the portlet output is considered to be out of date, and it's attributes must be
     * refreshed from the database. 
     * 
     * Implementing this method 
     * and getCacheKey can save trips to the database to retrieve a particular 
     * portlet's attributes
     * 
     * @param pageState the PageState of the current request.
     * @return true if the portlet must be refreshed, false if cached version is still valid
     */
    boolean isDirty(PageState state);
    /**
     * Key for lookup of cached generated XML. Implementing this method 
     * and isDirty can save trips to the database to retrieve a particular 
     * portlet's attributes.
     * 
     * PageState argument allows stateful portlets to cache particular states. Treat with caution. 
     * For example avoid the condition where all cached states are dirty, but 
     * a check on one state leads to refresh of output for that state, and subsequent checks 
     * for isDirty on other states return false. This can be avoided by ensuring that isDirty is 
     * dependent on the particular state being checked eg - a check on the cached time of the cached xml
     * or by overriding invalidateCachedVersion to discard all cached states
     * 
     * Also, ensure that you don't use the entire state as a key - the container can retrieve state defining information
     * such as a page number or name of visible component from the components and use that as key 
     * 
     * If you are not caching a stateful portlet, ignore the PageState argument.
     * 
     * nb - in this revision, return type has been restricted to String from Object in order that 
     * rendered output may  be cached in a CacheTable in order that cached version may be 
     * invalidated across all nodes if edited on one node
     * 
     * (chris.gilbert@westsussex.gov.uk)
     * 
     * @param pageState the PageState of the current request.
     * @return a key for the cache entry nb - while this is often a string representation of 
     * a portlet id, it is not necessarily so - eg content item portlet cached by item id, so 
     * several portlets may share the same cached XML
     */
    String getCacheKey(PageState state);
    
    /**
     * remove cached version of this portletRenderer's output. 
     * Implementors should ensure that the cached version is invalidated across all nodes
     * for example by using a com.arsdigita.caching.CacheTable for the cache
     * @param state
     */
    void invalidateCachedVersion(PageState state);

}
