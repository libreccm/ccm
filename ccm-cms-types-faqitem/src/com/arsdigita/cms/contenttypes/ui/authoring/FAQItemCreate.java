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
package com.arsdigita.cms.contenttypes.ui.authoring;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.contenttypes.FAQItem;
import com.arsdigita.cms.contenttypes.util.FAQGlobalizationUtil;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.ui.authoring.CreationSelector;


/**
 * A page that will create a new FAQItem. It overwrites the default PageCreate 
 * class to to customize the creation screen, and include 2 additional widgets 
 * (question and answer).
 * 
 * @author Dirk Gomez
 * @see com.arsdigita.intranet.cms.FAQItem
 * @version $Revision: #5 $
 */
public class FAQItemCreate extends PageCreate {

    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";

    private CreationSelector m_parent;

    /**
     * Constructor initializes the form by delegating to parent class.
     * 
     * @param itemModel
     * @param parent 
     */
    public FAQItemCreate(ItemSelectionModel itemModel,
                         CreationSelector parent) {

        super(itemModel, parent);
        m_parent = parent;
    }

    /**
     * Add form-specific widgets by overwriting parent class method.
     */
    @Override
    protected void addWidgets() {
        
        super.addWidgets();
        
        TextArea question = new TextArea(QUESTION);
        question.addValidationListener(new NotNullValidationListener());
        question.setCols(40);
        question.setRows(5);

        add(new Label(FAQGlobalizationUtil
                      .globalize("cms.contenttypes.ui.faq.question")));
        add(question);

        TextArea answer = new TextArea(ANSWER);
        answer.addValidationListener(new NotNullValidationListener());
        answer.setCols(40);
        answer.setRows(5);

        add(new Label(FAQGlobalizationUtil
                      .globalize("cms.contenttypes.ui.faq.answer")));
        add(answer);
    }

    /**
     * 
     * @param e
     * @throws FormProcessException 
     */
    @Override
    public void process(FormSectionEvent e) throws FormProcessException {

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        ItemSelectionModel m = getItemSelectionModel();

        // Try to get the content section from the state parameter
        Folder f = m_parent.getFolder(state);
        ContentSection sec = m_parent.getContentSection(state);
        FAQItem item = (FAQItem)createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));        
        item.setName((String)data.get(NAME));
        item.setTitle((String)data.get(TITLE));
        item.setQuestion((String)data.get(QUESTION));
        item.setAnswer((String)data.get(ANSWER));
        item.save();

        final ContentBundle bundle = new ContentBundle(item);
        bundle.setParent(f);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();
        
        // Apply default workflow
        getWorkflowSection().applyWorkflow(state, item);
        
        // Start edititng the component right away
        m_parent.editItem(state, item);
    }
}








