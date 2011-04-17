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

import com.arsdigita.faq.QAPair;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ListPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import java.math.BigDecimal;

class AdminQuestionView extends SimpleContainer {
    public static final String versionId =
        "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/AdminQuestionView.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private RequestLocal m_question;
    private SingleSelectionModel m_selection;
    private ToggleLink m_editLink;

    public AdminQuestionView(final SingleSelectionModel selection) {
        m_selection = selection;

        ActionLink returnLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.faq.ui.return_to_all_questions").localize());
        returnLink.addActionListener(
                                     new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                             selection.clearSelection(e.getPageState());
                                         }
                                     });
        add(returnLink);

        m_question = new RequestLocal() {
           protected Object initialValue(PageState s) {
              try {
                    QAPair qa = new QAPair 
                       (new BigDecimal ((String) selection.getSelectedKey(s)));
                    qa.assertPrivilege(PrivilegeDescriptor.READ);
                    return qa;
                    } catch (DataObjectNotFoundException e) {
                        return null;
                    }
                }
            };

        Label question = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label l = (Label) e.getTarget();
                    l.setLabel(getQAPair(e.getPageState()).getQuestion());
                }
            });

        Label answer = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label l = (Label) e.getTarget();
                    l.setLabel(getQAPair(e.getPageState())
                               .getAnswer().getHTMLText());
                }
            });

        add(new QAView(question, answer));

        ListPanel linkList = new ListPanel(ListPanel.UNORDERED);

        m_editLink = new ToggleLink("Edit this question");
        linkList.add(m_editLink);

        final ActionLink deleteLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.faq.ui.delete_this_question").localize());
        deleteLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    final QAPair pair = getQAPair(state);
                    if ( pair != null ) {
                      KernelExcursion ex = new KernelExcursion() {
                        protected void excurse() {
                            setEffectiveParty(Kernel.getSystemParty());
                            pair.delete();
                        }
                      };
                      ex.run();
                    }
                    selection.clearSelection(state);

                }
            });
        deleteLink.setConfirmation("This will permanently delete the question."
                                   + " Do you want to do this?");
        linkList.add(deleteLink);
        add(linkList);
    }

    ToggleLink getEditLink() {
        return m_editLink;
    }

    private QAPair getQAPair(PageState state) {
        return (QAPair) m_question.get(state);
    }
}
