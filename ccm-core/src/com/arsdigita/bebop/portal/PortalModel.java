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

import java.util.Iterator;

/**
 * <p>Defines a model for use by Portal.</p>
 * <p>
 * Portal builds and uses a PortalModel inside its {@link Portal#generateXML} method.
 * </p>
 *
 * @see Portal
 * @see PortalModelBuilder
 * @see PortletRenderer
 * @see AbstractPortletRenderer
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: PortalModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public interface PortalModel {

    /**
     * Get the {@link Portlet}s of this PortalModel.  Users of the
     * {@link Portal} component are meant to implement this method for
     * their particular data source.
     *
     * @return an Iterator over the set of Portlets.
     * @post return != null
     */
    Iterator getPortletRenderers();

    /**
     * Get the title of this Portal.
     *
     * @return the title.
     * @post return != null
     */
    String getTitle();
}
