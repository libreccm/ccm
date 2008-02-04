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
package com.arsdigita.cms.dispatcher;


/**
 * Resolves the JSP template to use for dispatching an 
 * item. This replaces TemplateResolver since the latter
 * has a useless API.
 * <p>In general, the process for resolving a template involves two
 * steps:</p>
 *
 * <ol>
 *
 *   <li>The template resolver examines specific properties of the
 *   item, the content section, and/or the request itself and selects
 *   an appropriate <em>context</em>.  A context is simply a token
 *   such as "plain" or "fancy".
 *
 *   <li>Based on the selected context, the template resolver
 *   identifies an appropriate template for the item.  This is a
 *   three-step process: (1) the resolver queries for an association
 *   between the item and a specific template for the selected
 *   context; (2) if no such association exists, the resolver queries
 *   the item's content type for a default template to use in the
 *   selected context; (3) if a default template is not found, return
 *   null (at which point the dispatcher should probably give up and
 *   return a 404 error).
 *
 * </ol>
 * @deprecated Included for compatibility with london code. use {@link DefaultTemplateResolver} instead
 */

public class DefaultItemTemplateResolver extends DefaultTemplateResolver {

    
}
