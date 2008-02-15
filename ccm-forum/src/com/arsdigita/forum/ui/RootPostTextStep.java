/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum.ui;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.forum.Post;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * root post manages the category(topic) in 
 * addition to subject & body 
 */
public class RootPostTextStep extends PostTextStep {

	private static Logger s_log = Logger.getLogger(RootPostTextStep.class);
	private CategoryWidget m_category;

	public RootPostTextStep(ACSObjectSelectionModel post, PostForm container) {
		super(post, container);

		m_category = new CategoryWidget(new BigDecimalParameter("postTopic"));
		add(m_category);

	}

	public void setText(Post post, PageState state) {
		super.setText(post, state);
		post.clearCategories();
		// need to save post after setting mandatory fields
		// (subject, body, status) and before mapping categories
		 
		post.save();
		BigDecimal categoryID = (BigDecimal) m_category.getValue(state);
		if (categoryID != null && !categoryID.equals(Constants.TOPIC_NONE)) {
			post.mapCategory(new Category(categoryID));
		}

	}

	protected void initWidgets(PageState state, Post post) {
		super.initWidgets(state, post);
		if (post != null) {
			CategoryCollection categories = post.getCategories();
			if (categories.next()) {
				Category cat = categories.getCategory();
				m_category.setValue(state, cat.getID());
			} else {
				m_category.setValue(state, Constants.TOPIC_NONE);
			}
			categories.close();
		} else {
			m_category.setValue(state, null);
		}

	}

}
