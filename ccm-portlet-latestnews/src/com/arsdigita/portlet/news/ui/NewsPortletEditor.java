/*
 * Copyright (C) 2003 - 2004 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.news.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portal.Portlet;

import com.arsdigita.portlet.news.NewsPortlet;

import org.apache.log4j.Logger;


/**
 * Provides configuration pane for NewsPortlet.
 *
 * @author Chris Gilbert (cgyg9330) &lt;chris.gilbert@westsussex.gov.uk&gt;
 * @version $Id: NewsPortletEditor.java 2005/03/07 13:48:49 cgyg9330 Exp $
 */
public class NewsPortletEditor extends PortletConfigFormSection {

    private static final Logger s_log = Logger.getLogger(NewsPortletEditor.class);

    private TextField m_itemCount;


    /**
     * Constructor
     * 
     * @param resType
     * @param parentAppRL 
     */
    public NewsPortletEditor(ResourceType resType, RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }

    /**
     * Constructor
     * 
     * @param application 
     */
    public NewsPortletEditor(RequestLocal application) {
        super(application);
    }


    /**
     * Add widgets to the form containing the configuration options.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

       /* Define the number of news item to display */
        m_itemCount = new TextField(new StringParameter(NewsPortlet.ITEM_COUNT));
        add(new Label("Number of items:", Label.BOLD), ColumnPanel.RIGHT);
        m_itemCount.addValidationListener(new IntegerValidationListener());
        add(m_itemCount);

    }


    /**
     * 
     * @param state
     * @param portlet
     * @throws FormProcessException 
     * 
     * TODO: add validation or set up drop down list with contents of Static folder?
     */
    @Override
    protected void initWidgets(PageState state, Portlet portlet)
                   throws FormProcessException {
        super.initWidgets(state, portlet);

        if (portlet != null) {
            NewsPortlet myportlet = (NewsPortlet) portlet;
            m_itemCount.setValue(state, myportlet.getItemCount()+ "");
        }

    }


    /**
     * 
     * @param state
     * @param portlet
     * @throws FormProcessException 
     */
    @Override
    protected void processWidgets(PageState state, Portlet portlet)
                   throws FormProcessException {
        s_log.debug("START processWidgets");
        super.processWidgets(state, portlet);

        NewsPortlet myportlet = (NewsPortlet) portlet;
        myportlet.setItemCount(new Integer(
                               (String) m_itemCount.getValue(state)).intValue());
        s_log.debug("END processWidgets");
    }

}
