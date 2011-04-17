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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SimpleContainer;

public class QAView extends SimpleContainer {

    public static final String versionId = "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/QAView.java#3 $ by $Author: dennis $, $DateTime: 2004/08/17 23:26:27 $";

    public QAView(QAPair qa) {
        this(qa.getQuestion(), qa.getAnswer().getHTMLText());
    }

    public QAView(String question, String answer) {
        this(new Label(question), new Label(answer));
    }

    public QAView(Label question, Label answer) {
        super("faq:question-answer-pair", FaqPage.FAQ_XML_NS);

        question.setClassAttr("question");
        add(question);

        answer.setClassAttr("answer");
        answer.setOutputEscaping(false);
        add(answer);
    }
}
