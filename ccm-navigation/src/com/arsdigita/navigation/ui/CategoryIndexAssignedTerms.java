/*
 * Copyright (C) 2001-2005 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.navigation.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.london.terms.ui.AbstractAssignedTerms;

/**
 * Generates  </terms:assignedTerms> tag in xml output for the IndexItem
 * of the category.
 * Put something like following in your jsp page. 
 * 
 *    <define:component name="assignedTerms"
 *    classname="com.arsdigita.navigation.ui.CategoryIndexAssignedTerms"/>
 *    
 * Used in ccm-navigation/web/templates/ccm-navigation/navigation/default.jsp
 */
public class CategoryIndexAssignedTerms extends AbstractAssignedTerms {

	protected ACSObject getObject(PageState state) {
		ACSObject ao = Navigation.getConfig().getDefaultModel().getObject();
		return ao;
	}
}
