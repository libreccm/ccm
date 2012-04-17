/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.atoz.AtoZItemAlias;
import com.arsdigita.atoz.AtoZItemProvider;
import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

/**
 * 
 * 
 */
public class ItemProviderAliasForm extends Form {

    private ACSObjectSelectionModel m_provider;

    private TextField m_title;
    private SingleSelect m_letter;
    private OptionGroup m_item;
    private SaveCancelSection m_buttons;

    public ItemProviderAliasForm(ACSObjectSelectionModel provider) {
        super("itemAliasForm", new SimpleContainer()); 
        setRedirecting(true);

        m_provider = provider;

        m_title = new TextField("title");
        m_title.addValidationListener(new StringInRangeValidationListener(1, 200));
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.setSize(80);

        m_letter = new SingleSelect("letter");
        m_letter.addValidationListener(new NotNullValidationListener());
        m_letter.addOption(new Option(null, "--Select one--"));
        for (int i = 0 ; i < 26 ; i++) {
            String letter = new String(new char[]{(char)((int)'a' + i)});
            m_letter.addOption(new Option(letter, letter.toUpperCase()));
        }

        m_item = new SingleSelect(new StringParameter("item"));
        try {
            m_item.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent event) {
                        OptionGroup group = (SingleSelect) event.getTarget();
                        PageState state = event.getPageState();
                        boolean valueSet = false;

			Category category = ((AtoZItemProvider) m_provider.getSelectedObject(state))
			    .getCategory();

			CategorizedCollection children = category.getObjects(ContentItem.BASE_DATA_OBJECT_TYPE);
			children.addOrder("name");

                        while (children.next()) {
                            ACSObject item = (ACSObject) children.getDomainObject();

                            if ((item instanceof ContentItem) &&
                                ((ContentItem) item).getVersion().equals(ContentItem.DRAFT)) {

                                group.addOption(new Option(item.getID().toString(),
							   ((ContentItem)item).getName()));
                            }
                        }
                    }
                });
        } catch (TooManyListenersException e) {
            //s_log.error("Error adding init listener to Radio Group", e);
            throw new UncheckedWrapperException(e);
        }

        add(m_title);
        add(m_letter);
        add(m_item); 

        m_buttons = new SaveCancelSection(new SimpleContainer());
        add(m_buttons);

        addProcessListener(new ProviderProcessListener());
        addSubmissionListener(new ProviderSubmissionListener());
    }
        
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

    private class ProviderProcessListener implements FormProcessListener {
        public void process(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            AtoZItemProvider provider = (AtoZItemProvider) m_provider
                .getSelectedObject(state);

            BigDecimal itemId = new BigDecimal(m_item.getValue(state).toString());
	    ContentItem item = new ContentItem(itemId);

            String letter = (String)m_letter.getValue(state);
            String title = (String)m_title.getValue(state);

            //provider.addAlias(item, letter, title);

	    AtoZItemAlias alias = (AtoZItemAlias) Classes.newInstance(AtoZItemAlias.class);
	    alias.setTitle(title);
	    alias.setLetter(letter);
	    alias.setContentItem(item);
	    alias.setAtoZItemProvider(provider);
	    alias.save();

            fireCompletionEvent(state);
        }
    }
}
