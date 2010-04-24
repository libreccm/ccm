/*
 * Copyright (C) 2008 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.navigation.ui.portlet;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.portlet.NavigationTreePortlet;
// @deprecated use com.arsdigita.bebop.portal.PortletConfigFormSection
// import com.arsdigita.london.portal.ui.PortletConfigFormSection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.Portlet;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * Editor for a {@link NavigationTreePortlet}.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class NavigationTreePortletEditor extends PortletConfigFormSection
{
    private static final Logger s_log = Logger.getLogger(NavigationTreePortletEditor.class);

    private SingleSelect m_root;

    private SingleSelect m_depth;

    public NavigationTreePortletEditor(ResourceType resType, RequestLocal parentAppRL)
    {
        super(resType, parentAppRL);
    }

    public NavigationTreePortletEditor(RequestLocal application)
    {
        super(application);
    }

    public void addWidgets()
    {
        super.addWidgets();

        try
        {
            m_root = new SingleSelect(new BigDecimalParameter("navigation"));
            m_root.addPrintListener(new CategoryPrintListener());
        }
        catch (TooManyListenersException ex)
        {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }

        m_depth = new SingleSelect(new IntegerParameter("depth"));
        m_depth.addOption(new Option("1", "1 Level"));
        m_depth.addOption(new Option("2", "2 Levels"));

        add(new Label("Navigation:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_root);

        add(new Label("Depth:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_depth);
    }

    public void initWidgets(PageState state, Portlet portlet) throws FormProcessException
    {
        super.initWidgets(state, portlet);

        if (portlet != null)
        {
            NavigationTreePortlet myportlet = (NavigationTreePortlet) portlet;

            m_root.setValue(state, myportlet.getNavigation().getID());
            m_depth.setValue(state, new Integer(myportlet.getDepth()));
        }
    }

    public void processWidgets(PageState state, Portlet portlet) throws FormProcessException
    {
        super.processWidgets(state, portlet);

        NavigationTreePortlet myportlet = (NavigationTreePortlet) portlet;
        myportlet.setDepth(((Integer) m_depth.getValue(state)).intValue());

        BigDecimal id = (BigDecimal) m_root.getValue(state);
        try
        {
            Navigation root = (Navigation) DomainObjectFactory.newInstance(new OID(Navigation.BASE_DATA_OBJECT_TYPE, id));
            myportlet.setNavigation(root);
        }
        catch (DataObjectNotFoundException ex)
        {
            throw new UncheckedWrapperException("cannot find category", ex);
        }
    }

    protected String getUseContext()
    {
        TemplateContext ctx = Navigation.getContext().getTemplateContext();
        String context = (ctx == null ? null : ctx.getContext());
        return context;
    }

    private class CategoryPrintListener implements PrintListener
    {
        public void prepare(PrintEvent e)
        {
            SingleSelect target = (SingleSelect) e.getTarget();

            DomainCollection navigations = new DomainCollection(SessionManager.getSession().retrieve(
                    Navigation.BASE_DATA_OBJECT_TYPE));
            try
            {
                while (navigations.next())
                {
                    Navigation navigation = (Navigation) navigations.getDomainObject();
                    target.addOption(new Option(navigation.getID().toString(), navigation.getPath()));
                }
            }
            finally
            {
                navigations.close();
            }
        }
    }
}
