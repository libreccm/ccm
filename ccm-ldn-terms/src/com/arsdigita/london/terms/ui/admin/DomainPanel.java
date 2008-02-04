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

package com.arsdigita.london.terms.ui.admin;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.london.util.ui.ActionLink;
import com.arsdigita.london.util.ui.ModalContainer;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.london.terms.Terms;

public class DomainPanel extends ModalContainer {

    private DomainListing m_domainListing;
    private ActionLink m_addDomain;
    private DomainForm m_domainForm;
    private DomainDetails m_domainDetails;
    private DomainUsage m_domainUsage;
    private ActionLink m_addUseContext;
    private DomainContextForm m_domainContextForm;
    private TermPanel m_termPanel;

    private DomainObjectParameter m_domain;

    public static final String MODE_VIEW_DOMAIN = "viewDomain";
    public static final String MODE_CREATE_DOMAIN = "createDomain";
    public static final String MODE_NO_DOMAIN = "noDomain";
    public static final String MODE_EDIT_DOMAIN = "editDomain";
    public static final String MODE_ADD_CONTEXT = "addContext";

    public DomainPanel() {
        super(Terms.XML_PREFIX + ":domainPanel",
              Terms.XML_NS);
        
        m_domain = new DomainObjectParameter("domain");

        m_domainListing = new DomainListing(m_domain);
        m_termPanel = new TermPanel(m_domain);
        m_addDomain = new ActionLink("Create domain");
        m_addDomain.setIdAttr("createDomain");
        m_domainDetails = new DomainDetails(m_domain);
        m_domainForm = new DomainForm("domainForm", m_domain);
        m_domainUsage = new DomainUsage(m_domain);
        m_addUseContext = new ActionLink("Add domain mapping");
        m_addUseContext.setIdAttr("addDomainMapping");
        m_domainContextForm = new DomainContextForm("useContext",
                                                    m_domain);

        m_domainListing.addDomainObjectActionListener(
            DomainListing.ACTION_VIEW,
            new ModeChangeListener(MODE_VIEW_DOMAIN));
        m_domainListing.addDomainObjectActionListener(
            DomainListing.ACTION_VIEW,
            new ModeResetListener(m_termPanel));
        m_addDomain.addActionListener(
            new ModeChangeListener(MODE_CREATE_DOMAIN));
        m_addDomain.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    e.getPageState().setValue(m_domain, null);
                }
            });
        m_domainDetails.addDomainObjectActionListener(
            DomainDetails.ACTION_DELETE,
            new ModeChangeListener(MODE_NO_DOMAIN));
        m_domainDetails.addDomainObjectActionListener(
            DomainDetails.ACTION_EDIT,
            new ModeChangeListener(MODE_EDIT_DOMAIN));
        m_domainForm.addCompletionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    if (state.getValue(m_domain) != null) {
                        setMode(state, MODE_VIEW_DOMAIN);
                    } else {
                        setMode(state, MODE_NO_DOMAIN);
                    }
                }
            });
        m_addUseContext.addActionListener(
            new ModeChangeListener(MODE_ADD_CONTEXT));
        m_domainContextForm.addCompletionListener(
            new ModeChangeListener(MODE_VIEW_DOMAIN));


        add(m_domainListing);
        add(m_addDomain);
        add(m_domainDetails);
        add(m_domainForm);
        add(m_domainUsage);
        add(m_addUseContext);
        add(m_domainContextForm);
        add(m_termPanel);

        registerMode(MODE_NO_DOMAIN,
                     new Component[] { m_domainListing, m_addDomain });
        registerMode(MODE_VIEW_DOMAIN,
                     new Component[] { m_domainListing, m_addDomain, 
                                       m_domainDetails, m_domainUsage, 
                                       m_addUseContext, m_termPanel });
        registerMode(MODE_EDIT_DOMAIN,
                     new Component[] { m_domainListing, m_domainForm });
        registerMode(MODE_CREATE_DOMAIN,
                     new Component[] { m_domainListing, m_domainForm });
        registerMode(MODE_ADD_CONTEXT,
                     new Component[] { m_domainListing, m_domainContextForm });
        
        setDefaultMode(MODE_NO_DOMAIN);
    }

    public void register(Page p) {
        super.register(p);
        
        p.addGlobalStateParam(m_domain);
    }    
}
