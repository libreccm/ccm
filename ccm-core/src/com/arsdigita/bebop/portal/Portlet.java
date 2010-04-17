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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.xml.Element;

/**
 *  <p>A Portlet is the basic
 * unit that Portals are constructed out of.  All children of non-model
 * backed Portals must be Portlets.
 *
 * <p>The {@link #generateXML} method in this class provides a default
 * XML dressing around a portlet. This dressing is used by Portal's
 * stylesheet rules to generate a title and frame around each portlet.
 * Each child of the Portlet will have its XML generated within this
 * title/frame.
 * @see Portal
 *
 * @author Justin Ross
 * @author James Parsons
 * @author Bill Schneider
 * @version $Id: Portlet.java 902 2005-09-22 04:57:12Z apevec $
 */

public class Portlet extends SimpleContainer
    implements PortletRenderer, BebopConstants {

    private String m_title = "";
    private int m_cellNumber = 1;
    private int m_sortKey = 0;
    private String m_profile = "";

    /**
     * The wide profile type.
     */
    public static final String WIDE_PROFILE = "wide";

    /**
     * The narrow profile type.
     */
    public static final String NARROW_PROFILE = "narrow";

    /**
     * Generates the standard portlet XML container element to wrap
     * the portlet contents.
     * @return An XML element
     * <pre>&lt;bebop:portlet title= cellNumber=... sortKey=... profile=.../>
     * </pre>
     */
    Element generateXMLHelper(Element parentElement) {
        Element portletElement = parentElement.newChildElement(
            BEBOP_PORTLET,
            Component.BEBOP_XML_NS);
        portletElement.addAttribute("title", getTitle());
        portletElement.addAttribute("cellNumber",
                                    Integer.toString(getCellNumber()));
        portletElement.addAttribute("sortKey",
                                    Integer.toString(getSortKey()));
        portletElement.addAttribute("profile", getProfile());

        exportAttributes(portletElement);

        return portletElement;
    }

    public void setPortletAttribute(String name, String value) {
        super.setAttribute(name, value);
    }

    /**
     * An implementation of {@link Portlet#generateXML} that provides
     * a default Portlet mini-schema.
     *
     * <blockquote><pre>
     * &lt;bebop:portlet title="A Portlet" cellNumber="1" profile="narrow"&gt;
     *   &lt;!-- XML defined by children --&gt;
     * &lt;/bebop:portlet&gt;
     * </pre></blockquote>
     *
     * @param pageState the PageState representing the current
     * request.
     * @param parentElement the Element to which to attach the XML
     * this method produces.
     * @pre pageState != null
     * @pre parentElement != null
     */
    public void generateXML(PageState pageState,
                            Element parentElement) {
        Element portlet = generateXMLHelper(parentElement);
        generateXMLBody(pageState, portlet);
        generateChildrenXML(pageState, portlet);
    }

    void generateXMLBody(PageState ps, Element parentElement) {
        super.generateXML(ps, parentElement);
    }

    /**
     * Get the cell number of this portlet.  A cell is one of several
     * distinct regions, often columns, in a portal's layout.
     *
     * @return the cell number of this portlet.  If the cell number is
     * not set, returns 1.
     */
    public int getCellNumber() {
        return m_cellNumber;
    }

    /**
     * Set the cell number of this portlet. A cell is one of several
     * distinct regions, often columns, in a portal's layout.
     *
     * @param cellNumber the cell number of this portlet.
     */
    public void setCellNumber(int cellNumber) {
        m_cellNumber = cellNumber;
    }

    /**
     * Get the sort key of this portlet.  The sort key is used to
     * order the portlets in a given cell.
     *
     * @return the sort key of this portlet.  If unset, returns 0;
     */
    public int getSortKey() {
        return m_sortKey;
    }

    /**
     * Set the sort key of this portlet.  The sort key is used to
     * order the portlets in a given cell.
     *
     * @param sortKey the sort key of this portlet.
     */
    public void setSortKey(int sortKey) {
        m_sortKey = sortKey;
    }

    /**
     * Get the profile of this portlet.  Profile describes the form
     * factor of this portlet.  Right now there are two profiles, wide
     * and narrow.
     *
     * @return the profile of this portlet.  If profile is not set,
     * returns empty string.
     * @post return != null
     */
    public String getProfile() {
        return m_profile;
    }

    /**
     * Set the profile of this portlet.  Profile describes the form
     * factor of this portlet.  Right now there are two profiles, wide
     * and narrow.  Use {@link #WIDE_PROFILE} or {@link
     * #NARROW_PROFILE} to specify the profile type.
     *
     * @param profile the profile of this portlet.
     * @pre profile != null
     */
    public void setProfile(String profile) {
        m_profile = profile;
    }

    /**
     * Get the title of this portlet.
     *
     * @return the title of this portlet.  If title is not set, returns
     * empty string.
     * @post return != null
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Set the title of this portlet.
     *
     * @return the title of this portlet.
     * @post return != null
     */
    public void setTitle(String title) {
        m_title = title;
    }

    // For PortletRenderer interface.

    public boolean isDirty(PageState state) {
        return true;
    }

    public String getCacheKey(PageState state) {
        // cannot return null, as CacheTable doesn't like it
        return "";
    }

    /*
     * @see com.arsdigita.bebop.portal.PortletRenderer#invalidateCachedVersion(com.arsdigita.bebop.PageState)
     */
    public void invalidateCachedVersion(PageState state) {
        // to be implemented in the class that implements the cache
    }

}
