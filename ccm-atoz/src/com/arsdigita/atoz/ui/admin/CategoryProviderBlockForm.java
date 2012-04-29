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

package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.CategoryProvider;
import com.arsdigita.categorization.ui.ObjectCategoryPicker;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;


import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.kernel.ACSObject;

/**
 * 
 * 
 */
public class CategoryProviderBlockForm extends Form {

    private ACSObjectSelectionModel m_provider;

    private ObjectCategoryPicker m_category;
    private SaveCancelSection m_buttons;

    /**
     * 
     * @param provider 
     */
    public CategoryProviderBlockForm(ACSObjectSelectionModel provider) {
        super("categoryBlockForm", new SimpleContainer()); 
        setRedirecting(true);
        
        m_provider = provider;

        m_category = new ObjectCategoryPicker("category") {
                public String getContext(PageState state) {
                    return null;
                }
                public ACSObject getObject(PageState state) {
                    return (ACSObject)m_provider.getSelectedObject(state);
                }
            };
        add(m_category); 

        m_buttons = new SaveCancelSection(new SimpleContainer());
        add(m_buttons);

        addProcessListener(new ProviderProcessListener());
        addSubmissionListener(new ProviderSubmissionListener());
    }
        
    /**
     * 
     */
    private class ProviderSubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            if (m_buttons.getCancelButton().isSelected(state)) {
                fireCompletionEvent(state);
                throw new FormProcessException("cancel hit");
            }
        }
    }

    /**
     * 
     */
    private class ProviderProcessListener implements FormProcessListener {
        public void process(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            CategoryProvider provider = (CategoryProvider)m_provider
                .getSelectedObject(state);

            Category cat = m_category.getCategory(state);
            provider.addBlock(cat);

            fireCompletionEvent(state);
        }
    }
}
