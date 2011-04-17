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

import com.arsdigita.faq.Faq;

import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;
import com.arsdigita.web.URL;
import com.arsdigita.persistence.DataAssociationCursor;
import java.math.BigDecimal;

public class FaqQuestionsPortletRenderer extends AbstractPortletRenderer {
    public static final String versionId = "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/FaqQuestionsPortletRenderer.java#4 $ by $Author: dennis $, $DateTime: 2004/08/17 23:26:27 $";

    private FaqQuestionsPortlet m_portlet;

    public FaqQuestionsPortletRenderer(FaqQuestionsPortlet
                                       faqQuestionsPortlet) {
        m_portlet = faqQuestionsPortlet;
    }

    protected void generateBodyXML(PageState pageState,
                                   Element parentElement) {
        Faq faq = (Faq) m_portlet.getParentApplication();

        DataAssociationCursor questionAnswerPairs =
            faq.getQAPairs().cursor();

        GridPanel panel = new GridPanel(1);

        String url = URL.getDispatcherPath() + faq.getPrimaryURL();

        while (questionAnswerPairs.next()) {
            String id = ((BigDecimal) questionAnswerPairs.get("id")).toString();
            String question = (String) questionAnswerPairs.get("question");

            // TODO - figure out how to do this with a link.
            // After an hour, I couldn't find a way.

            // the thing which is a problem is setting the fragment

            Label link = new Label("<a href=" + url + "#" + id + ">" +
                                   question + "</a>", false);
            panel.add(link);
        }

        panel.generateXML(pageState, parentElement);
    }
}
