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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.categorization.Category;
import com.arsdigita.dispatcher.AccessDeniedException;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CategoryEditForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
final class CategoryEditForm extends BaseCategoryForm {
    public static final String versionId =
        "$Id: CategoryEditForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (CategoryEditForm.class);

    private final CategoryRequestLocal m_category;

    public CategoryEditForm(final CategoryRequestLocal parent,
                            final CategoryRequestLocal category) {
        super("EditCategory", gz("cms.ui.category.edit"), parent);

        m_category = category;

        //m_name.addValidationListener(new NameUniqueListener(null, m_name, NameUniqueListener.NAME_FIELD));
        //m_url.addValidationListener(new NameUniqueListener(null, m_url, NameUniqueListener.URL_FIELD));

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            m_name.setValue(state, category.getName(""));
            m_description.setValue(state, category.getDescription(""));
            m_url.setValue(state, category.getURL(""));
            // this seems anti-intuitive but the question is "can you place
            // items in this category.  If the user says "yes" then the
            // category is not abstract
            if (category.isAbstract()) {
                m_isAbstract.setValue(state, "no");
            } else {
                m_isAbstract.setValue(state, "yes");
            }

            if (category.isEnabled("")) {
                m_isEnabled.setValue(state, "yes");
            } else {
                m_isEnabled.setValue(state, "no");
            }
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            if (category.canEdit()) {
                category.setName((String) m_name.getValue(state));
                category.setDescription((String) m_description.getValue(state));
                category.setURL((String) m_url.getValue(state));
                String isAbstract = (String)m_isAbstract.getValue(state);
                // this seems anti-intuitive but the question is "can you place
                // items in this category.  If the user says "yes" then the
                // category is not abstract
                if ("yes".equals(isAbstract)) {
                    category.setAbstract(false);
                } else if ("no".equals(isAbstract)) {
                    category.setAbstract(true);
                }

                String isEnabled = (String)m_isEnabled.getValue(state);
                if ("yes".equals(isEnabled)) {
                    category.setEnabled(true);
                } else if ("no".equals(isEnabled)) {
                    category.setEnabled(false);
                }

                category.save();
            } else {
                throw new AccessDeniedException();
            }
        }
    }
}
