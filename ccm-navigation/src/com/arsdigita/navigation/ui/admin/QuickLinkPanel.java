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

package com.arsdigita.navigation.ui.admin;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.navigation.Navigation;

public class QuickLinkPanel extends SimpleContainer {

    private QuickLinkListing m_linkListing;
    private ActionLink m_addLink;
    private QuickLinkForm m_linkForm;

    private ACSObjectSelectionModel m_cat;
    private ACSObjectSelectionModel m_link;
    private StringParameter m_path;

    /**
     * allow simple panel to be used in isolation without the
     * category tree - enables quick link admin for a category to
     * be accessed from that category
     */
    private boolean m_standalone = false;


    public QuickLinkPanel() {
        this(new CategorySelectionModel());
        m_path = new StringParameter("path");
        m_standalone = true;

    }

    public QuickLinkPanel(ACSObjectSelectionModel cat) {
        super(Navigation.NAV_PREFIX + ":quickLinkPanel",
              Navigation.NAV_NS);

        m_cat = cat;
        m_link = new ACSObjectSelectionModel(
            new BigDecimalParameter("link"));

        m_linkListing = new QuickLinkListing(cat,
                                             m_link);
        m_linkListing.addDomainObjectActionListener(
            QuickLinkListing.ACTION_DELETE,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    PageState state = e.getPageState();
                    m_addLink.setVisible(state,
                                         true);
                    m_linkForm.setVisible(state,
                                          false);
                }
            });
        m_linkListing.addDomainObjectActionListener(
            QuickLinkListing.ACTION_EDIT,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    PageState state = e.getPageState();
                    m_addLink.setVisible(state,
                                         false);
                    m_linkForm.setVisible(state,
                                          true);
                }
            });

        m_addLink = new ActionLink("Create link");
        m_addLink.setClassAttr("action");
        m_addLink.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    m_link.clearSelection(state);
                    m_addLink.setVisible(state,
                                         false);
                    m_linkForm.setVisible(state,
                                          true);
                }
            });

        m_linkForm = new QuickLinkForm("linkForm", cat, m_link);
        m_linkForm.addCompletionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    m_link.clearSelection(state);
                    m_addLink.setVisible(state,
                                         true);
                    m_linkForm.setVisible(state,
                                          false);
                }
            });

        add(m_linkListing);
        add(m_addLink);
        add(m_linkForm);
    }

    public void register(Page p) {
        super.register(p);
        if (m_standalone) {
            p.addGlobalStateParam(m_cat.getStateParameter());
            p.addGlobalStateParam(m_path);
        }
        p.addGlobalStateParam(m_link.getStateParameter());
        p.setVisibleDefault(m_linkForm, false);
    }
}
