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

package com.arsdigita.atoz.ui.admin;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Component;

public class ItemProviderAdmin extends ProviderAdmin {
    
    private ItemProviderForm m_detailsForm;
    private ActionLink m_editDetails;
    private ProviderDetails m_details;

    private ItemProviderAliasList m_aliasList;
    private ItemProviderAliasForm m_aliasForm;
    private ActionLink m_addAlias;

    public ItemProviderAdmin(ACSObjectSelectionModel provider) {
        super("categoryProviderAdmin", provider);
               
        m_details = new ProviderDetails(provider);
        m_detailsForm = new ItemProviderForm(provider);
        m_editDetails = new ActionLink("Edit details");
        m_editDetails.setIdAttr("edit");

        add(m_details);
        add(m_detailsForm);
        add(m_editDetails);

        m_aliasList = new ItemProviderAliasList(provider);
        m_aliasForm = new ItemProviderAliasForm(provider);
        m_addAlias = new ActionLink("Add item alias");
        m_addAlias.setIdAttr("addAlias");

        add(m_aliasList);
        add(m_aliasForm);
        add(m_addAlias);

        m_editDetails.addActionListener(new ItemProviderEditStart());
        m_detailsForm.addCompletionListener(new ItemProviderEditComplete());

        m_addAlias.addActionListener(new ItemAliasStart());
        m_aliasForm.addCompletionListener(new ItemAliasComplete());
    }

    public void register(Page p) {
        super.register(p);
        
        p.setVisibleDefault(m_detailsForm, false);
        p.setVisibleDefault(m_aliasForm, false);
    }

    private void switchMode(PageState state,
                            Component form,
                            boolean active) {
        form.setVisible(state, active);
                
        m_details.setVisible(state, !active);
        m_editDetails.setVisible(state, !active);

        m_aliasList.setVisible(state, !active);
        m_addAlias.setVisible(state, !active);
    }

    private class ItemProviderEditStart implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            
            switchMode(state, m_detailsForm, true);
        }
    }

    private class ItemProviderEditComplete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_detailsForm, false);
        }
    }

    private class ItemAliasStart implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_aliasForm, true);
        }
    }

    private class ItemAliasComplete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_aliasForm, false);
        }
    }
}
