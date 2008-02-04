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
*/
package com.arsdigita.london.navigation.ui;

import java.util.Iterator;
import java.util.List;


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.util.StringUtils;
import com.arsdigita.xml.Element;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Simple Component to output categories specified as date ordered in configuration.
 * each category also has an order that should be used in stylesheet
 * 
 */
public class DateOrderedCategoryComponent extends SimpleComponent {
	
	
	public void generateXML(PageState state, Element p) {
		Element content = Navigation.newElement("dateOrderCategories");
		exportAttributes(content);
		Iterator it = Navigation.getConfig().getDateOrderedCategories().iterator();
		while(it.hasNext()) {
			Element categoryElement = content.newChildElement(Navigation.newElement("category"));
			String[] category = StringUtils.split((String)it.next(), ':');
			categoryElement.addAttribute("id", category[0]);
			String order = "descending";
			if (category.length > 1) {
				order = category[1];
				
			}
			categoryElement.addAttribute("order", order);
		}
		p.addContent(content);
		
		}


}
