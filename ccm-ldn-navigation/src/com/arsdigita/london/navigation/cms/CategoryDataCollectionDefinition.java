/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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

package com.arsdigita.london.navigation.cms;

import com.arsdigita.london.navigation.NavigationModel;
import com.arsdigita.categorization.Category;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;

/**
 * Use this to display the items in one specific category,
 * bypassing the navigation model.
 */
public class CategoryDataCollectionDefinition extends CMSDataCollectionDefinition {

    private Category m_category = null;

    public void setCategoryByPID(String pid, String domain) {
	Domain dom = Domain.retrieve(domain);
	Term term = dom.getTerm(pid);
	Category cat = term.getModel();
	setCategory(cat);
    }

    public void setCategory(Category category) {
	m_category = category;
    }

    /** Use the specified category if any; otherwise use the model. */
    protected Category getCategory(NavigationModel model) {
	return (m_category != null) ? m_category : model.getCategory();
    }
}
