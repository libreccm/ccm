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

import java.math.BigDecimal;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.atoz.CategoryProvider;
import com.arsdigita.atoz.ui.AtoZGlobalizationUtil;

/**
 *
 *
 */
public class CategoryProviderAliasForm extends Form {

    private ACSObjectSelectionModel m_provider;

    private TermWidget m_termWidget;

    private TextField m_title;
    private SingleSelect m_letter;
    private SaveCancelSection m_buttons;

    /**
     *
     * @param provider
     */
    public CategoryProviderAliasForm(ACSObjectSelectionModel provider) {
        super("categoryAliasForm", new SimpleContainer());
        setRedirecting(true);

        m_provider = provider;

        m_title = new TextField("title");
        m_title.addValidationListener(new StringInRangeValidationListener(1, 200));
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.setSize(80);

        m_letter = new SingleSelect("letter");
        m_letter.addValidationListener(new NotNullValidationListener());
        m_letter.addOption(new Option(null, "--Select one--"));
        for (int i = 0; i < 26; i++) {
            String letter = new String(new char[]{(char) ((int) 'a' + i)});
            m_letter.addOption(new Option(letter, letter.toUpperCase()));
        }

        add(m_title);
        add(m_letter);

        m_termWidget = new TermWidget(provider);
        m_termWidget.addValidationListener(new NotNullValidationListener());
        add(m_termWidget);

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
                throw new FormProcessException(AtoZGlobalizationUtil.globalize(
                        "atoz.ui.admin.cancel_hit"));
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

            CategoryProvider provider = (CategoryProvider) m_provider
                    .getSelectedObject(state);

            Category cat = new Category(((BigDecimal[]) m_termWidget.getValue(state))[0]);
            String letter = (String) m_letter.getValue(state);
            String title = (String) m_title.getValue(state);

            provider.addAlias(cat, letter, title);

            fireCompletionEvent(state);
        }
    }
}
