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
package com.arsdigita.faq.ui;

import com.arsdigita.faq.QAPair;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.dispatcher.DispatcherHelper;
import java.io.IOException;
import java.math.BigDecimal;

class AdminQuestionsPane extends ModalContainer implements ActionListener {
    public static final String versionId =
        "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/AdminQuestionsPane.java#3 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private Container m_questions;
    private ToggleLink m_newLink;
    private AdminQuestionView m_questionView;
    private FaqQuestionEntryForm m_editForm;
    private SingleSelectionModel m_selection;

    public AdminQuestionsPane() {
        m_questions = new SimpleContainer();
        Table questionsTable = new AdminQuestionTable(
                                                      new AdminQuestionTable.QuestionModelBuilder());
        m_selection = questionsTable.getRowSelectionModel();
        m_questions.add(questionsTable);

        m_newLink = new ToggleLink("Add a new question");
        m_questions.add(m_newLink);
        add(m_questions);
        setDefaultComponent(m_questions);

        m_questionView = new AdminQuestionView(m_selection);
        add(m_questionView);

        m_editForm = new FaqQuestionEntryForm();
        m_editForm.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    if (m_selection.isSelected(s)) {
                        m_questionView.getEditLink().setSelected(s, false);
                        setVisibleComponent(s, m_questionView);
                    } else {
                        m_newLink.setSelected(s, false);
                        setVisibleComponent(s, m_questions);
                    }
                }
            });

        add(m_editForm);
    }

    public void register(Page p) {
        super.register(p);
        p.addActionListener(this);
        ((FaqPage) p).setQuestionSelectionModel(m_selection);
    }

    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();

        if (m_selection.isSelected(s)) {
            redirectWhenNotFound(s);

            if (m_questionView.getEditLink().isSelected(s)) {
                setVisibleComponent(s, m_editForm);
            } else {
                setVisibleComponent(s, m_questionView);
            }
        } else if (m_newLink.isSelected(s)) {
            setVisibleComponent(s, m_editForm);
        } else {
            setVisibleComponent(s, m_questions);
        }
    }

    private void redirectWhenNotFound(PageState state) {
        BigDecimal id = new BigDecimal
            ((String) m_selection.getSelectedKey(state));

        try {
            QAPair pair = new QAPair(id);
        } catch (DataObjectNotFoundException nfe) {
            try {
                DispatcherHelper.sendRedirect
                    (state.getRequest(), state.getResponse(),
                     "/error/object-not-found.jsp");
            } catch (IOException ioe) {
                throw new UncheckedWrapperException(ioe);
            }
        }
    }
}
