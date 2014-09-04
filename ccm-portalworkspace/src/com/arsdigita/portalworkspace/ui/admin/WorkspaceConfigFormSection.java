/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalworkspace.ui.admin;

import java.util.TooManyListenersException;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portalworkspace.PageLayout;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ui.ApplicationConfigFormSection;

// Referenced in Initializer.
// No other referebce found.
// TODO: What happens if omitted?
// (2013-02-10pb)
/**
 *
 *
 */
public class WorkspaceConfigFormSection extends ApplicationConfigFormSection {

    private SingleSelect m_layout;

    public WorkspaceConfigFormSection(ResourceType resType,
                                      RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }

    public WorkspaceConfigFormSection(RequestLocal application) {
        super(application);
    }

    protected void addWidgets() {
        super.addWidgets();

        m_layout = new SingleSelect(new OIDParameter("layout"));
        m_layout.addValidationListener(new NotNullValidationListener());
        try {
            m_layout.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    SingleSelect target = (SingleSelect) ev.getTarget();
                    target.clearOptions();
                    DomainCollection layouts = PageLayout.retrieveAll();
                    layouts.addOrder(PageLayout.TITLE);
                    while (layouts.next()) {
                        PageLayout layout = (PageLayout) layouts
                                .getDomainObject();
                        target.addOption(new Option(layout.getOID().toString(),
                                                    layout.getTitle()));
                    }
                }
            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
        add(new Label("Default Page Layout:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_layout);
    }

    protected void initWidgets(PageState state, Application app)
            throws FormProcessException {
        super.initWidgets(state, app);

        if (app != null) {
            Workspace workspace = (Workspace) app;
            m_layout.setValue(state, workspace.getDefaultLayout().getOID());
        } else {
            m_layout.setValue(state, PageLayout.getDefaultLayout().getOID());
        }
    }

    protected void processWidgets(PageState state, Application app)
            throws FormProcessException {
        super.processWidgets(state, app);

        Workspace workspace = (Workspace) app;

        OID layoutOID = (OID) m_layout.getValue(state);
        PageLayout layout = (PageLayout) DomainObjectFactory
                .newInstance(layoutOID);
        workspace.setDefaultLayout(layout);
    }
}
