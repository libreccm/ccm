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

/**
 *  <p>Acts as the
 * go-between for {@link Portal}s and {@link PortalModel}s.  A class
 * implementing this interface, usually an anonymous inner class, is
 * passed into Portal's constructor.  Portal calls
 * PortalModelBuilder's {@link #buildModel} method to get a
 * PortalModel for the current request.  See {@link Portal} for some
 * sample code.</p>
 *
 * @see Portal
 * @see PortalModel
 * @see PortletRenderer
 * @see AbstractPortletRenderer
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: PortalModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface PortalModelBuilder {

    /**
     * Build a {@link PortalModel} for the current request.
     *
     * @param pageState represents the current request.
     * @pre pageState != null
     * @post return != null
     */
    PortalModel buildModel(PageState pageState);
}
