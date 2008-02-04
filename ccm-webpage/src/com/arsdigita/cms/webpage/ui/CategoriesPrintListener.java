/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.webpage.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentSection;

/* CategoriesPrintListener displays categories for a given
 * content section.  May be used in conjunction with an
 * item-edit form, so that assigned categories are preselected.
 * 
 * @author Crag Wolfe
 */
public class CategoriesPrintListener implements PrintListener {
	
	private ArrayList options = new ArrayList();

	private static final String SEPARATOR = ">";

	public CategoriesPrintListener(ContentSection section) {
		if (section != null) {
			Category root = section.getRootCategory();
			CategoryCollection children = root.getChildren();
			while (children.next()) {
				Category child = children.getCategory();
				processCategory(child, options, 1);
			}
		}
	}

	public void prepare(PrintEvent e) {
		OptionGroup o = (OptionGroup) e.getTarget();

		for (int i = 0; i < options.size(); i++) {
			o.addOption((Option) options.get(i));
		}
	}

	private void processCategory(Category category, ArrayList o, int depth) {
		String id = category.getID().toString();
		String name = category.getName();
		name = " " + name;

		for (int i = 1; i < depth; i++) {
			name = "------" + name;
		}
		o.add(new Option(id, name));

		CategoryCollection children = category.getChildren();
		while (children.next()) {
			Category child = children.getCategory();
			processCategory(child, o, depth + 1);
		}
	}
}
