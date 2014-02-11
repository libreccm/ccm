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
package com.arsdigita.bebop;

import com.arsdigita.xml.Element;

// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;

/**
 * An abstract implementation of {@link Portlet} that captures default behavior for portlets defined
 * by users of the {@link
 * com.arsdigita.bebop.portal.Portal} component.
 *
 * <p>
 * The {@link #generateXML} method in this class provides a default XML dressing around a Portlet.
 * This dressing is used by Portal's stylesheet rules to generate a title and frame around each
 * Portlet. Programmers looking to implement a Portlet should extend this class and override
 * {@link #generateBodyXML}.</p>
 *
 * @see com.arsdigita.bebop.portal.Portal
 * @see com.arsdigita.bebop.portal.PortalModel
 * @see com.arsdigita.bebop.portal.PortalModelBuilder
 * @see Portlet
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: AbstractPortlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class AbstractPortlet implements Portlet, BebopConstants {

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
     * An implementation of {@link Portlet#generateXML} that provides a default Portlet mini-schema.
     * Portlet implementers
     * <em>cannot</em> override this method, as it is meant to give the Portal stylesheet something
     * to grab on to. If you really want to go your own way, implement Portlet instead of extending
     * AbstractPortlet.
     *
     * <blockquote><pre>
     * &lt;bebop:portlet title="A Portlet" cellNumber="1" profile="narrow"&gt;
     *   &lt;!-- XML defined in {@link #generateBodyXML} --&gt;
     * &lt;/bebop:portlet&gt;
     * </pre></blockquote>
     *
     * @param pageState     The PageState representing the current request. Must not be
     *                      <code>null</code>.
     * @param parentElement The Element to which to attach the XML that this method produces. Must
     *                      not be <code>null</code>.
     *
     *
     */
    @Override
    public final void generateXML(final PageState pageState,
                                  final Element parentElement) {
        final Element portletElement = parentElement.newChildElement(BEBOP_PORTLET,
                                                                     Component.BEBOP_XML_NS);
        portletElement.addAttribute("title", getTitle());
        portletElement.addAttribute("cellNumber",
                                    Integer.toString(getCellNumber()));
        portletElement.addAttribute("sortKey",
                                    Integer.toString(getSortKey()));
        portletElement.addAttribute("profile", getProfile());

        generateBodyXML(pageState, portletElement);
    }

    /**
     * Generates XML for the body (not the frame) of this Portlet. It's the primary intention of
     * this class that programmers override this particular method.
     *
     * @param pageState The PageState representing the current request. Must not be 
     * <code>null</code>.
     * @param parentElement The Element to which to attach the XML that this method produces. Must
     * not be <code>null</code>.
     */
    protected abstract void generateBodyXML(PageState pageState,
                                            Element parentElement);

    /**
     * Gets the cell number of this portlet. A cell is one of several distinct regions, often
     * columns, in a portal's layout.
     *
     * @return The cell number of this portlet, or 1 if the cell number is not set.
     */
    public final int getCellNumber() {
        return m_cellNumber;
    }

    /**
     * Sets the cell number of this portlet. A cell is one of several distinct regions, often
     * columns, in a portal's layout.
     *
     * @param cellNumber The cell number of this portlet
     */
    public final void setCellNumber(final int cellNumber) {
        m_cellNumber = cellNumber;
    }

    /**
     * Gets the sort key of this portlet. The sort key is used to order the portlets in a given
     * cell.
     *
     * @return the sort key of this portlet, or 0 if the sort key is not set.
     */
    public final int getSortKey() {
        return m_sortKey;
    }

    /**
     * Sets the sort key of this portlet. The sort key is used to order the portlets in a given
     * cell.
     *
     * @param sortKey the sort key of this portlet
     */
    public final void setSortKey(final int sortKey) {
        m_sortKey = sortKey;
    }

    /**
     * Gets the profile of this portlet, which describes the form factor of this portlet. There are
     * two profiles, wide and narrow.
     *
     * @return the profile of this portlet, or an empty string if the profile is not set.
     *
     * @post return != null
     */
    public final String getProfile() {
        return m_profile;
    }

    /**
     * Sets the profile of this portlet. Profile describes the form factor of this portlet. There
     * are two profiles, wide and narrow. Use {@link #WIDE_PROFILE} or {@link
     * #NARROW_PROFILE} to specify the profile type.
     *
     * @param profile The profile of this portlet. Must not be <code>null</code>.
     *
     */
    public final void setProfile(final String profile) {
        m_profile = profile;
    }

    /**
     * Gets the title of this portlet.
     *
     * @return The title of this portlet, or an empty string if the title is not set. This method
     * never returns <code>null</code>.
     *
     */
    public final String getTitle() {
        return m_title;
    }

    /**
     * Sets the title of this portlet.
     * 
     * @param title The (new) title of this portlet.
     */
    public final void setTitle(final String title) {
        m_title = title;
    }

}
