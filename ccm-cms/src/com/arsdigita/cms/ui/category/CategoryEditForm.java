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
 * XXX
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CategoryEditForm.java 2090 2010-04-17 08:04:14Z pboy $
 */
final class CategoryEditForm extends BaseCategoryForm {

    private static final Logger s_log = Logger.getLogger(CategoryEditForm.class);
    private static final String NO = "no";
    private static final String YES = "yes";
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

        @Override
        public final void init(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            // Quasimodo:
            // Modified to ensure that the value is read from Category (and not the
            // localized version). This is necessary because we are in the admin GUI,
            // a localized version would be confusing.
            m_name.setValue(state, category.getName(""));
            m_description.setValue(state, category.getDescription(""));
            m_url.setValue(state, category.getURL(""));
            // this seems anti-intuitive but the question is "can you place
            // items in this category.  If the user says "yes" then the
            // category is not abstract
            if (category.isAbstract()) {
                m_isAbstract.setValue(state, NO);
            } else {
                m_isAbstract.setValue(state, YES);
            }

            if (category.isVisible()) {
                m_isVisible.setValue(state, YES);
            } else {
                m_isVisible.setValue(state, NO);
            }


            if (category.isEnabled("")) {
                m_isEnabled.setValue(state, YES);
            } else {
                m_isEnabled.setValue(state, NO);
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            if (category.canEdit()) {
                category.setName((String) m_name.getValue(state));
                category.setDescription((String) m_description.getValue(state));
                category.setURL((String) m_url.getValue(state));
                
                final String isAbstract = (String) m_isAbstract.getValue(state);
                // this seems anti-intuitive but the question is "can you place
                // items in this category.  If the user says "yes" then the
                // category is not abstract
                if (YES.equals(isAbstract)) {
                    category.setAbstract(false);
                } else if (NO.equals(isAbstract)) {
                    category.setAbstract(true);
                }

                final String isVisible = (String) m_isVisible.getValue(state);
                if (YES.equals(isVisible)) {
                    category.setVisible(true);
                } else {
                    category.setVisible(false);
                }

                final String isEnabled = (String) m_isEnabled.getValue(state);
                if (YES.equals(isEnabled)) {
                    category.setEnabled(true);
                } else if (NO.equals(isEnabled)) {
                    category.setEnabled(false);
                }

                category.save();
            } else {
                throw new AccessDeniedException();
            }
        }

    }
}
