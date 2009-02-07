/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.categorization.Category;
import com.arsdigita.dispatcher.AccessDeniedException;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Generates a form for editing an existing localisation for the given category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form
 * in order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein (quasimodo) quasi@zes.uni-bremen.de
 */
public class CategoryLocalizationEditForm extends CategoryLocalizationForm {
    
    public static final String versionId =
        "$Id: CategoryLocalizationEditForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";
    
    private static final Logger s_log = Logger.getLogger
            (CategoryLocalizationEditForm.class);
    
    private final String m_categoryLocalizationLocale;
    
    /**
     * Creates a new instance of CategoryLocalizationEditForm
     */
    public CategoryLocalizationEditForm(final CategoryRequestLocal category,
            final String locale) {
        
        super("EditCategoryLocalization", gz(
                   "cms.ui.category.localization.edit"), category);
        
        // Speichere Locale ab
        m_categoryLocalizationLocale = locale;
        
        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
        
    }

    /**
     * please add: purpose of this class
     */
    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e)
        throws FormProcessException {
            
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            // Hide Locale-Widget and lock it (read-only)
            m_locale.addOption(new Option(m_categoryLocalizationLocale,
                new Locale(m_categoryLocalizationLocale).getDisplayLanguage()), state);
//            m_locale.setValue(state, m_categoryLocalizationLocale);
//            m_locale.setVisible(state, false);
            m_locale.lock();
            
            m_name.setValue(state, category.getName((String) m_locale.getValue(state)));
            m_description.setValue(state, category.getDescription(
                                            (String) m_locale.getValue(state)));
            m_url.setValue(state, category.getURL((String) m_locale.getValue(state)));

            if (category.isEnabled((String) m_locale.getValue(state))) {
                m_isEnabled.setValue(state, "yes");
            } else {
                m_isEnabled.setValue(state, "no");
            }
        }
    }
    
    /**
     * ##todo: document purpose of this
     */
    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
        throws FormProcessException {
            
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Editing localization for locale " + m_locale +
                            " for category " + category);
            }
            
            if (category.canEdit()) {
                category.setName((String) m_name.getValue(state),
                                 (String) m_locale.getValue(state));
                category.setDescription((String) m_description.getValue(state),
                                        (String) m_locale.getValue(state));
                category.setURL((String) m_url.getValue(state),
                                (String) m_locale.getValue(state));
                category.setEnabled("yes".equals(
                                        (String) m_isEnabled.getValue(state)),
                                            (String) m_locale.getValue(state));
                category.save();
            } else {
                throw new AccessDeniedException();
            }
        }
    }
}
