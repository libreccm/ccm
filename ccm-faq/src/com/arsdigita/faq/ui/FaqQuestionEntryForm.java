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


import com.arsdigita.faq.util.GlobalizationUtil; 

import com.arsdigita.faq.Faq;
import com.arsdigita.faq.QAPair;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormValidationException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.toolbox.ui.TextTypeWidget;
import com.arsdigita.util.TypedText;

import java.math.BigDecimal;

/**
 * A reusable component for a faq Question add/edit form
 */
public class FaqQuestionEntryForm extends Form
    implements FormInitListener, FormValidationListener,
               FormProcessListener {
    public static final String versionId =
        "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/FaqQuestionEntryForm.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static org.apache.log4j.Logger log
        = org.apache.log4j.Logger.getLogger(Faq.class);

    private RequestLocal m_faqQuestion;

    FaqQuestionEntryForm() {
        super("faqQuestionEntry");

        setMethod("POST");

        add(new Label(GlobalizationUtil.globalize("cw.faq.ui.question")));
        ParameterModel question = new StringParameter("question");
        question.addParameterListener(new NotEmptyValidationListener());
        TextField question_tf = new TextField(question);
        question_tf.setMaxLength(1000);
        add(question_tf);

        add(new Label(GlobalizationUtil.globalize("cw.faq.ui.answer")));

        ParameterModel answer = new StringParameter("answer");
        answer.addParameterListener(new NotEmptyValidationListener());
        answer.addParameterListener(new StringLengthValidationListener(4000));
        add(new TextArea(answer, 4, 40, TextArea.SOFT));

        add(new Label(GlobalizationUtil.globalize("cw.faq.ui.text_type")));
        add(new TextTypeWidget(new StringParameter("answerTextType")));

        add(new Submit("Save"), ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);

        addProcessListener(this);
        addInitListener(this);
        addValidationListener(this);

        m_faqQuestion = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    BigDecimal id =
                        ((FaqPage) state.getPage()).getQuestionID(state);

                    if (id == null) {
                        return null;
                    }

                    try {
                        return new QAPair(id);
                    } catch (DataObjectNotFoundException nfe) {
                        return null;
                    }
                }
            };
    }

    // Can return null, including when the DataObject is not found.
    private QAPair getQAPair(PageState state) {
        QAPair pair = (QAPair) m_faqQuestion.get(state);

        return pair;
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        BigDecimal id = ((FaqPage) state.getPage()).getQuestionID(state);

        if (id != null) {
            QAPair pair = getQAPair(state);

            if (pair != null) {
                data.put("question", pair.getQuestion());
                data.put("answer", pair.getAnswer().getText());
                data.put("answerTextType", pair.getAnswer().getType());
            }
        }
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        QAPair pair = getQAPair(state);

        String name = (String) data.get("question");

        Faq faq = ((FaqPage) state.getPage()).getFaq(state);
        DataAssociationCursor pairs =
            ((DataAssociation) faq.getQAPairs()).cursor();

        Filter f = pairs.addFilter("upper(question) = :name");
        f.set("name", name.toUpperCase());

        BigDecimal id = ((FaqPage) state.getPage()).getQuestionID(state);

        if (id != null) {
            pairs.addNotEqualsFilter("id", id);
        }

        if (pairs.next()) {
            data.addError("question", "This question already exists.");
        }

        pairs.close();
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        BigDecimal id = ((FaqPage) state.getPage()).getQuestionID(state);

        Faq faq = ((FaqPage) state.getPage()).getFaq(state);
        QAPair pair;

        if (id != null) {
            // We are editing. Get the object we are supposed to change.

            pair = getQAPair(state);

            if (pair == null) {
                throw new FormValidationException
                    ("The object you are editing cannot be found in the " +
                     "database.");
            }

            pair.setQuestion((String) data.get("question"));
            pair.setAnswer
                (new TypedText((String) data.get("answer"),
                               (String) data.get("answerTextType")));
        } else {
            // We are adding. Create a new questionAnswerPair.

            pair = faq.createQuestion
                ((String) data.get("question"),
                 new TypedText((String) data.get("answer"),
                               (String) data.get("answerTextType")));
        }

        pair.save();

        fireCompletionEvent(state);
    }
}
