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
 *
 */
package com.arsdigita.ui.sitemap;


import com.arsdigita.ui.util.GlobalizationUtil ; 


import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;

public class SiteMapAdminPane extends SimpleContainer
    implements ActionListener {

    public static final String versionId = "$Id: SiteMapAdminPane.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private SingleSelectionModel m_processes;
    private ToggleLink m_addLink;

    private Label m_noSelection;
    private DisplayActions m_displayActions;
    private Label m_cfgSiteMap;
    public SiteMapAdminPane (SingleSelectionModel m, ToggleLink l) {
        super();
        m_processes = m;
        m_addLink = l;

        m_noSelection = new Label(GlobalizationUtil.globalize("ui.sitemap.h4emselect_sitenode_to_view_detailsemh4"),  false);
        add(m_noSelection);

        m_displayActions = new DisplayActions(m);
        add(m_displayActions);


        //Have this call a class that outputs the config menu...
        m_cfgSiteMap = new Label(GlobalizationUtil.globalize("ui.sitemap.configuration_menu_placeholder"));
        add(m_cfgSiteMap);
    }

    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_noSelection, true);
        p.setVisibleDefault(m_displayActions, false);
        p.setVisibleDefault(m_cfgSiteMap, false);
        p.addActionListener( this );
    }

    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();
        boolean proc = m_processes.isSelected(s);
        boolean add = m_addLink.isSelected(s);

        m_noSelection.setVisible(s, ! ( proc || add ));
        m_displayActions.setVisible(s, proc);
        m_cfgSiteMap.setVisible(s, add);
    }

}
