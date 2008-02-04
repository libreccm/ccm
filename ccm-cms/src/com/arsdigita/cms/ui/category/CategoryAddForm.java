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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.categorization.Category;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CategoryAddForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
final class CategoryAddForm extends BaseCategoryForm {
    public static final String versionId =
        "$Id: CategoryAddForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (CategoryAddForm.class);

    private final SingleSelectionModel m_model;

    /**
     * Constructor.
     */
    public CategoryAddForm(final CategoryRequestLocal parent,
                           final SingleSelectionModel model) {
        super("AddSubcategories", gz("cms.ui.category.add"), parent);

        m_model = model;

        //m_name.addValidationListener(new NameUniqueListener(null, m_name, NameUniqueListener.NAME_FIELD));
        //m_url.addValidationListener(new NameUniqueListener(null, m_url, NameUniqueListener.URL_FIELD));
	
        addProcessListener(new ProcessListener());
    }

    private final class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            s_log.debug("Adding a category");

            final PageState state = e.getPageState();

            final Category parent = m_parent.getCategory(state);
            final String name = (String) m_name.getValue(state);
            final String description = (String) m_description.getValue(state);
	    final String url = (String) m_url.getValue(state);
	    final String isAbstract = (String) m_isAbstract.getValue(state);

            Assert.assertNotNull(parent, "Category parent");

            if (s_log.isDebugEnabled()) {
                s_log.debug("Using parent category " + parent + " to " +
                            "create new category");
            }

            if (parent.canEdit()) {
                final Category category = new Category(name, description, url);
		// this seems anti-intuitive but the question is "can you place
		// items in this category.  If the user says "yes" then the
		// category is not abstract
		if ("yes".equals(isAbstract)) {
		    category.setAbstract(false);
		} else if ("no".equals(isAbstract)) {
		    category.setAbstract(true);
		}
                category.save(); // XXX this is necessary?

                parent.addChild(category);
                parent.save();

                category.setDefaultParentCategory(parent);
                category.save();

                m_model.setSelectedKey(state, category.getID());
            } else {
                // XXX user a better exception here.
                // PermissionException doesn't work for this case.
                throw new AccessDeniedException();
            }
        }
    }
}
