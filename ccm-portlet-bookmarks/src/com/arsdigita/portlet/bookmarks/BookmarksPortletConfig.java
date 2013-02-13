/*
 * Copyright (C) 2003 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.bookmarks;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * Configuration allows links to be access controlled (so homepage admin 
 * can add links that only selected users can see).
 * 
 * @author Chris Gilbert (cgyg9330) &lt;chris.gilbert@westsussex.gov.uk&gt;
 * @version $Id: BookmarksPortletConfig.java 2007/08/08 09:28:26 cgyg9330 $
 */
public class BookmarksPortletConfig extends AbstractConfig {

	private BooleanParameter checkPermissions = new BooleanParameter(
			"uk.gov.westsussex.portlet.bookmarks.checkPermissions",
			Parameter.REQUIRED, new Boolean(false));

	public BookmarksPortletConfig() {

		register(checkPermissions);
		loadInfo();
	}

	public boolean checkPermissions() {
		return ((Boolean) get(checkPermissions)).booleanValue();
	}

}
