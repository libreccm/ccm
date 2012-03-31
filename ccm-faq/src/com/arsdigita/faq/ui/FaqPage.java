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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.util.MessageType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import com.arsdigita.faq.Faq;
import com.arsdigita.web.Application;
import com.arsdigita.faq.ApplicationAuthenticationListener;
import com.arsdigita.faq.ui.FaqBasePage;

import java.math.BigDecimal;


/**
 * FaqPage is the base Bebop page for the Faq package.
 *
 * It includes a common header, a footer, and a main "content"
 * area. Components are added to the main content area.
 *
 * FaqPage also contains static utility methods used by the
 * throughout the FAQ package.
 *
 * @author <a href="mailto:teadams@arsdigita.com">Tracy Adams</a>
 * @version $Revision: #4 $ $Date: 2004/08/17 $
 * @version $Id: FaqPage.java#4 $
 */

public class FaqPage extends FaqBasePage {

    private static final org.apache.log4j.Logger log =
        org.apache.log4j.Logger.getLogger(FaqPage.class);

    public static final String FAQ_XML_NS = "http://www.arsdigita.com/faq/1.0";

    private SingleSelectionModel m_questionSelection;

    public FaqPage() {
        this("user");
    }

    public FaqPage(String view) {
        super(view);
        if ( view.equals("admin") ) {
            addRequestListener(new ApplicationAuthenticationListener("admin"));
        }
    }

    void setQuestionSelectionModel(SingleSelectionModel selection) {
        m_questionSelection = selection;
    }

    BigDecimal getQuestionID(PageState s) {
        String key = (String) m_questionSelection.getSelectedKey(s);
        if (StringUtils.emptyString(key)) {
            return null;
        } else {
            return new BigDecimal(key);
        }
    }

    /**
     *
     * gets the ID of the selected faq. If no faq is selected,
     * it returns null
     *
     * @return the BigDecimal ID of the faq or null if none is selected
     */

    public BigDecimal getFaqID(PageState state) {
        Faq faq = getFaq(state);
        Assert.exists(faq, "faq");
        return faq.getID();
    }

    public Faq getFaq(PageState state) {
        return (Faq)Application.getCurrentApplication(state.getRequest());
    }

    /** Returns HTML text, converted from the following:
     *  HTML -- returns the input
     *  pre-formatted - returns the input wrapped in <pre> tags
     *  plain - returns the input converted to HTML.
     */

    public static String generateHTMLText(String text, String formatType) {
        if (text == null) {
            return "";
        }
        if (formatType.equals(MessageType.TEXT_HTML)) {
            return text;
        } else if (formatType.equals(MessageType.TEXT_PREFORMATTED)) {
            return "<pre>"+text+"</pre>";
        } else {
            /*format is plain*/
            return StringUtils.textToHtml(text);
        }
    }
}
