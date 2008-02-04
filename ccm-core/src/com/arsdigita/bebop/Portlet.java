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

/**
 *  <p>An interface specifying {@link Component}-like behavior for a
 * Portlet, insofar as XML generation is concerned.  Since a Portlet
 * gets its state only from {@link
 * com.arsdigita.bebop.portal.PortalModel}, it is stateless from the
 * Bebop point of view and does not need Component's state management.
 * We do still, however, want Portlet to produce XML just as other
 * Components do.</p>
 *
 * <p>The Portlet interface is used in {@link com.arsdigita.bebop.portal.Portal} when it builds a
 * new {@link com.arsdigita.bebop.portal.PortalModel} and fetches a set of Portlets.  Portal
 * calls {@link #generateXML} on each Portlet returned.</p>
 *
 * <p>Implementers of Portlets will ordinarily want to
 * extend {@link AbstractPortlet} since it provides a default XML
 * frame for portlets that the Portal stylesheet knows to
 * transform.</p>
 *
 * @see com.arsdigita.bebop.portal.Portal
 * @see com.arsdigita.bebop.portal.PortalModel
 * @see com.arsdigita.bebop.portal.PortalModelBuilder
 * @see AbstractPortlet
 * @author Justin Ross 
 * @author James Parsons 
 * @version $Id: Portlet.java 287 2005-02-22 00:29:02Z sskracic $ */
public interface Portlet {

    public static final String versionId = "$Id: Portlet.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    /**
     * Builds an XML fragment and attaches it to this component's parent.
     * Someone implementing a TimeOfDayPortlet could, for instance,
     * override this method to fetch the time and wrap it in a
     * Bebop {@link Label}.  However, it is preferable to
     * extend {@link AbstractPortlet}, since it provides a default "XML
     * wrapper" for portlets.
     *
     * @param pageState the PageState of the current request
     * @param parentElement the element to which to attach the XML this
     * method creates
     * @pre pageState != null
     * @pre parentElement != null
     */
    void generateXML(PageState pageState, Element parentElement);
}
